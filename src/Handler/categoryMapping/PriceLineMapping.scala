package Handler.categoryMapping

object PriceLineMapping {
	def apply(can : String, name : String) : String = can match {
	  case "Lotions & Oils" => 
	      if (name.toLowerCase.contains("Oil")) "Oil"
	      else if (name.toLowerCase.contains("Cream")) "Baby Cream"
	      else "Lotions"
	        
	  case "Baby Creams & Ointments" => "Baby Cream"
	    
	  case _ => can
	}
}