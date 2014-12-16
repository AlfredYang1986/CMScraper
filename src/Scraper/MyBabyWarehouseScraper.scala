package Scraper

import Handler.PageHandler_2
import Application.ScraperApp
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import DAO.ScraperCache

class MyBabyWarehouseScraper(p : String, h : String) extends CategoryCrawl {

  def url = p
  def name = "My Baby Warehouse"
  override def host = h
    
  def categoryQueryString = "div#header-nav > nav > ol > li > ul > li > a"
  def itemQueryString = "ul.products-grid > li > a"
  def itmesPerPage(html : Document) : Int = 0

  override def cateUrlFilter : Element => Boolean = !_.text.contains("Brands")

  def totalItemsInCategory(html : Document) : Int = html.select("p.amount").first.text.trim.split(" ").last.toInt 

  def cateUrlFromNode : Element => String = _.attr("href")
  def itemUrlFromPage: Element => String = _.attr("href")

  def urlForNextPage(html : Document, baseUrl : String, pgeIndex : Int) : String = baseUrl + "?limit=all" 
  def enumLoop(html : Document, page : String, PrintFunc : (Int, Int) => Unit) : List[String] = {
      var reVal : List[String] = Nil
//    val pgeSize = itmesPerPage(html)
      val totalItemInCat = totalItemsInCategory(html)
      totalPrintFunc(totalItemInCat)
  
//    for (index <- 0 to totalItemInCat / pgeSize) {
      val next = urlForNextPage(html, page, 0)
      if (next != null) reVal = reVal ::: enumItemInCategory(next)
//      ScraperApp.printer.writeLine("now processing " + reVal.size + " of " + totalItemInCat + " items", name)
//    }
    reVal
  }
  
  override def categoryPageInfo(html : Document) = html.select("#site-container > div > div > div > ul > li")
		  											.last.children().select("strong").text
}