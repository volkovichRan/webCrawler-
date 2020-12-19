
package com.fun.crawler

import java.io.{File, PrintWriter}
import java.nio.file.{FileSystems, Files}

import com.fun.crawler.model.{OutPut, Site}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}


/**
 * known issues / comments
 * need to make sure the configBasePath is exist
 * I didnt deal with input validation ex:
 * input less then 1
 * I didnt deal with page not found
 * I used simple Set to for the reason that each url can be only 1 time
 * I check in spray.io and scala-scraper didnt find mime type
 * I didnt deal with out of memory
 *
 */
object Main extends App {

  /**
   * Configuration vars
   */
  val configBasePath: String = "/tmp/lightricks/"

  var sites = {
    val dir = FileSystems.getDefault.getPath(configBasePath)
    Files.walk(dir).iterator().asScala.filter(Files.isRegularFile(_)).map(_.toString).toSet
  }

  def removeProtocol(url: String): String = url.replaceFirst("^(http[s]?://)", "")

  def storeSite(site: Site): Unit = {
    val filePath = site.transformUrl()
    val file = new File(filePath)
    sites += filePath
    Try(new PrintWriter(file)) match {
      case Success(pw) =>
        pw.print(site.html)
        pw.close()
      case Failure(exception) =>
        println(filePath)
    }


  }

  def calcRation(url: String, sites: Iterable[String]): Double = {
    val baseUrl = sites.map(removeProtocol).map(_.split("/")(0))
    val countUrl = baseUrl.count(_ == url)
    countUrl / sites.size.toDouble
  }

  def getLinksOfPage(url: Document): List[String] = {
    val items = url >> elementList("a")
    items.map(_.attrs.getOrElse("href", "")).filterNot(_.isEmpty)
  }

  def validOrFixUrl(url: String): String = {
    def isStartWithProtocol(url: String): Boolean = url.startsWith("http://") || url.startsWith("https://")

    def fixUrl(url: String): String = "http://" + url

    isStartWithProtocol(url) match {
      case true => url
      case false => fixUrl(url)
    }
  }

  def isTextHtmlMime(url: String): Boolean = ???

  def crawl(url: String, depth: Int, basePath: String = configBasePath): Unit = {
    val site = Site(url, depth)
    if (!sites.contains(site.transformUrl())) {
      val browser = JsoupBrowser()
      Try(browser.get(url)) match {
        case Success(document) =>
          depth match {
            case 1 =>
              storeSite(site.copy(html = document.toHtml))
            case _ =>
              storeSite(site.copy(html = document.toHtml))
              getLinksOfPage(document).foreach(crawl(_, depth - 1))
          }
        case Failure(err) =>
          println(url)
      }
    }
    else {
      println(s"$url is already parsed")
    }

  }

  val browser = JsoupBrowser()
  //println(getLinksOfPage(browser.get("https://doc.akka.io/docs/akka-http/current/introduction.html")))
  crawl("https://doc.akka.io/docs/akka-http/current/introduction.html", 2)


}
