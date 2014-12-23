package Scraper

class BabyrusScraper(p : String, h : String) extends ToysrusScraper(p, h) {
	override def name = "Baby r us"
}

class BabyrusHomeScraper(p : String, h : String) extends ToysrusScraper(p, h) {
	override def name = "Baby r us home"
}