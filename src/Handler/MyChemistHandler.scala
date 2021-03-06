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
import Handler.categoryMapping.ChemitWarehouseAndMyChemitMapping
import Scraper.ItemNode
import Application.ScraperApp
import Application.JSoapConnectionManager

class MyChemistHandler extends PageHandler_2 {
  def name = "My Chemist"
  def apply(node : ItemNode, host : String) : Unit = {
    val url = node.url
    
    val html = JSoapConnectionManager(url)
    if (html == null) return
    
    ScraperApp.printer.writeLine("paser item begin ...", name)
    val builder = MongoDBObject.newBuilder

    /**
	 * 2. get product name
	 */
	val proName = html.select("div.ProductPage_ProductName").text
	ScraperApp.printer.writeLine(proName, name)
	builder += "name" -> proName
			
	/**
	 * 1. get brand
	 */
	val tmp = html.select("div#MainContent > table > tbody > tr > td > div > div > a > b")
	var brand : String = tmp.get(tmp.size - 2).text
	if (BrandList.contains(brand)) builder += "brand" -> brand	
	else {
		val bd = BrandList.brands.find(x => proName.toLowerCase.startsWith(x.name.toLowerCase))
		if (bd.isEmpty) brand = "Unknown"
		else brand = bd.get.name
	} 
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
			if (cur.isEmpty) xsl.head
			  
			else if (cur.head.startsWith(brand))
				if (cur.tail.isEmpty) "Miscellaneous"
				else cur.tail.head
			else getCatIter(cur.tail, xsl)
		}
		  
		val tmp = html.select("div#MainContent > table > tbody > tr > td > div > div > a > b")
		
		var candi : List[String] = Nil
		for (index <- 1 to tmp.size - 1)
			candi = tmp.get(index).text :: candi
		  
		val reVal = getCatIter(candi, candi)
		ScraperApp.printer.writeLine(reVal, name)
		reVal
	}
	val cat = getCategory
	builder += "cat" -> ChemitWarehouseAndMyChemitMapping(cat, proName)
    
    /**
     * 3. get image url
     */
    try {
      val imgContent = html.select("div.ProductPage_ProdImage > div > a").first
      val imgUrl = host + imgContent.attr("href")
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
    price_builder += "source" -> "My Chemist"
    price_builder += "oriCat" -> cat
    val price_cur = html.select("div.ProductPage_NormalPrice").text
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
