package Handler.categoryMapping

object BabyBuningMapping {
	def apply(can : String, name : String) : String = can.toLowerCase match {
	  case "3 wheel prams" => "Prams"
	  case "4 wheel prams" => "Prams"
	  case _ => can
	}
}