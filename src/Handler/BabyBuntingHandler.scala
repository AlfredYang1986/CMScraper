package Handler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import DAO.ScraperCache
import com.mongodb.casbah.Imports._
import Application.BrandList
import Scraper.ItemNode
import Handler.categoryMapping.BabyBuningMapping
import Application.ScraperApp
import Application.JSoapConnectionManager

class BabyBuntingHandler extends PageHandler_2 {
	def name = "Baby Bunting"
	def apply(node : ItemNode, host : String) : Unit = {
        val url = node.url
        
        ScraperApp.printer.writeLine("paser item begin ...", name)
        
        val html : Document = JSoapConnectionManager(url)
        if (html == null) return
    
        val builder = MongoDBObject.newBuilder
        
        try {
               
        /**
         * 2. get product name
         */
        val proName = html.select("div.page-title > h1").first.text
        ScraperApp.printer.writeLine(proName, name)
        builder += "name" -> proName
        
        /**
         * 1. get brand
         */
        val can = BrandList.brands.find(x => proName.toLowerCase.startsWith(x.name.toLowerCase))
        if (can.isEmpty) builder += "brand" -> "Unknown"
        else builder += "brand" -> can.get.name
        
        /**
         * 1.1 get cat
         */
		builder += "cat" -> BabyBuningMapping(node.other.asInstanceOf[String], "")
        
        /**
         * 3. get image url
         */
        try {
          val imgContent = html.select("div.product-img-box > a > img").first
          val imgUrl = imgContent.attr("src")
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
        price_builder += "source" -> "Baby Bunting"
        price_builder += "oriCat" -> node.other.asInstanceOf[String]
        
        try {
          val price_ori = html.select("p.old-price > span").first.text
          price_builder += "isOnSale" -> true
          price_builder += "ori_price" -> price_ori
          ScraperApp.printer.writeLine(price_ori, name)
          
          val price_cur = html.select("p.special-price > span.price > amasty_seo").last.text
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
        
        } catch {
          case _ => return
        }
       
        ScraperApp.printer.writeLine("paser item end ...", name)
    }
}
