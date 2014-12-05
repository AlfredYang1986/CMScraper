package Application

import scala.xml.XML
import DAO.MongoDBCollManager
import com.mongodb.casbah.Imports._

object CateList {
	
	case class catNode(parent : catNode, name : String) {
		def isLeaf : Boolean = false
		var children : List[catNode] = Nil
		var relateBrands : List[String] = Nil
		
		override def toString = "catNode: " + name + children.toString 
	}
	
	class catLeaf(parent: catNode, name : String) extends catNode(parent, name) {
		override def isLeaf = true

		override def toString = "catLeaf: " + name
	}
 
	var cats : List[catNode] = Nil

	def insertBrandForCat(brandName : String, catName : String) = {
		cats map { x => 
			val tmp = x.children.find(_.name == catName)
			if (!tmp.isEmpty) {
				tmp.get.relateBrands = brandName :: tmp.get.relateBrands
				tmp.get.relateBrands = tmp.get.relateBrands.distinct
				println(tmp.get.relateBrands)
			}
		}
	}

	/**
	 * save to db
	 */
	def save = {
		def saveNode(iter : catNode) : Unit = {
			val builder = MongoDBObject.newBuilder
			builder += "catName" -> iter.name
			
			val brandList = MongoDBList.newBuilder
			iter.relateBrands map (br => brandList += br)
			builder += "relateBrands" -> brandList.result
			println(brandList.result)
			
			builder += "isLeaf" -> iter.isLeaf
			MongoDBCollManager.insert("cats")(builder.result)		  		

			if (!iter.isLeaf) {
				iter.children.foreach (saveNode(_))
			}
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