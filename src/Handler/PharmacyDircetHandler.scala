package Handler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import DAO.ScraperCache

import com.mongodb.casbah.Imports._

class PharmacyDircetHandler extends PageHandler_2 {
    def apply(url : String, host : String) = {
        println("paser item begin ...")
       
        val html = Jsoup.connect(url).timeout(0).get
    
        val builder = MongoDBObject.newBuilder
        /**
         * 1. get brand
         */
        val tmp = html.select("div.product-details-layout-2-right > div > h2")
        val brand = tmp.text
        println(brand)
        builder += "brand" -> brand
        
        /**
         * 2. get name
         */
        val proName = html.select("div.product-details-layout-2-right > div > h2")
        println(proName)
        builder += "name" -> proName
        
        /**
         * 3. get image url
         */
        try {
            val imgContent = html.select("span.image-wrapper > a > img").first
            val imgUrl = imgContent.attr("src")
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
        price_builder += "source" -> "Price Line"
        val price_cur = html.select("div.order-desc > h2").text
        price_builder += "current_price" -> price_cur 

        try {
            val price_ori = html.select("div.order-rrp > span").first.text
            price_builder += "isOnSale" -> true
            price_builder += "ori_price" -> price_ori
        } catch {
          case _ => { println("not on sale"); price_builder += "isOnSale" -> false }
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