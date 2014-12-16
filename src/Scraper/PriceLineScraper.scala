package Scraper

import Handler.PageHandler_2
import Application.ScraperApp
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import DAO.ScraperCache

class PriceLineScraper(p : String, h : String) extends CategoryCrawl {

  def url = p
  def name = "Price Line Mother and Baby"
  override def host = h
    
  def categoryQueryString = "div.brand-box > div > a"
  def itemQueryString = "div.product-desc > a"
  def itmesPerPage(html : Document) : Int = 20

  def totalItemsInCategory(html : Document) : Int = html.select("div.category-products-list-top-left-total")
                            .first.text.trim.split(" ").apply(5).toInt
  
  def cateUrlFromNode : Element => String = host + _.attr("href")
  def itemUrlFromPage: Element => String = host + _.attr("href")

  def urlForNextPage(html : Document, baseUrl : String, pgeIndex : Int) : String = baseUrl + "&page=" + pgeIndex
  def enumLoop(html : Document, page : String, PrintFunc : (Int, Int) => Unit) : List[String] = {
      var reVal : List[String] = Nil
    	  val pgeSize = itmesPerPage(html)
    	  val totalItemInCat = totalItemsInCategory(html)
    	  totalPrintFunc(totalItemInCat)
  
    	  for (index <- 0 to totalItemInCat / pgeSize) {
          val next = urlForNextPage(html, page, index + 1)
        	  if (next != null) reVal = reVal ::: enumItemInCategory(next)
        	  ScraperApp.printer.writeLine("now processing " + reVal.size + " of " + totalItemInCat + " items", name)
    	  }
    	  reVal
  }
}