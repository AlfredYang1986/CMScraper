package Handler.categoryMapping

object ChemitWarehouseAndMyChemitMapping {
	def apply(can : String, name : String) : String = {
		if (can == "Baby Accessories") {
			if (name.toLowerCase.contains("bottle") || name.toLowerCase.contains("cup")) "Bottles & Teats"
			else if (name.toLowerCase.contains("breast")) "Feeding Accessories"
			else can
		} else if (can == "Baby Creams and Lotions") {
			if (name.toLowerCase.contains("cream")) "Bady Cream & Ointment"
			else "Lotions"
		} else if (can == "Baby Shampoo and Conditioners") "Bady Shampoo & Body Wash"
		else if (can == "Baby Medical and Vitamins") "Medical & Vitamins"
		else if (can == "Breast Pads, Shields and Cream") "Bath Acessories"
		else if (can == "Baby Nappies") "Changing Accessories"
		else if (can == "Baby Oils") "Oil"
		else if (can == "Baby Wipes") "Baby Wipe"
		else if (can == "Baby Accessories Miscellaneous") {
			if (name.toLowerCase.contains("bottle") || name.toLowerCase.contains("cup")) "Bottles & Teats"
			else if (name.toLowerCase.contains("breast")) "Feeding Accessories"
			else "Baby Accessories" 
		}
		else can
	}
}