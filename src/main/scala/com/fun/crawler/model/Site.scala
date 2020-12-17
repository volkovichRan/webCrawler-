package com.fun.crawler.model

import net.ruippeixotog.scalascraper.model.Document


case class Site(url: String, depth: Int, html: String)
object Site {
  def apply(document: Document, depth : Int): Site = Site(document.location, depth, document.toHtml)
}

