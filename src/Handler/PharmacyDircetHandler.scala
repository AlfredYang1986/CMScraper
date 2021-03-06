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
import Application.ScraperApp
import Application.JSoapConnectionManager

class PharmacyDircetHandler extends PageHandler_2 {
	def name = "Pharmacy Direct Mother and Baby"
    def apply(node : ItemNode, host : String) : Unit = {
        val url = node.url
        ScraperApp.printer.writeLine("paser item begin ...", name)
      
        val html = JSoapConnectionManager(url)
        if (html == null) return 
    
        val builder = MongoDBObject.newBuilder
        /**
         * 2. get name
         */
        val proName = html.select("div.product-details-layout-2-right > div > h2").text
        ScraperApp.printer.writeLine(proName, name)
        builder += "name" -> proName
        
        /**
         * 1. get brand
         */
        val can = BrandList.brands.find(x => proName.toLowerCase.startsWith(x.name.toLowerCase))
        if (can.isEmpty) builder += "brand" -> "Unknown"
        else builder += "brand" -> can.get.name

        /**
		 * 1.1 get what it for category
		 */
        val tmp = node.other.asInstanceOf[String]
        ScraperApp.printer.writeLine(tmp, name)
        builder += "cat" -> tmp
        
        
        /**
         * 3. get image url
         */
        try {
            val imgContent = html.select("span.image-wrapper > a > img").first
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
        val price_cur = html.select("div.order-desc > h2").text
        price_builder += "current_price" -> price_cur 

        try {
            val price_ori = html.select("div.order-rrp > span").first.text
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
