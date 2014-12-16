package Application

import Components.WebsiteProxy

case class ThreadsManager(proxy : WebsiteProxy) extends Runnable {

	override def run() : Unit = proxy.apply
}
