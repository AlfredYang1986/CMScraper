package Application

import DAO.ScraperCache
import com.mongodb.casbah.Imports._
import java.io.PrintWriter
import java.io.FileWriter
import java.util.Scanner
import Database2Excel.Data2ExcelManager

object ScraperApp extends App {
	
	/**
	 * Scraper system log
	 */
	object printer {
	
		var pw : Map[String, PrintWriter] = Map.empty
	  
		def writeLine(str : String, path : String = "") = {
			if (path == "") println(str)
			else {
				println(str)
				val p = pw.get(path)
				if (p.isEmpty) {
					val np = new PrintWriter(new FileWriter(path, true))
					pw += path -> np
					np.write(str + "\n")
				} else p.get.write(str + "\n")
			}
		}
		
		def write(str : String, path : String = "") {
			println(str)
		  	if (path == "") println(str)
			else {
				val p = pw.get(path)
				if (p.isEmpty) {
					val np = new PrintWriter(new FileWriter(path, true))
					pw += path -> np
					np.write(str)
				} else p.get.write(str)
			}
		}
		
		def close(path : String) = {
			val p = pw.get(path)
			if (!p.isEmpty) p.get.close
		}
	}

	def scrapDataFromWeb = {
		/**
		 * Scraper system started
		 */
		printer.writeLine("Scraper running ...")
		BrandList("src/Config/BabyBrands.xml")
		CateList("src/Config/babyCate.xml")
		
		var tls : List[Thread] = Nil
		WebsiteProxyConfigRender("src/Config/Config.xml") map { x => val t = new Thread(new ThreadsManager(x)); t.start; tls = t :: tls }
		tls map (_.join)
		
		ScraperCache.refresh()
		BrandList.save
		CateList.save  
	}
	

	def exportDataToExcel = Data2ExcelManager("test.xls")

	println("Usage: \n 1. press 1 scrap the data from website \n 2. press 2 change database to excel file")
	
	var running = true 
	val scanner = new Scanner(System.in)
	while (running && scanner.hasNext) {
		val n = scanner.nextLine.trim.charAt(0)
		
		if (n == 'q') running = false
		else if (n == '1') scrapDataFromWeb
		else if (n == '2') exportDataToExcel
		else running = true
	}
}
