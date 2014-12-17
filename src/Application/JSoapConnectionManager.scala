package Application

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object JSoapConnectionManager {
	def apply(url : String, times : Int = -1) : Document = { 

		var html : Document = null
		var attmps = times
		while (attmps != 0) {
			try {
				html = Jsoup.connect(url).timeout(10000).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get
				attmps = 0
			} catch {
				case e : java.net.SocketTimeoutException => ScraperApp.printer.writeLine("timeout reconnecting..."); attmps = attmps - 1
				case _ => ScraperApp.printer.writeLine("some error with this url : \n\t" + url); attmps = 0
			}
		}
		
		html
	}
}
