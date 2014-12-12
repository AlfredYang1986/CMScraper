package Scraper

import Handler.PageHandler_2
import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import scala.collection.JavaConverters._
import Application.ScraperApp

trait CategoryCrawl extends Crawl_2 {
	/**
	 * 1. search nav to get the url or the category
	 */
	def categoryQueryString : String
	def cateUrlFromNode : Element => String
	def cateUrlFilter : Element => Boolean = _ => true

	def getCategoryFromNav : List[String] = 
      Jsoup.connect(url).timeout(0).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get.select(categoryQueryString)
			    .asScala.toList.distinct filter cateUrlFilter map cateUrlFromNode
//			    .asScala.toList.distinct map cateUrlFromNode
//          		.asScala.toList.head :: Nil map cateUrlFromNode // only first for
		
	/**
	 * 2. get current page items
	 */
	def itemQueryString : String
	def itmesPerPage(html : Document) : Int
	
	def totalItemsInCategory(html : Document) : Int
	def totalPrintFunc(t : Int) = 
		ScraperApp.printer.writeLine("there are " + t + " items in this category")
		
	def itemUrlFromPage: Element => String
	def urlForNextPage(html : Document, baseUrl : String, pgeIndex : Int) : String
	
	def enumLoop(html : Document, page : String, PrintFunc : (Int, Int) => Unit) : List[String]
	def enumLoopPrintFunc(s : Int, t : Int) = 
		ScraperApp.printer.writeLine("now processing " + s + " of " + t + " items")

	def enumItemInCategory(cate : String) : List[String] =
    		Jsoup.connect(cate).timeout(0).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get.select(itemQueryString)
			.asScala.toList.distinct map itemUrlFromPage
//			.asScala.toList.head :: Nil map itemUrlFromPage  // only first for test
//			.asScala.toList.distinct.take(20) map itemUrlFromPage  // only top 100 for test
	
	def categoryPageInfo(html : Document) = ""
			
//	def enumPagesInCategory(categories : List[String]) : List[String] = {
	def enumPagesInCategory(categories : List[String]) : List[ItemNode] = {
//		def enumPages(page : String) : List[String] = {
		def enumPages(page : String) : List[ItemNode] = {
			val html = Jsoup.connect(page).timeout(0).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get
			val cat = categoryPageInfo(html)
			println(cat)
			enumLoop(html, page, enumLoopPrintFunc) map (new ItemNode(_, cat))
		}
		
		ScraperApp.printer.writeLine("there are " + categories.size + " categories")
		var reVal : List[ItemNode] = Nil
		categories map { iter => 
			reVal = reVal ::: enumPages(iter)
		}
		reVal.distinct
	}
		
	/**
	 * 3. handler url to item
	 */
//	def enumItems(itemUrls : List[String], handler : PageHandler_2) = 
	def enumItems(itemUrls : List[ItemNode], handler : PageHandler_2) = itemUrls map { 
			try {
				handler(_, host) 
			  
			} catch {
			  case _ => "error on parse \n" + _.url 
			}
		}
}