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

class MyBabyWarehouseHandler extends PageHandler_2 {
//    def apply(url : String, host : String) = {
    def apply(node : ItemNode, host : String) = {
        val url = node.url
        val can_cat = node.other.asInstanceOf[String]
        println("paser item begin ...")
        println(url)
        println(can_cat)

        val html = JSoapConnectionManager(url) 
    
        val builder = MongoDBObject.newBuilder
        /**
         * 1. get brand
         */
        val tmpBrand = html.select("#product-attribute-specs-table > tbody > tr").get(1).children().select("td").last.text
        println(tmpBrand)
        builder += "brand" -> html.select("#product-attribute-specs-table > tbody > tr").get(1).children().select("td").last.text
         
        /**
         * 2. get product name
         */
        val proName = html.select("div.product-name > span").first.text
        println(proName)
        builder += "name" -> proName       
        
        /**
         * 1.1 get categories
         */
        val tmpCategory = html.select("#site-container > div > div > div > ul > li > a").last.text
        println(tmpCategory)
        builder += "cat" -> MyBabyWarehouseMapping(tmpCategory, proName)
        
        /**
         * 3. get image url
         */
        try {
          val imgContent = html.select("p.product-image > a").first
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
        price_builder += "source" -> "My Baby Warehouse"
        price_builder += "oriCat" -> tmpCategory
        
        try {
          val price_ori = html.select("p.old-price > span.price").text
          price_builder += "isOnSale" -> true
          price_builder += "ori_price" -> price_ori
          println(price_ori)
          
          val price_cur = html.select("p.special-price > span.price").text
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