package DAO

import com.mongodb.casbah.Imports._

object MongoDBCollManager {
  
    /**
     * for further use which database and the service are not in the same machine
     */
    private def DBHost = "localhost"
    private def DBPort = ""
    private def username = ""
    private def password = ""

    /**
     * functional functions
     */
    private def getDefaultCilent : MongoClient = MongoClient()
    private def getBabyDatabase(name : String = "baby") : MongoDB = this.getDefaultCilent(name)
    private def getCollection(name : String) : MongoCollection = this.getBabyDatabase()(name)

    private def getProductsCollection : MongoCollection = this.getCollection("products")
//    private def getProductsDetailCollection : MongoCollection = this.getCollection("products_details")
    
    /**
     * nonfunctional functions, for control the total collections
     * in order to save bits
     */
   
    private var colls : Map[String, MongoCollection] = Map.empty
    
    def getCollectionSafe(name : String) : MongoCollection = colls.get(name).getOrElse(null) match {
      case null => { val r = getCollection(name); colls += name -> r; r }
      case e : MongoCollection => {
          if (e.isTraversableAgain) e
          else { val r = getCollection(name); colls += name -> r; r }
      }
    }
}