package Scraper

import Handler.PageHandler_2

trait Crawl_2 {
	def apply(handler :PageHandler_2)
	def url : String
	def name : String
	def host : String = ""
}

/**
 * 4. inject category for some url which does not put the category in item page
 */
case class ItemNode(url : String, other : Object)