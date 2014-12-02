package Application

import DAO.ScraperCache

import com.mongodb.casbah.Imports._

object ScraperApp extends App {
	
	/**
	 * Scraper system log
	 */
	object printer {
		def writeLine(str : String) {
			println(str)
		}
		
		def write(str : String) {
			print(str)
		}
	}
	
	/**
	 * Scraper system started
	 */
	printer.writeLine("Scraper running ...")
	WebsiteProxyConfigRender("src/Config/Config.xml") map (_.apply)
	ScraperCache.refresh()
}