package Scraper

import Handler.PageHandler_2
import Application.ScraperApp
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import DAO.ScraperCache

class ChemistWarehouseScraper(p : String, h : String) extends CategoryCrawl {
	def apply(handler :PageHandler_2) = {
		ScraperApp.printer.writeLine("Chemist Warehouse Scraper Prase Start ...")
		enumItems(enumPagesInCategory(getCategoryFromNav), handler)
//    ScraperCache.refresh()
		ScraperApp.printer.writeLine("Chemist Warehouse Scraper Prase End...")
	}

	def url = p
	def name = "Chemist Warehouse"
	override def host = h
	override def cateUrlFilter : Element => Boolean = x => x.text == "Baby Care" //|| x.text.contains("Kids")
//	  (_.text == "Baby Care") || (_.text.contains("kids"))
	  
	def categoryQueryString = "div#MainContent > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr > td > div > a"
	def itemQueryString = "div.column > div > div > a"
	def itmesPerPage(html : Document) : Int = 120

	def totalItemsInCategory(html : Document) : Int = html.select("div#MainContent > table > tbody > tr > td > div > div > b")
														.first.text.trim.split(" ").apply(0).toInt
	
	def cateUrlFromNode : Element => String = host + _.attr("href")
	def itemUrlFromPage: Element => String = host + _.attr("href")

	def urlForNextPage(html : Document, baseUrl : String, pgeIndex : Int) : String = baseUrl + "&perPage=120" + "&page=" + pgeIndex
	def enumLoop(html : Document, page : String, PrintFunc : (Int, Int) => Unit) : List[String] = {
	  	var reVal : List[String] = Nil
		val pgeSize = itmesPerPage(html)
		val totalItemInCat = totalItemsInCategory(html)
		totalPrintFunc(totalItemInCat)
	
		for (index <- 0 to totalItemInCat / pgeSize) {
		  	val next = urlForNextPage(html, page, index + 1)
			if (next != null) reVal = reVal ::: enumItemInCategory(next).distinct
			ScraperApp.printer.writeLine("now processing " + reVal.size + " of " + totalItemInCat + " items")
		}
		reVal
	}
}