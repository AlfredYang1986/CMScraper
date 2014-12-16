package Handler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import DAO.ScraperCache
import com.mongodb.casbah.Imports._
import Scraper.ItemNode
import Handler.categoryMapping.PriceLineMapping
import Application.ScraperApp

class PriceLineHandler extends PageHandler_2 {
	def name = "Price Line Mother and Baby"
    def apply(node : ItemNode, host : String) = {
        val url = node.url
        ScraperApp.printer.writeLine("paser item begin ...", name)
       
        val html = Jsoup.connect(url).timeout(0).get
    
        val builder = MongoDBObject.newBuilder
        /**
         * 1. get brand
         */
        val tmp = html.select("div.product-brand-logo > img")
        val brand = tmp.attr("title")
        ScraperApp.printer.writeLine(brand, name)
        builder += "brand" -> brand
        
        /**
         * 2. get name
         */
        val proName = html.select("div.product-name-size").text
        ScraperApp.printer.writeLine(proName, name)
        builder += "name" -> proName
       
        /**
         * 1.1 get category
         */
        val catArr = html.select("div.b2c_breadcump > a")
        val catName = catArr.get(catArr.size - 2).text
        ScraperApp.printer.writeLine(catName, name)
        builder += "cat" -> PriceLineMapping(catName, proName)
        
        /**
         * 3. get image url
         */
        try {
            val imgContent = html.select("div.product-image-container > a > img").first
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
        price_builder += "source" -> "Price Line"
        price_builder += "oriCat" -> catName
        val price_cur = html.select("div.basket-right-price").text
        price_builder += "current_price" -> price_cur 

        try {
            val price_ori = html.select("span.ProductPage_NormalSRP").first.text
            price_builder += "isOnSale" -> true
            price_builder += "ori_price" -> price_ori
        } catch {
          case _ => { ScraperApp.printer.writeLine("not on sale", name); price_builder += "isOnSale" -> false }
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
