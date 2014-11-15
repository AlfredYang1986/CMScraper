package Scraper

import Handler.PageHandler_2
import Application.ScraperApp
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import DAO.ScraperCache

class ToysrusScraper(p : String, h : String) extends CategoryCrawl {
  def apply(handler :PageHandler_2) = {
    ScraperApp.printer.writeLine("Toysrus Scraper Prase Start ...")
    enumItems(enumPagesInCategory(getCategoryFromNav), handler)
    ScraperApp.printer.writeLine("Toysrus Bunting Scraper Prase End...")
  }

  def url = p
  def name = "Toysrus"
  override def host = h
    
  def categoryQueryString = "div.menuByCategory > div > ul > li > a"
  def itemQueryString = "div.productImageCell > span > a"
  def itmesPerPage(html : Document) : Int = 0

  def totalItemsInCategory(html : Document) : Int = 0  

  def cateUrlFromNode : Element => String = _.attr("href")
  def itemUrlFromPage: Element => String = _.attr("href")

  def urlForNextPage(html : Document, baseUrl : String, pgeIndex : Int) : String = {
      var reUrl : String = ""
      if (baseUrl.endsWith("/")) reUrl = baseUrl.substring(0, baseUrl.length - 1)
      else if (baseUrl.endsWith("___")) reUrl = baseUrl.substring(0, baseUrl.length - 5)
      else reUrl = baseUrl
  
      "%s_%d___".format(reUrl, pgeIndex)
  }
  def enumLoop(html : Document, page : String, PrintFunc : (Int, Int) => Unit) : List[String] = {
      var reVal : List[String] = Nil
//    val pgeSize = itmesPerPage(html)
//    val totalItemInCat = totalItemsInCategory(html)
//    totalPrintFunc(totalItemInCat)
      val totalPages = html.select("table.pfproductcategorypaging > tbody > tr > td > b")
                          .first.text.trim.split(" ").last.toInt
      println("there are %d pages in this category".format(totalPages))
      
      for (index <- 1 to totalPages) {
          val next = urlForNextPage(html, page, index)
          if (next != null) reVal = reVal ::: enumItemInCategory(next)
      }
      reVal
  }
}