package Scraper

import Handler.PageHandler_2
import Application.ScraperApp
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import DAO.ScraperCache

class PharmacyDirectScraper(p : String, h : String) extends CategoryCrawl {

  def url = p
  def name = "Pharmacy Direct Mother and Baby"
  override def host = h
    
  def categoryQueryString = "div#divByDeptHeading > ul > li > ul > li > a"
  def itemQueryString = "div.sli_grid_result > h2 > a"
  def itmesPerPage(html : Document) : Int = 15 

  def totalItemsInCategory(html : Document) : Int = html.select("span.sli_bct_total_records").first.text.trim.toInt
  
  def cateUrlFromNode : Element => String = _.attr("href")
  def itemUrlFromPage: Element => String = _.attr("href")

  def urlForNextPage(html : Document, baseUrl : String, pgeIndex : Int) : String = baseUrl.substring(0, baseUrl.lastIndexOf("/")) + "/" + pgeIndex
  def enumLoop(html : Document, page : String, PrintFunc : (Int, Int) => Unit) : List[String] = {
      var reVal : List[String] = Nil
    val pgeSize = itmesPerPage(html)
    val totalItemInCat = totalItemsInCategory(html)
    totalPrintFunc(totalItemInCat)
  
    for (index <- 0 to totalItemInCat / pgeSize) {
      val next = urlForNextPage(html, page, index)
      if (next != null) reVal = reVal ::: enumItemInCategory(next)
      ScraperApp.printer.writeLine("now processing " + reVal.size + " of " + totalItemInCat + " items", name)
    }
    reVal
  }

  override def categoryPageInfo(html : Document) = html.select("#sli_bct > div > div > b").last.text
}