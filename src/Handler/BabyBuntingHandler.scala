package Handler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import DAO.ScraperCache

import com.mongodb.casbah.Imports._

class BabyBuntingHandler extends PageHandler_2 {
    def apply(url : String, host : String) = {
        println("paser item begin ...")
        
        val html = Jsoup.connect(url).timeout(0).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get
    
        val builder = MongoDBObject.newBuilder
        /**
         * 1. get brand
         */
        builder += "brand" -> "Baby Bunting"
        
        /**
         * 2. get product name
         */
        val proName = html.select("div.page-title > h1").first.text
        println(proName)
        builder += "name" -> proName
        
        /**
         * 3. get image url
         */
        try {
          val imgContent = html.select("div.product-img-box > a > img").first
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
        price_builder += "source" -> "Baby Bunting"
        
        try {
          val price_ori = html.select("p.old-price > span").text
          price_builder += "isOnSale" -> true
          price_builder += "ori_price" -> price_ori
          println(price_ori)
          
          val price_cur = html.select("p.special-price > span.price > amasty_seo").last.text
          price_builder += "current_price" -> price_cur 
          println(price_cur)
 
        } catch {
            case _ => { 
              val price_cur = html.select("span.regular-price > span > amasty_seo").last.text
              price_builder += "current_price" -> price_cur 
              println(price_cur)
              println("not on sale"); price_builder += "isOnSale" -> false
            }
        }
        
        println("paser item end ...")
    }
}