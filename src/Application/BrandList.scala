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
//		if (MongoDBCollManager.isCollectionExist("brands")) 
//			MongoDBCollManager.removeCollection("brands")

		def union(cur : BasicDBList, cats : List[String]) : MongoDBList = {
			val left = cur.sortBy(x => x.asInstanceOf[String])
		
			val result = MongoDBList.newBuilder
			(left.union(cats).distinct) map ( x => result += x )
			
			result.result
		}
	  
		brands map { iter => 
		  	
		  	val coll = MongoDBCollManager.getCollectionSafe("brands")
		  	val find_builder = MongoDBObject.newBuilder
		  	find_builder += "brandName" -> iter.name
		  	val fr = coll.findOne(find_builder.result)
		  	
		  	if (fr.isEmpty) {
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
		  		
		  	} else {
		  		val cur = fr.get
		  		val rcs = cur.get("relateCats").asInstanceOf[BasicDBList]
		  		cur += "relateCats" -> union(rcs, iter.relateCats)
		  		
		  		coll.update(find_builder.result, cur)
		  	}
		}  
	}
	
	def apply(path : String) = {
		new XMlBrandExtention()(path)
		new ToysRusUrlBrandExtention()("http://www.toysrus.com.au/brands/")
		new MyBabyWarehouseUrlBrandExtention()("http://mybabywarehouse.com.au/baby-brands.html?___SID=U")
	}
}
