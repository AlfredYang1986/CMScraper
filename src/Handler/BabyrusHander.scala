package Handler

class BabyrusHandler extends ToysrusHandler {
	override def name = "Baby r us"
	override def catMapping(cat1 : String, name :String, cat2 : String) : String = cat2
}

class BabyrusHomeHandler extends ToysrusHandler {
	override def name = "Baby r us home"
	override def catMapping(cat1 : String, name :String, cat2 : String) : String = cat2
}