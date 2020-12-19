package com.fun.crawler.utils

import com.fun.crawler.Main.removeProtocol
import net.ruippeixotog.scalascraper.model.Document
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import net.ruippeixotog.scalascraper.dsl.DSL._

object UrlUtils {

  def toBaseUrl(url: String):String = removeProtocol(url).split("/")(0)

  def validOrFixUrl(url: String): String = {
    def isStartWithProtocol(url: String): Boolean = url.startsWith("http://") || url.startsWith("https://")

    def fixUrl(url: String): String = "http://" + url

    isStartWithProtocol(url) match {
      case true => url
      case false => fixUrl(url)
    }
  }

  def getLinksOfPage(url: Document): List[String] = {
    val items = url >> elementList("a")
    items.map(_.attrs.getOrElse("href", "")).filterNot(_.isEmpty)
  }

  def isTextHtmlMime(url: String): Boolean = ???

}
