package DAO

import com.mongodb.casbah.Imports._
import Application.CateList
import Application.BrandList


object ScraperCache {

    var lock : AnyRef = new Object()      	
    private var cache : List[MongoDBObject] = Nil 
   
    def refresh(name : String = "products") =  {
        def refreshAcc(xls : List[MongoDBObject]) : Unit = 
            if (xls.isEmpty) Unit 
            else { 
                val coll = MongoDBCollManager.getCollectionSafe(name) //+= xls.head
               
                val builder = MongoDBObject.newBuilder
                builder += "name" -> xls.head.get("name").get.asInstanceOf[String]

                val one = coll.findOne(builder.result)
                if (one.isEmpty) coll += xls.head
                else {
                	val ex = one.get
                	val ne = union(ex, xls.head)
                	coll.update(ex, ne)
                }
                
                refreshAcc(xls.tail) 
            }
       
        if (!cache.isEmpty) {
//        	if (MongoDBCollManager.isCollectionExist(name)) 
//        		MongoDBCollManager.removeCollection(name)
        		
        	refreshAcc(cache)
        }
    }
    
    /**
     * 2. union result if exist
     */
	def union(o : MongoDBObject, n : MongoDBObject) : MongoDBObject = {
		
	  	def unionAcc(left : MongoDBList, right : MongoDBList) : MongoDBList = {
	  	  
	  		if (right.isEmpty) left
	  		else if (left.isEmpty) right
	  	
	  		val lhs = left.head.asInstanceOf[MongoDBObject].get("source").get.asInstanceOf[String] 
	  		val rhs = right.head.asInstanceOf[MongoDBObject].get("source").get.asInstanceOf[String]
	  		
	  		if (lhs >= rhs) unionAcc(left.tail.asInstanceOf[MongoDBList], right.tail.asInstanceOf[MongoDBList]) += right.head
	  		else unionAcc(left.tail.asInstanceOf[MongoDBList], right.tail.asInstanceOf[MongoDBList]) += left.head
	  	}
	  
    	var po = (o.get("prices").get).asInstanceOf[MongoDBList]
    				.sortBy(x => x.asInstanceOf[MongoDBObject].get("source").get.asInstanceOf[String])
    				.asInstanceOf[MongoDBList]
        
	  	var pn = (n.get("prices").get).asInstanceOf[MongoDBList]
    				.sortBy(x => x.asInstanceOf[MongoDBObject].get("source").get.asInstanceOf[String])
    				.asInstanceOf[MongoDBList]
//        po += pn.head
    				
        o += "prices" -> unionAcc(po, pn)
        o
	}
   
    /**
     * manipulating price into mongolist
     */
    def ++ (newObj : MongoDBObject) = lock.synchronized  {
       
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
