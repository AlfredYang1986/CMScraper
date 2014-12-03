package Application

object CateList {
	
	case class catNode(parent : catNode, children : List[catNode]) {
		def isLeaf : Boolean = false
	}
	
	class catLeaf(parent: catNode) extends catNode(parent, Nil) {
		override def isLeaf = true
	}
 
	var cats : List[catNode] = Nil
	
	def apply(path : String) = {
	  
	}
}