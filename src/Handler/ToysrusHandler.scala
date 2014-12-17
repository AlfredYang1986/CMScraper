package Handler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import DAO.ScraperCache
import com.mongodb.casbah.Imports._
import Scraper.ItemNode
import Application.BrandList
import Application.JSoapConnectionManager
import Handler.categoryMapping.ToysrusMapping
import Application.ScraperApp

class ToysrusHandler extends PageHandler_2 {
	def name = "Toysrus"
    def apply(node : ItemNode, host : String) : Unit = {
        val url = node.url
        ScraperApp.printer.writeLine("paser item begin ...", name)
        ScraperApp.printer.writeLine(url, name)
        
        val html = JSoapConnectionManager(url)
        if (html == null) return 

        val builder = MongoDBObject.newBuilder
        
        /**
         * 2. get product name
         */
        val proName = html.select("div.prodTitle").text
        ScraperApp.printer.writeLine(proName, name)
        builder += "name" -> proName
        
        /**
         * 1. get brand
         */
		var brand : String = "Unknown"
	
		val bd = BrandList.brands.find(x => proName.startsWith(x.name))
		if (bd.isEmpty) brand = "Unknown" 
		else brand = bd.get.name
		
		ScraperApp.printer.writeLine(brand, name)
		builder += "brand" -> brand

		/**
		 * 1.1 get what it for category
		 * 		1.1.1 if brand occurs, previous is 
		 *   	1.1.2 if brand starts, previous is 
		 *    	1.1.3 if none brands, last one is
		 */
		def getCategory : String = {
			def getCatIter(cur : List[String], xsl : List[String]) : String = {
				if (cur.isEmpty) 
					if (proName == xsl.head) "Miscellaneous"
					else xsl.head
			  
				else if (cur.head.startsWith(brand))
					if (cur.tail.isEmpty) "Miscellaneous"
					else if (cur.tail.head.startsWith(brand)) getCatIter(cur.tail, xsl)
					else cur.tail.head
				else getCatIter(cur.tail, xsl)
			}
		  
			val tmp = html.select("table.pfNavPath > tbody > tr > td > font > a")
		
			var candi : List[String] = Nil
			for (index <- 1 to tmp.size - 1)
				candi = tmp.get(index).text :: candi
		 
			val reVal = getCatIter(candi, candi)
			ScraperApp.printer.writeLine("category : " + reVal, name)
			reVal
		}
		val cat = getCategory
		builder += "cat" -> ToysrusMapping(cat, proName)
		
        /**
         * 3. get image url
         */
        try {
          val imgContent = html.select("div.leftPane > div > table > tbody > tr > td > a").first
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
        price_builder += "source" -> "toysrus"
        price_builder += "oriCat" -> cat
           
        val price_cur = html.select("span.price").first.text
        price_builder += "current_price" -> price_cur 
        ScraperApp.printer.writeLine(price_cur, name)
        
        try {
          val price_ori = html.select("span.preSalePrice").first.text
          if (price_ori != "$0.00") {
              price_builder += "isOnSale" -> true
              price_builder += "ori_price" -> price_ori
              ScraperApp.printer.writeLine(price_ori, name)
          
          } else {
              ScraperApp.printer.writeLine("not on sale", name)
              price_builder += "isOnSale" -> false
          }
        } catch {
            case _ => ScraperApp.printer.writeLine("not on sale", name); price_builder += "isOnSale" -> false
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
