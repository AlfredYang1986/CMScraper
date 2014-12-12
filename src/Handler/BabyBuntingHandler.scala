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

class BabyBuntingHandler extends PageHandler_2 {
//    def apply(url : String, host : String) = {
    def apply(node : ItemNode, host : String) : Unit = {
        val url = node.url
        
        println("paser item begin ...")
        
        
        var html : Document = null
        
        try {
          html = Jsoup.connect(url).timeout(0).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get
        } catch {
          case _ => return Unit
        }
    
        val builder = MongoDBObject.newBuilder
        /**
         * 2. get product name
         */
        val proName = html.select("div.page-title > h1").first.text
        println(proName)
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
//		builder += "cat" -> BabyBuningMapping(node.other.asInstanceOf[String], "")
		builder += "cat" -> node.other.asInstanceOf[String]
        
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
          val price_ori = html.select("p.old-price > span").first.text
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