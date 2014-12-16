package Handler.categoryMapping

object BabyBuningMapping {
	def apply(can : String, name : String) : String = can match {
	 
	  /**
	   * Prams
	   */
	  case "3 Wheel Prams" => "Prams"
	  case "4 Wheel Prams" => "Prams"
	  case "Compact Prams" => "Prams"
	  case "Strollers" => "Prams"
	  case "Twins & Triplets" => "Prams"
	  case "Tandems" => "Prams"
	   
	  /**
	   * Prams Accessories
	   */
	  case "Pram Accessories" => "Pram Accessories"

	  /**
	   * Car Seats
	   */
	  case "Capsules 0 - 6 mths" => "Car Seats"
	  case "Convertibles 0 - 4 yrs" => "Car Seats"
	  case "Boosters 6 mths - 7 yrs" => "Car Seats"

	  /**
	   * Car Seats Accessories
	   */
	  case "Car Seat Accessories" => "Car Seat Accessories"
	 
	  /**
	   * Car Accessories
	   */
	  case "Car Accessories" => "Car Accessories"

	  /**
	   * Nappy Bags
	   */
	  case "Nappy Bags" => "Nappy Bags"
	   
	  /**
	   * Baby Carriers
	   */
	  case "Slings" => "Baby Carriers"
	  case "Baby Carriers" => "Baby Carriers"
	    
	  /**
	   * Cots
	   */
	  case "Portable Cots" => "Cots"
	  case "Cots" => "Cots"
	  case "Playpens" => "Cots"
	    
	  /**
	   * Beds
	   */
	  case "Beds" => "Beds"
	  case "Cradles & Bassinets" => "Beds"
	  case "Bed Rails" => "Beds"
	    
	  /**
	   * Furniture
	   */
	  case "Drawer" => "Furniture"
	  case "Bookcase" => "Furniture"
	  case "Furniture" => "Furniture"
	  case "Storage" => "Furniture"
	  case "Glider Chairs" => "Furniture"
	  case "Mattress" => "Furniture"
	  case "Room Decorator" => "Furniture"
	  case "Portable Chairs" => "Furniture"
	  case "Kids Play Furniture" => "Furniture"
	    
	  /**
	   * Change Tables
	   */
	  case "Change Tables" => "Change Tables"
	  case "Bath Change Center" => "Change Tables"
	    
	  /**
	   * Sheets
	   */
	  case "Sheets" => "Sheets"
	  case "Designer Quilts & Coverlets" => "Quilts"
	  case "Changepad Covers" => "Quilts"
	  case "Quilts & Underlays" => "Quilts"

	  case "Nursery Collections" => "Baby Accessories"
	  case "Bumpers" => "Baby Accessories"

	  case "Towels & Face Washers" => "Face Washers"
	  case "Blankets" => "Blankets"
	  case "Pillows" => "Pillows"

	  /**
	   * sleeping suit
	   */
	  case "Swaddling" => "Sleepingsuits"
	  case "Mattress Protectors" => "Sleepingsuits"
	  case "Sleep Positioners" => "Sleepingsuits"
	  case "Sleeping Bags / Sleepsuits" => "Sleepingsuits"

	  /**
	   * baby wear
	   */
	  case "Premmie" => "Babywear"
	  case "Babywear" => "Babywear"

	  case "Underwear" => "Underwear"
	  case "Outerwear" => "Outerwear"

	  case "Clothing Accessories" => "Clothing Accessories"
	  case "Accessories" => "Clothing Accessories"

	  /**
	   * Feeding 
	   */
	  case "Bibs" => "Feeding Accessories"
	  case "Breast Feeding" => "Breast Feeding"
	  case "Bottle Feeding" => "Bottles & Teats"
	  case "Feeding & Weaning" => "Bottles & Teats"
	  case "Drying & Cleaning" => "Bath Accessories"
	  case "Soothers" => "Feeding Accessories"
	  case "Highchairs" => "High Chairs"
	  case "Food" => "Baby Food"
	    
	  /**
	   * Furniture Accessories
	   */
	  case "Highchair Accessories" => "Furniture Accessories"
	  case "Safety Gates" => "Furniture Accessories"
	  case "Safety Products" => "Furniture Accessories"

	  case "Monitors" => "Baby Accessories"
	    
	  /**
	   * Health 
	   * Todo : need for further operation
	   */
	  case "Health" => "Medicine & Vitamins"
	    
	  /**
	   * toys
	   */
	  case "Playgyms" => "Outdoors"
	  case "Outdoor Play" => "Outdoors"

	  case "Soft Plush Toys" => "Indoors"
	  case "ELC" => "Indoors"
	  
	  case "Early Development Toys" => "Intelligence Development"
	  case "Interactive Toys" => "Intelligence Development"

	  case "Trikes" => "Walkers"
	  case "Walkers" => "Walkers"
	    
	  case "Rockers & Bouncers" => "Swings"
	  case "Swings" => "Swings"

	  case "Card & Wrap" => "Indoors"
	  case "Books & CD's" => "Books & CD's"

	  case "Gifts" => "Baby Accessories"
	  
	  case _ => can
	}
}