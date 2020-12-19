package com.fun.testcrawler

import java.nio.file.{Files, Path}

import com.fun.crawler.Main
import com.fun.crawler.model.Site
import com.fun.crawler.output.TSVFileForSite
import com.fun.crawler.utils.UrlUtils
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.scalatest.flatspec.AnyFlatSpec

class Test extends AnyFlatSpec {

  def assert[T](expectedResult: T, result: T) = {
    assertResult(expectedResult)(result)
  }

  "write tab separated value " should "look like excepted " in {
    val filePath = Path.of("/tmp/output.csv")
    val headers = Seq(
      "url",
      "depth",
      "ration"
    )
    val sites = Seq(
      Site("http://www.google.com", 1),
      Site("http://www.facebook.com", 2)
    )
    new TSVFileForSite(sites, headers, filePath.toString).output()
    assertResult(true)(filePath.toFile.exists)
    assert(75, Files.size(filePath))
  }

  /*ignore  should "return true for text/html other false" in {
    val urlList = List("http://www.google.com", "https://middycdn-a.akamaihd.net/bootstrap/bootstrap.js", "www.facebook.com")
    val expectedResult = List(true, false, true)
    val result = urlList.map(Main.isTextHtmlMime)
    assert(expectedResult, result)
  }*/

  "url are missing protocol " should "fix the url" in {
    val urlList = List("http://www.google.com", "https://middycdn-a.akamaihd.net/bootstrap/bootstrap.js", "www.facebook.com")
    val expectedResult = List("http://www.google.com", "https://middycdn-a.akamaihd.net/bootstrap/bootstrap.js", "http://www.facebook.com")
    val result = urlList.map(UrlUtils.validOrFixUrl)
    assert(expectedResult, result)
  }

  "https://doc.akka.io/docs/akka-http/current/introduction.html" should "have 136 links" in {
    val expectedResult = 136
    val browser = JsoupBrowser()
    val site = browser.get("https://doc.akka.io/docs/akka-http/current/introduction.html")
    val result = UrlUtils.getLinksOfPage(site).length
    assert(expectedResult, result)
  }

  "ration" should "be right" in {
    val sites = Seq(
      Site("http://www.foo.com/a.html "),
      Site("http://www.foo.com/b/c.html "),
      Site("http://baz.foo.com/"),
      Site("http://www.google.com/baz.html")
    )

    val result = Main.calcRation("www.foo.com", sites)
    val expectedResult = 0.5
    assert(expectedResult, result)
  }


}
