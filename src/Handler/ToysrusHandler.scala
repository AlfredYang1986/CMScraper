package Handler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import DAO.ScraperCache

import com.mongodb.casbah.Imports._

class ToysrusHandler extends PageHandler_2 {
    def apply(url : String, host : String) = {
        println("paser item begin ...")
        println(url)
        
        val html = Jsoup.connect(url).timeout(0).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get
    
        val builder = MongoDBObject.newBuilder
        /**
         * 1. get brand
         */
        builder += "brand" -> "Toysrus"
        
        /**
         * 2. get product name
         */
        val proName = html.select("div.prodTitle").text
        println(proName)
        builder += "name" -> proName
        
        /**
         * 3. get image url
         */
        try {
          val imgContent = html.select("div.leftPane > div > table > tbody > tr > td > a").first
          val imgUrl = imgContent.attr("href")
          println(imgUrl)   
          builder += "imgUrl" -> imgUrl
          builder += "StoreOnly" -> false 
      
        } catch {
          case _ => { println("item only in store"); builder += "StoreOnly" -> true }
        }
        
        /**
         * 4. get price
         */
        val price_builder = MongoDBObject.newBuilder
        price_builder += "source" -> "toysrus"
           
        val price_cur = html.select("span.price").first.text
        price_builder += "current_price" -> price_cur 
        println(price_cur)       
        
        try {
          val price_ori = html.select("span.preSalePrice").first.text
          if (price_ori != "$0.00") {
              price_builder += "isOnSale" -> true
              price_builder += "ori_price" -> price_ori
              println(price_ori)
          
          } else {
              println("not on sale")
              price_builder += "isOnSale" -> false
          }
        } catch {
            case _ => println("not on sale"); price_builder += "isOnSale" -> false
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
       
        println("paser item end ...")
    }
}