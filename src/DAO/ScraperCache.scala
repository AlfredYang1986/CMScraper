package DAO

import com.mongodb.casbah.Imports._

object ScraperCache {

    private var cache : List[MongoDBObject] = Nil 
   
    def refresh(name : String = "products") =  {
        def refreshAcc(xls : List[MongoDBObject]) : Unit = 
            if (xls.isEmpty) Unit 
            else { 
                MongoDBCollManager.getCollectionSafe(name) += xls.head
                refreshAcc(xls.tail) 
            }
        
        if (!cache.isEmpty) refreshAcc(cache)
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
    }
}