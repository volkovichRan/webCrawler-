package com.fun.crawler.model

import com.fun.crawler.Main
import com.fun.crawler.utils.OutPutObj
import net.ruippeixotog.scalascraper.model.Document


case class Site(url: String, depth: Int, html: String, ration: Double = 0) extends OutPutObj {
  override def toOutPut(): Seq[String] = Seq(url, depth.toString, ration.toString)

  def transformUrl(): String =  Main.configBasePath + url.replace("/", "@")


}
object Site {
  def apply(document: Document, depth : Int): Site = Site(document.location, depth, document.toHtml)
  def apply(url: String, depth : Int): Site = Site(url, depth, "")
}

