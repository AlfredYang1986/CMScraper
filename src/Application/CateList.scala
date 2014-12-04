package Application

import scala.xml.XML
import DAO.MongoDBCollManager
import com.mongodb.casbah.Imports._

object CateList {
	
	case class catNode(parent : catNode, name : String) {
		def isLeaf : Boolean = false
		var children : List[catNode] = Nil
		
		override def toString = "catNode: " + name + children.toString 
	}
	
	class catLeaf(parent: catNode, name : String) extends catNode(parent, name) {
		override def isLeaf = true
		var relateBrands : List[String] = Nil

		override def toString = "catLeaf: " + name
	}
 
	var cats : List[catNode] = Nil

	/**
	 * save to db
	 */
	def save = {
		def saveNode(iter : catNode) : Unit = {
			val builder = MongoDBObject.newBuilder
			builder += "catName" -> iter.name
			
			if (iter.isLeaf) {
				builder += "isLeaf" -> true
			
				val brandList = MongoDBList.newBuilder
				iter.children map (br => brandList += br)
				builder += "relateBrands" -> brandList.result
			  
			} else {
				builder += "isLeaf" -> false
				iter.children.foreach (x => saveNode(x))
			}
			MongoDBCollManager.insert("cats")(builder.result)		  		
		}
	  
		if (MongoDBCollManager.isCollectionExist("cats")) 
			MongoDBCollManager.removeCollection("cats")
			
		cats map (iter => saveNode(iter))
	}
	
	def apply(path : String) = {
		try {
			(XML.loadFile(path) \\ "cat") map { nd =>
			  	val tmp = new catNode(null, (nd \ "@value") text)
			  	(nd \ "subcat") map { sb => 
			  	  	tmp.children = new catLeaf(tmp, sb.text) :: tmp.children
			  	}
			  	cats = tmp :: cats
			}
		} catch {
		  	case _ => ScraperApp.printer.writeLine("file not exist or something wrong with prasing")
		}
	}
}