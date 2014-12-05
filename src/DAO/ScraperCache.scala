package DAO

import com.mongodb.casbah.Imports._
import Application.CateList
import Application.BrandList

object ScraperCache {

    private var cache : List[MongoDBObject] = Nil 
   
    def refresh(name : String = "products") =  {
        def refreshAcc(xls : List[MongoDBObject]) : Unit = 
            if (xls.isEmpty) Unit 
            else { 
                MongoDBCollManager.getCollectionSafe(name) += xls.head
                refreshAcc(xls.tail) 
            }
       
        if (!cache.isEmpty) {
        		if (MongoDBCollManager.isCollectionExist(name)) 
        			MongoDBCollManager.removeCollection(name)
        		
        		refreshAcc(cache)
        }
    }
   
    /**
     * manipulating price into mongolist
     */
    def ++ (newObj : MongoDBObject) = {
        /**
         * 2. union result if exist
         */
        def union(o : MongoDBObject, n : MongoDBObject) : MongoDBObject = {
            var po = (o.get("prices").get).asInstanceOf[MongoDBList]
            var pn = (n.get("prices").get).asInstanceOf[MongoDBList]
            po += pn.head
            
            o += "prices" -> po
            o
        }
        
        /**
         * 3. find result
         */
        def unionInCache : MongoDBObject = {
          val re = cache.find { _.get("name").get == newObj.get("name").get }
          if (re == None) newObj
          else union(re.get, newObj)
        }
        
        /**
         * 1. first check the has project or not      
         */
        cache = unionInCache :: cache.filter { _.get("name") != newObj.get("name") }
        
        val brand = newObj.get("brand").get.asInstanceOf[String]
        val cat = newObj.get("cat").get.asInstanceOf[String]
        /**
         * 4. add brands for this category
         */
        CateList.insertBrandForCat(brand, cat)
        
        /**
         * 5. add category for brands
         */
        BrandList.insertCatForBrand(cat, brand)
    }
}