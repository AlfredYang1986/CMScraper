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

	def insertCatForBrand(catName : String, brandName : String) = {
		val tmp = brands.find(_.name.toLowerCase == brandName.toLowerCase)
		if (!tmp.isEmpty) {
			tmp.get.relateCats = catName :: tmp.get.relateCats
			tmp.get.relateCats = tmp.get.relateCats.distinct
		}
	}
	
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
		new XMlBrandExtention()(path)
		new ToysRusUrlBrandExtention()("http://www.toysrus.com.au/brands/")
		new MyBabyWarehouseUrlBrandExtention()("http://mybabywarehouse.com.au/baby-brands.html?___SID=U")
	}
}