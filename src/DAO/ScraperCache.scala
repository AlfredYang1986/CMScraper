package DAO

import com.mongodb.casbah.Imports._
import Application.CateList
import Application.BrandList
import com.mongodb.BasicDBList


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
                	val ne = unionWithDB(ex, xls.head)
                	ne.get("prices")
                	println(ne)
                	coll.update(builder.result, ne)
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
	def unionWithDB(o : MongoDBObject, n : MongoDBObject) : MongoDBObject = {
		
	  	def unionAcc(left : Seq[BasicDBObject], right : Seq[BasicDBObject]) : Seq[BasicDBObject] = { //MongoDBList = {
	  	 
	  		if (right.isEmpty) left
	  		else if (left.isEmpty) right
	  		else {
		  		val lhs = left.head.asInstanceOf[BasicDBObject].get("source").asInstanceOf[String] 
		  		val rhs = right.head.asInstanceOf[BasicDBObject].get("source").asInstanceOf[String]
	  		
		  		if (lhs >= rhs) right.head +: unionAcc(left.tail, right.tail) //+= right.head
		  		else left.head +: unionAcc(left.tail, right) //+= left.head  		  
	  		}
	  	}
	 
    	var po = (o.get("prices").get).asInstanceOf[BasicDBList]
    				.sortBy(x => x.asInstanceOf[BasicDBObject].get("source").asInstanceOf[String])
        
    	var pn = (n.get("prices").get).asInstanceOf[MongoDBList]
    				.sortBy(x => x.asInstanceOf[BasicDBObject].get("source").asInstanceOf[String])
    			
        o += "prices" -> unionAcc(po.asInstanceOf[Seq[BasicDBObject]], pn.asInstanceOf[Seq[BasicDBObject]])
        o
	}
   
    /**
     * manipulating price into mongolist
     */
    def ++ (newObj : MongoDBObject) = lock.synchronized  {
      
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
