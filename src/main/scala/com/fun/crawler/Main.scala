
package com.fun.crawler

import java.io.{File, PrintWriter}

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model.Document
import java.nio.file.{FileSystems, Files}

import com.fun.crawler.model.{OutPut, Site}
import com.fun.crawler.model.Site

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
  val protocolRegex = "^(http[s]?://www\\.|http[s]?://|www\\.)"

  var sites = {
    val dir = FileSystems.getDefault.getPath(configBasePath)
    Files.walk(dir).iterator().asScala.filter(Files.isRegularFile(_)).map(_.toString).toSet
  }

  def removeProtocol(url: String): String = url.replaceFirst(protocolRegex, "")

  def storeSite(site: Site, basePath: String = configBasePath): Unit = {
    val filePath = basePath + site.url.replace("/","@")
    val file = new File(filePath)
    if (!sites.contains(filePath)) {
      sites += filePath
      Try(new PrintWriter(file)) match {
        case Success(pw) =>
          pw.print(site.html)
          pw.close
        case Failure(exception) =>
          println(filePath)
      }
    } else {
      println(s"file already parssed $filePath")
    }

  }

  def calcRation(sites :Set[Site]):List[OutPut] = ???

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

  def formatOutPut(value: List[String]): String = value.mkString("\t")

  def crawl(url: String, depth: Int): Unit = {
    val browser = JsoupBrowser()
    Try(browser.get(url)) match {
      case Success(document) =>
        depth match {
          case 1 =>
            storeSite(Site(document, 1))
          case _ =>
            storeSite(Site(document, depth))
            getLinksOfPage(document).foreach(crawl(_, depth - 1))
        }
      case Failure(err) =>
        println(url)
    }

  }

  val browser = JsoupBrowser()
  //println(getLinksOfPage(browser.get("https://doc.akka.io/docs/akka-http/current/introduction.html")))
  crawl("https://doc.akka.io/docs/akka-http/current/introduction.html", 2)


}
