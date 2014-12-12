package Application

import scala.xml.XML
import org.jsoup.Jsoup

trait BrandExtention {
	def apply(s : String)
}

class XMlBrandExtention extends BrandExtention {
	override def apply(path : String) = {
		try {
			(XML.loadFile(path) \\ "brand") map { nd =>
			  	BrandList.brands = new BrandList.brandNode(nd.text) :: BrandList.brands
			}
			BrandList.brands.distinct
		} catch {
		  	case _ => ScraperApp.printer.writeLine("file not exist or something wrong with prasing")
		}
	}
}

abstract class UrlBrandExtention extends BrandExtention {
	def brandElementQueryString : String
  
  	override def apply(url : String) {
  		val html = Jsoup.connect(url).timeout(0).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get
  		val nd = html.select(brandElementQueryString)
  	
  		for (index <- 0 to nd.size - 1) ( BrandList.brands = new BrandList.brandNode(nd.get(index).text) :: BrandList.brands )
  		
  		BrandList.brands.distinct
	}
}

class ToysRusUrlBrandExtention extends UrlBrandExtention {
	override def brandElementQueryString = "table > tbody > tr > td > table > tbody > tr > td > b > a"
}

class MyBabyWarehouseUrlBrandExtention extends UrlBrandExtention {
	override def brandElementQueryString = "li.amshopby-cat > a"
}