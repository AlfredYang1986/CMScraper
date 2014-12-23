package Scraper

import Handler.PageHandler_2
import Application.ScraperApp
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import DAO.ScraperCache
import Application.JSoapConnectionManager
import scala.collection.JavaConverters._

class ToysrusScraper(p : String, h : String) extends CategoryCrawl {

  def url = p
  def name = "Toysrus"
  override def host = h
    
  def categoryQueryString = "div.menuByCategory > div > ul > li > a"
  override def itemQueryString = "div.productImageCell > span > a"
  def itmesPerPage(html : Document) : Int = 0

  def totalItemsInCategory(html : Document) : Int = 0  

  override def cateUrlFilter : Element => Boolean = _.children().size > 0

  def cateUrlFromNode : Element => String = _.attr("href")
  def itemUrlFromPage: Element => String = _.attr("href")

  def urlForNextPage(html : Document, baseUrl : String, pgeIndex : Int) : String = {
      var reUrl : String = ""
      if (baseUrl.endsWith("/")) reUrl = baseUrl.substring(0, baseUrl.length - 1)
      else if (baseUrl.endsWith("___")) reUrl = baseUrl.substring(0, baseUrl.length - 5)
      else reUrl = baseUrl
  
      "%s_%d___/".format(reUrl, pgeIndex)
  }
  def enumLoop(html : Document, page : String, PrintFunc : (Int, Int) => Unit) : List[String] = {
      var reVal : List[String] = Nil
//    val pgeSize = itmesPerPage(html)
//    val totalItemInCat = totalItemsInCategory(html)
//    totalPrintFunc(totalItemInCat)
      val totalPageElement = html.select("table.pfproductcategorypaging > tbody > tr > td > b")
      val totalPages =  if (totalPageElement.size == 0) 1
      					else totalPageElement.first.text.trim.split(" ").last.toInt

      ScraperApp.printer.writeLine("there are %d pages in this category".format(totalPages), name)
      
      for (index <- 1 to totalPages) {
          val next = urlForNextPage(html, page, index)
          if (next != null) reVal = reVal ::: enumItemInCategory(next)
          ScraperApp.printer.writeLine("now processing page %d".format(index), name)
      }
      reVal
  }
  
  override def categoryPageInfo(html : Document) : String = html.select("table.pfNavPath > tbody > tr > td > font > a").last.text
}