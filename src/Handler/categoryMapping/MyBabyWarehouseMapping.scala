package Handler.categoryMapping

object MyBabyWarehouseMapping {
	def apply(can : String, name : String) : String = can match {

	  /**
	   * at home
	   */
	  case "ACTIVITY CENTRES" => "Swings"
	  case "BOUNCERS & ROCKERS" => "Beds"
	  case "BOOSTERS, CHAIRS & TABLES" => "Furniture"
	  case "PLAYPENS" => "Cots"
	  case "SWINGS" => "Swings"
	  case "WALKERS" => "Walkers"
	  	 
	  /**
	   * Bath & Changing
	   */
	  case "BATH SAFETY" => "Bath Accessories"
	  case "BABY SCALES" => "Bath Accessories"
	  case "BATH SEATS & AIDS" => "Bath Accessories"
	  case "BATHS & STANDS" => "Bath Accessories"

	  case "CHANGE PADS" => "Change Tables"
	  case "CHANGE STATIONS" => "Change Tables"
	  case "FOLDING CHANGE TABLES" => "Change Tables"
	  case "HEALTH & FIRST AID" => "First Aid"
	  case "FOR MUM" => "Mum & Mum-To-Be"
	  case "GROOMING" => "Bath Accessories"

	  case "NAPPIES" => "Baby Nappy"
	  case "NAPPY BINS & PAILS" => "Nappy Bags"
	  case "POTTIES & TOILET TRAINER SEATS" => "Toilet"
	  case "SKIN CARE & TOILETRIES" => "Baby Cream"

	  case "STEP STOOLS" => "Intelligence Development"
	   
	  /**
	   * Bedding
	   */
	  case "NIGHT LIGHTS & COMFORT SOUNDS" => "Indoors"
	  case "MANCHESTER & LINEN" => "Indoors"
	  case "MATERNITY PILLOWS" => "Pillows"
	  case "SLEEPING BAGS" => "Sleepingsuits"
	  case "SLEEP POSITIONERS" => "Sleepingsuits"
	  case "SNUGGLE BEDS" => "Beds"
	  case "SWADDLES & WRAPS" => "Sleepingsuits"
	  case "MOBILES" => "Indoors"
	    
	  /**
	   * Car safety
	   */
	  case "NEW ISOFIX COMPATIBLE RANGE" => "Car Seats"
	  case "CAPSULES & BABY CARRIERS (0-12 MONTHS)" => "Car Seats"
	  case "CONVERTIBLE CAR SEATS (0 TO 4 YEARS)" => "Car Seats"
	  case "FORWARD FACING SEAT (6 MONTHS TO 4 YEARS)" => "Car Seats"
	  case "VERSATILE CAR SEAT (0-8 YEARS)" => "Car Seats"
	  case "HARNESSED CAR SEATS (6 MONTHS TO 8YRS)" => "Car Seats"
	  case "CONVERTIBLE BOOSTER SEATS (6 MONTHS TO 6-8 YRS)" => "Car Seats"
	  case "BOOSTER SEATS (4 TO 8 YEARS)" => "Car Seats"
	  case "Car Accessories" => "Car Accessories"
	  case "WINDOW SOX & SHADES" => "Car Accessories"
	    
	  /**
	   * Feeding Time
	   */
	  case "BREASTFEEDING" => "Breast Feeding"
	  case "BOTTLE FEEDING" => "Bottles & Teats"
	  case "BOTTLE WARMERS AND STERILISERS" => "Warmers & Sterililsers"
	  case "BIBS, SMOCKS AND BURP CLOTHS" => "Babywear"
	  case "DISHES, UTENSILS & FEEDING SETS" => "Feeding Accessories"
	  case "DUMMIES & TEETHERS" => "Feeding Accessories"
	  case "FOOD PREP AND STORAGE" => "Feeding Accessories"
	  case "HIGH CHAIRS, BOOSTERS & HOOK ON SEATS" => "High Chairs"
	  case "NURSING PILLOWS" => "Pillows"
	  case "PACKS & GIFT SETS" => "Bottles & Teats"
	  case "TRAINING & TODDLER CUPS/BOTTLES" => "Bottles & Teats"
	    
	  /**
	   * On the Go
	   */
	  case "BACK PACKS & LUNCH BOXES" => "Feeding Accessories"
	  case "BREASTFEEDING COVERS" => "Breastcare"
	  case "CARRIERS & SLINGS" => "Baby Carriers"
	  case "CARRYCOTS & COCOONS" => "Baby Carriers"
	  case "HARNESS & REINS" => "Baby Carriers"
	  case "NAPPY BAGS" => "Nappy Bags"
	  case "PORTACOTS" => "Cots"
	  case "TROLLEY ACCESSORIES" => "Toilet"
	  case "PROTECTION FOR BABY" => "First Aid"
	    
	  /**
	   * Nursery
	   */
	  case "BASSINETS & CRADLES" => "Cots"
	  case "BED CONVERSION KITS" => "Beds"
	  case "BOOKCASES & SHELVES" => "Furniture"
	  case "CHEST & ROBES" => "Furniture"
	  case "CHANGE TABLES" => "Change Tables"
	  case "COTS" => "Cots"
	  case "FURNITURE PACKAGES" => "Furniture"
	  case "MATTRESSES" => "Sheets"
	  case "TIDY DRAWERS & TOY BOXES" => "Furniture"
	  case "GLIDERS & OTTOMANS" => "Furniture"
	  case "NURSERY ACCESSORIES" => "First Aid"	    

	  /**
	   * playtime
	   */
	  case "CDS, DVDS & BOOKS" => "Books & CD's"
	  case "JUMPERS" => "Swings"
	  case "PLAY GYMS & FLOOR MATS" => "Indoors"
	  case "TOYS" => "Indoors"
	  case "GIFTWARE" => "Indoors"
	    
	  /**
	   * Prams
	   */
	  case "3 WHEELERS" => "Prams"
	  case "4 WHEELERS" => "Prams"
	  case "TWIN & TANDEM" => "Prams"
	  case "PRAM ACCESSORIES" => "Prams Accessories"
	    
	  /**
	   * SAFETY
	   */
	  case "BATHROOM SAFETY" => "Bath Accessories"
	  case "BEDGUARDS" => "Beds"
	  case "EDGE & CORNER GUARDS" => "Baby Accessories"
	  case "ELECTRICAL SAFETY" => "Baby Accessories"
	  case "GATES & EXTENSIONS" => "Cots"
	  case "KITCHEN SAFETY" => "Baby Accessories"
	  case "LATCHES & LOCKS" => "Baby Accessories"
	  case "MONITORS" => "Baby Accessories"
	  case "THERMOMETERS" => "Thermometers"	    

	  case _ => "Unknows"
	}
}
