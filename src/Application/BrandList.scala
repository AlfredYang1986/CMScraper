package Application

import scala.xml.XML
import DAO.MongoDBCollManager
import com.mongodb.casbah.Imports._

object BrandList {
  
	case class brandNode(name : String) {
		var relateCats : List[String] = Nil
	}
  
	var brands : List[brandNode] = Nil

	def contains(n : String) : Boolean = !(brands.find(x => x.name == n).isEmpty)
	
	/**
	 * save to db
	 */
	def save = {
		if (MongoDBCollManager.isCollectionExist("brands")) 
			MongoDBCollManager.removeCollection("brands")
	
		brands map { iter => 
		  	/**
		  	 * brand name
		  	 */
			val builder = MongoDBObject.newBuilder
			builder += "brandName" -> iter.name
			
			/**
			 * relate Categories
			 */
			val catsList = MongoDBList.newBuilder
			iter.relateCats map (cat => catsList += cat)
			builder += "relateCats" -> catsList.result
			
			MongoDBCollManager.insert("brands")(builder.result)
		}  
	}
	
	def apply(path : String) = {
		try {
			(XML.loadFile(path) \\ "brand") map { nd =>
			  	brands = new brandNode(nd.text) :: brands
			}
		} catch {
		  	case _ => ScraperApp.printer.writeLine("file not exist or something wrong with prasing")
		}
	}
}