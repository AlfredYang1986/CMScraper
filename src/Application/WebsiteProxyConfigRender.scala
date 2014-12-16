package Application

import Components.WebsiteProxy
import scala.xml.XML
import Handler._
import Scraper._

object WebsiteProxyConfigRender {
	def apply(path : String) : Seq[WebsiteProxy] = {
		def name2Crawl(str : String, url : String, host : String) : Crawl_2 = { 
			str match {
			  case "ChemistWarehouseScraper" => new ChemistWarehouseScraper(url, host)
			  case "MyChemistScraper" => new MyChemistScraper(url, host)
			  case "PriceLineScraper" => new PriceLineScraper(url, host)
			  case "PharmacyDirectScraper" => new PharmacyDirectScraper(url, host)
			  case "BabyBuntingScraper" => new BabyBuntingScraper(url, host)
			  case "ToysrusScraper" => new ToysrusScraper(url, host)
			  case "MyBabyWarehouseScraper" => new MyBabyWarehouseScraper(url, host)
			  case _ => null
			}
		}
		
		def name2Handler(str : String) : PageHandler_2 = { 
			str match {
			  case "ChemistWarehouseHandler" => new ChemistWarehouseHandler
			  case "MyChemistHandler" => new MyChemistHandler 
			  case "PriceLineHandler" => new PriceLineHandler
			  case "PharmacyDirectHandler" => new PharmacyDircetHandler
			  case "BabyBuntingHandler" => new BabyBuntingHandler
			  case "ToysrusHandler" => new ToysrusHandler 
			  case "MyBabyWarehouseHandler" => new MyBabyWarehouseHandler
			  case _ => null
			}
		}
	  
		try {
			(XML.loadFile(path) \\ "website") map { nd =>
				new WebsiteProxy((nd \ "@name") text, 
				    (nd \ "@url") text, name2Handler((nd \ "@handler") text), 
				    name2Crawl((nd \ "@crawl") text, (nd \ "@url") text, (nd \ "@host") text))
			}
		} catch {
		  	case _ => ScraperApp.printer.writeLine("file not exist or something wrong with prasing")
		  	return null
		}
	}
}
