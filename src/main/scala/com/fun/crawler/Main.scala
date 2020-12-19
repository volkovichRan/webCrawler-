
package com.fun.crawler

import java.io.{File, PrintWriter}
import java.nio.file.{FileSystems, Files, Path}

import com.fun.crawler.model.Site
import com.fun.crawler.output.TSVFileForSite
import com.fun.crawler.utils.UrlUtils
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
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
 * i didnt encode the depth into storage should have
 * i should have calc the ration in the end and not in every store
 */
object Main extends App {

  /**
   * Configuration vars
   */
  val storeDirectoryPath: String = "/tmp/lightricks/"
  val outputFilePath = Path.of("/tmp/output.csv")
  val headers = Seq(
    "url",
    "depth",
    "ration"
  )

  var sites = {
    val dir = FileSystems.getDefault.getPath(storeDirectoryPath)
    Files.walk(dir).iterator().asScala.filter(Files.isRegularFile(_)).map(site => Site(site.toString)).toSeq
  }

  def removeProtocol(url: String): String = url.replaceFirst("^(http[s]?://)", "")

  def storeSite(site: Site): Unit = {
    val filePath = site.transformUrl()
    val file = new File(filePath)
    Try(new PrintWriter(file)) match {
      case Success(pw) =>
        pw.print(site.html)
        pw.close()
      case Failure(exception) =>
        println(filePath)
    }


  }

  def calcRation(url: String, sites: Iterable[Site]): Double = {
    val baseUrl = sites.map(site => UrlUtils.toBaseUrl(site.url))
    val countUrl = baseUrl.count(_ == url)
    countUrl / sites.size.toDouble
  }

  def crawl(url: String, depth: Int, basePath: String = storeDirectoryPath): Unit = {

    def treatNewSite(document: Document, site: Site) = {
      val newSite = site.copy(html = document.toHtml, ration = calcRation(UrlUtils.toBaseUrl(site.url), sites))
      storeSite(newSite)
      sites = sites :+ site
    }

    val site = Site(url, depth)
    if (!sites.exists(s => s.transformUrl() == site.transformUrl())) {
      val browser = JsoupBrowser()
      Try(browser.get(url)) match {
        case Success(document) =>
          depth match {
            case 1 =>
              treatNewSite(document, site)
            case _ =>
              treatNewSite(document, site)
              UrlUtils.getLinksOfPage(document).foreach(crawl(_, depth - 1))
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
  crawl("https://doc.akka.io/docs/akka-http/current/introduction.html", 5)
  new TSVFileForSite(sites, headers, outputFilePath.toString).output()

}
