package Handler

import Scraper.ItemNode

trait PageHandler_2 {
	def apply(node : ItemNode, host : String) : Unit
	def name : String
}
