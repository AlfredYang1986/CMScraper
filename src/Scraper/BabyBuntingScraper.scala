package Scraper

import Handler.PageHandler_2
import Application.ScraperApp
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import DAO.ScraperCache

class BabyBuntingScraper(p : String, h : String) extends CategoryCrawl {

  def url = p
  def name = "Baby Bunting"
  override def host = h
    
  def categoryQueryString = "div.category-view > div > div > ul > li > a"
  def itemQueryString = "ul.products-grid > li > div > div > a"
  def itmesPerPage(html : Document) : Int = 30

  def totalItemsInCategory(html : Document) : Int = {

	  var result : Int = 0
    
	  try {
		  var str = html.select("div.toolbar > div > p > strong").first.text.trim
		  result = str.substring(0, str.indexOf(" ")).toInt
	    
	  } catch {
	    case _ => {
	      try {
		    val arr = html.select("div.toolbar > div > p").first.text.trim.split(" ") 
	    	if (arr.length == 2) result = arr.head.toInt
	        else result = arr.apply(arr.length - 2).toInt
	        
	      } catch {
	        case _ => result = 0
	      }
	    }
	  }
	  result
  }

  def cateUrlFromNode : Element => String = host + _.attr("href")
  def itemUrlFromPage: Element => String = _.attr("href")

  def urlForNextPage(html : Document, baseUrl : String, pgeIndex : Int) : String = baseUrl + "?limit=30&p=" + pgeIndex
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
	
  override def categoryPageInfo(html : Document) = html.select("ul#vertnav > li.active > span > a > span").text
}