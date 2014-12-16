package Handler.categoryMapping

object ToysrusMapping {
	def apply(can : String, name : String) : String = can match {
	  case "Figures and Playsets" => "Indoors"
	  case "Building & Construction" => "Indoors"
	  case "Dolls & Playsets" => "Indoors"
	  case "Games" => "Intelligence Development"
	  case "Role Play" => "Intelligence Development"
	  case "Vehicles" => "Intelligence Development"
	  case "Pre-School" => "Intelligence Development"
	  case "Learning" => "Books & CD's"
	  case "Outdoor" => "Outdoors"
	  case "Wheels" => "Outdoors"
	  case "Soft Toys" => "Indoors"
	  case "Summer" => "Indoors"
	  case "Bedroom" => "Indoors"
	  case "Batteries" => "Indoors"
	}
}
