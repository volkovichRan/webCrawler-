package com.fun.crawler.model

import com.fun.crawler.Main
import com.fun.crawler.output.OutPutObj
import net.ruippeixotog.scalascraper.model.Document


case class Site(url: String, depth: Int, html: String, ration: Double = 0) extends OutPutObj {
  override def toOutPut(): Iterable[String] = Seq(url, depth.toString, ration.toString)

  def transformUrl(): String =  Main.storeDirectoryPath + url.replace("/", "@")


}
object Site {
  def apply(document: Document, depth : Int): Site = Site(document.location, depth, document.toHtml)
  def apply(url: String, depth : Int): Site = Site(url, depth, "")
  def apply(url: String): Site = Site(url, 0 , "")
}

