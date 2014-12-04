package Handler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import DAO.ScraperCache
import com.mongodb.casbah.Imports._
import Application.BrandList
import Application.BrandList.brandNode

class MyChemistHandler extends PageHandler_2 {
  def apply(url : String, host : String) = {
    println("paser item begin ...")
    val html = Jsoup.connect(url).timeout(0).get
    
    val builder = MongoDBObject.newBuilder

    /**
	 * 2. get product name
	 */
	val proName = html.select("div.ProductPage_ProductName").text
	println(proName)
	builder += "name" -> proName
			
	/**
	 * 1. get brand
	 */
	val tmp = html.select("div#MainContent > table > tbody > tr > td > div > div > a > b")
	var brand : String = tmp.get(tmp.size - 2).text
	if (BrandList.contains(brand)) builder += "brand" -> brand	
	else {
		val bd = BrandList.brands.find(x => proName.startsWith(x.name))
		if (bd.isEmpty) brand = "Miscellaneous"
		else brand = bd.get.name
	} 
	println(brand)
	
	/**
	 * 1.1 get what it for category
	 */
    
    /**
     * 3. get image url
     */
    try {
      val imgContent = html.select("div.ProductPage_ProdImage > div > a").first
      val imgUrl = host + imgContent.attr("href")
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
    price_builder += "source" -> "My Chemist"
    val price_cur = html.select("div.ProductPage_NormalPrice").text
    price_builder += "current_price" -> price_cur 

    try {
      val price_ori = html.select("span.ProductPage_NormalSRP").first.text
      price_builder += "isOnSale" -> true
      price_builder += "ori_price" -> price_ori
    } catch {
      case _ => { println("not on sale"); price_builder += "isOnSale" -> true }
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