package Application

import DAO.ScraperCache
import com.mongodb.casbah.Imports._
import java.io.PrintWriter
import java.io.FileWriter

object ScraperApp extends App {
	
	/**
	 * Scraper system log
	 */
	object printer {
	
		var pw : Map[String, PrintWriter] = Map.empty
	  
		def writeLine(str : String, path : String = "") = {
			if (path == "") println(str)
			else {
				val p = pw.get(path)
				if (p.isEmpty) {
					val np = new PrintWriter(new FileWriter(path, true))
					pw += path -> np
					np.write(str + "\n")
				} else p.get.write(str + "\n")
			}
		}
		
		def write(str : String, path : String = "") {
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
	
	/**
	 * Scraper system started
	 */
	printer.writeLine("Scraper running ...")
	BrandList("src/Config/BabyBrands.xml")
	CateList("src/Config/babyCate.xml")
	WebsiteProxyConfigRender("src/Config/Config.xml") map { x => val t = new Thread(new ThreadsManager(x)); t.start; t.join }
	ScraperCache.refresh()
	BrandList.save
	CateList.save
}
