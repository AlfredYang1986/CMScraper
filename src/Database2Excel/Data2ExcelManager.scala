package Database2Excel

import java.io._
import jxl._
import jxl.write._
import DAO.MongoDBCollManager
import com.mongodb.BasicDBList
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBObject
import scala.Boolean

trait Collection2ExcelSheetProxy {
	def collName : String
	def headers : List[String]
	def apply : Unit = {
		Data2ExcelManager.createWorkSheet(collName)
		var col = 0
		headers map { x => 
		  	Data2ExcelManager.addColHeader(col, x, collName) 
		  	col = col + 1
		}
	}
}

class Brand2ExcelProxy extends Collection2ExcelSheetProxy {
 
	def collName = "brands"
	def headers : List[String] = List("Brand Name", "Relate Cats")
	  
	override def apply = {
		println("brands data to excel start ...")
		super.apply
		var col = 0; var row = 0
		MongoDBCollManager.getCollectionSafe(collName).find foreach { x =>
		  	row = row + 1
		  	col = 0
		  	
			val brandName = x.get("brandName").asInstanceOf[String]
		  	Data2ExcelManager.addValueAtColRow(col, row, brandName, collName)

		  	col = col + 1
		  	val cats = x.get("relateCats").asInstanceOf[BasicDBList].toArray
		  	cats map { x =>
		  	  	if (x != cats.head) row = row + 1
		  	  	
		  	  	Data2ExcelManager.addValueAtColRow(col, row, x.asInstanceOf[String], collName)
		  	}
		}
		println("brands data to excel end ...")
	}
}

class Cat2ExcelProxy extends Collection2ExcelSheetProxy {
	
	def collName = "cats"
	def headers : List[String] = List("Cat Name", "Relate Brands")

	override def apply = {
		println("cats data to excel start ...")
		super.apply
		var col = 0; var row = 0
		
		val builder = MongoDBObject.newBuilder
		builder += "isLeaf" -> true
		
		MongoDBCollManager.getCollectionSafe(collName).find(builder.result) foreach { x =>
		  	row = row + 1
		  	col = 0
		  	
			val catName = x.get("catName").asInstanceOf[String]
		  	Data2ExcelManager.addValueAtColRow(col, row, catName, collName)

		  	col = col + 1
		  	val brands = x.get("relateBrands").asInstanceOf[BasicDBList].toArray
		  	brands map { x =>
		  	  	if (x != brands.head) row = row + 1
		  	  
		  	  	Data2ExcelManager.addValueAtColRow(col, row, x.asInstanceOf[String], collName)
		  	}
		}
		println("cats data to excel end ...")
	}
}

class products2ExcelProxy extends Collection2ExcelSheetProxy {

	def collName = "products"
	def headers : List[String] = List("Product Name", "Brand", "Category", "imgUrl", "source", "Ori Category", "Cur Price", "Ori Price", "On Sale")
	  
	override def apply = {
	  	println("products data to excel start ...")
		super.apply
		var col = 0; var row = 0
		
		MongoDBCollManager.getCollectionSafe(collName).find foreach { x =>
		  	row = row + 1
		  	col = 0
		  
		  	/**
		  	 * product name
		  	 */
			val name = x.get("name").asInstanceOf[String]
		  	Data2ExcelManager.addValueAtColRow(col, row, name, collName)

		  	/**
		  	 * brand
		  	 */
		  	col = col + 1
		  	val brand = x.get("brand").asInstanceOf[String]
		  	Data2ExcelManager.addValueAtColRow(col, row, brand, collName)
		  
		  	/**
		  	 * category
		  	 */
		  	col = col + 1
		  	val cat = x.get("cat").asInstanceOf[String]
		  	Data2ExcelManager.addValueAtColRow(col, row, cat, collName)
		  	
		  	/**
		  	 * imgUrl
		  	 */
		  	col = col + 1
		  	val imgUrl = x.get("imgUrl").asInstanceOf[String]
		  	Data2ExcelManager.addValueAtColRow(col, row, imgUrl, collName)
		  
		  	val tmp_col = col
		  	val arr = x.get("prices").asInstanceOf[BasicDBList].toArray
		  	arr map { x =>
		  	  	if (x != arr.head) row = row + 1
		  	  	val tmp = x.asInstanceOf[BasicDBObject]
		  	  	
		  	  	/**
		  	  	 * source
		  	  	 */
		  	  	col = tmp_col + 1
		  	  	val source = tmp.get("source").asInstanceOf[String]
		  	  	Data2ExcelManager.addValueAtColRow(col, row, source, collName)
		  	  	
		  	  	/**
		  	  	 * oriCat
		  	  	 */
		  	  	col = col + 1
		  	  	val oriCat = tmp.get("oriCat").asInstanceOf[String]
		  	  	Data2ExcelManager.addValueAtColRow(col, row, oriCat, collName)
		  	  	
		  	  	/**
		  	  	 * current_price
		  	  	 */
		  	  	col = col + 1
		  	  	val current_price = tmp.get("current_price").asInstanceOf[String]
		  	  	Data2ExcelManager.addValueAtColRow(col, row, current_price, collName)
		  	  	
		  	  	/**
		  	  	 * ori_price
		  	  	 */
		  	  	col = col + 1
		  	  	val ori_price = tmp.get("ori_price").asInstanceOf[String]
		  	  	Data2ExcelManager.addValueAtColRow(col, row, ori_price, collName)
		  	  	
		  	  	/**
		  	  	 * isOnSale
		  	  	 */
		  	  	col = col + 1
		  	  	if (tmp.get("isOnSale").asInstanceOf[Boolean]) Data2ExcelManager.addValueAtColRow(col, row, "true", collName)
		  	  	else Data2ExcelManager.addValueAtColRow(col, row, "false", collName)
		  	}
		}
	  	println("products data to excel end ...")
	}
}

object Data2ExcelManager {

	var book : WritableWorkbook = null
  
	def apply(path : String) = {
		if (book != null) book.close

		val f = new File(path)
		if (f.exists) f.delete

		createXSLFile(path)
	
		val tmp = MongoDBCollManager.enumCollections 
		println(tmp)
		
		MongoDBCollManager.enumCollections foreach ( x => x match {
		  case "brands" => new Brand2ExcelProxy().apply
		  case "cats" => new Cat2ExcelProxy().apply
		  case "products" => new products2ExcelProxy().apply
		  case _ => Unit
		})
		
		save
		close
	}
  
	def createXSLFile(path : String) = book = Workbook.createWorkbook(new File(path))
	def createWorkSheet(name : String) = book.createSheet(name, 1)
	
	def addValueAtColRow(col : Int, row : Int, value : String, sheetName : String) = book.getSheet(sheetName).addCell(new Label(col, row, value))
	def addColHeader(col : Int, value : String, sheetName : String) = addValueAtColRow(col, 0, value, sheetName)
	
	def save = book.write
	def close = book.close
}