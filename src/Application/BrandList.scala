package Application

import scala.xml.XML

object BrandList {
	var brands : List[String] = Nil
  
	def apply(path : String) = {
		try {
			(XML.loadFile(path) \\ "brand") map { nd =>
			  	brands = nd.text :: brands
			}
		} catch {
		  	case _ => ScraperApp.printer.writeLine("file not exist or something wrong with prasing")
		}
	}
}