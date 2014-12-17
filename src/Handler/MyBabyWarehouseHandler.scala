package Handler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import DAO.ScraperCache
import com.mongodb.casbah.Imports._
import Scraper.ItemNode
import Application.JSoapConnectionManager
import Handler.categoryMapping.MyBabyWarehouseMapping
import Application.ScraperApp

class MyBabyWarehouseHandler extends PageHandler_2 {
	def name = "My Baby Warehouse"
    def apply(node : ItemNode, host : String) : Unit = {
        val url = node.url
        val can_cat = node.other.asInstanceOf[String]
        ScraperApp.printer.writeLine("paser item begin ...", name)
        ScraperApp.printer.writeLine(url, name)
        ScraperApp.printer.writeLine(can_cat, name)

        val html = JSoapConnectionManager(url)
        if (html == null) return
    
        val builder = MongoDBObject.newBuilder
        /**
         * 1. get brand
         */
        val tmpBrand = html.select("#product-attribute-specs-table > tbody > tr").get(1).children().select("td").last.text
        ScraperApp.printer.writeLine(tmpBrand, name)
        builder += "brand" -> html.select("#product-attribute-specs-table > tbody > tr").get(1).children().select("td").last.text
         
        /**
         * 2. get product name
         */
        val proName = html.select("div.product-name > span").first.text
        ScraperApp.printer.writeLine(proName, name)
        builder += "name" -> proName       
        
        /**
         * 1.1 get categories
         */
        val tmpCategory = html.select("#site-container > div > div > div > ul > li > a").last.text
        ScraperApp.printer.writeLine(tmpCategory, name)
        builder += "cat" -> MyBabyWarehouseMapping(tmpCategory, proName)
        
        /**
         * 3. get image url
         */
        try {
          val imgContent = html.select("p.product-image > a").first
          val imgUrl = imgContent.attr("href")
          ScraperApp.printer.writeLine(imgUrl, name)   
          builder += "imgUrl" -> imgUrl
          builder += "StoreOnly" -> false 
      
        } catch {
          case _ => { ScraperApp.printer.writeLine("item only in store", name); builder += "StoreOnly" -> true }
        }
        
        /**
         * 4. get price
         */
        val price_builder = MongoDBObject.newBuilder
        price_builder += "source" -> "My Baby Warehouse"
        price_builder += "oriCat" -> tmpCategory
        
        try {
          val price_ori = html.select("p.old-price > span.price").text
          price_builder += "isOnSale" -> true
          price_builder += "ori_price" -> price_ori
          ScraperApp.printer.writeLine(price_ori, name)
          
          val price_cur = html.select("p.special-price > span.price").text
          price_builder += "current_price" -> price_cur 
          ScraperApp.printer.writeLine(price_cur, name)
 
        } catch {
            case _ => { 
              val price_cur = html.select("span.regular-price > span > amasty_seo").last.text
              price_builder += "current_price" -> price_cur 
              ScraperApp.printer.writeLine(price_cur, name)
              ScraperApp.printer.writeLine("not on sale", name); price_builder += "isOnSale" -> false
            }
        }
        
        /**
         * 5. put price in source list
         */
        val source_list = MongoDBList.newBuilder
        source_list += price_builder.result

        builder += "prices" -> source_list.result
    
        /**
         * 6. save to database
         */
        ScraperCache ++ builder.result
       
        ScraperApp.printer.writeLine("paser item end ...", name)
    }
}
