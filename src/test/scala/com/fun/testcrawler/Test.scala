package com.fun.testcrawler

import com.fun.crawler.Main
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.scalatest.flatspec.AnyFlatSpec

class Test extends AnyFlatSpec {

  def assert[T](expectedResult: T, result: T) = {
    assertResult(expectedResult)(result)
  }

  "write tab separated value " should  "look like excepted " in {
    val expectedResult = "a\tb\tc"
    val result = Main.formatOutPut(List("a","b","c"))
    assertResult(expectedResult)(result)
  }

  ignore  should "return true for text/html other false" in {
    val urlList = List("http://www.google.com", "https://middycdn-a.akamaihd.net/bootstrap/bootstrap.js", "www.facebook.com")
    val expectedResult = List(true, false, true)
    val result = urlList.map(Main.isTextHtmlMime)
    assert(expectedResult, result)
  }

  "url are missing protocol " should "fix the url" in {
    val urlList = List("http://www.google.com", "https://middycdn-a.akamaihd.net/bootstrap/bootstrap.js", "www.facebook.com")
    val expectedResult = List("http://www.google.com", "https://middycdn-a.akamaihd.net/bootstrap/bootstrap.js", "http://www.facebook.com")
    val result = urlList.map(Main.validOrFixUrl)
    assert(expectedResult, result)
  }

  "https://doc.akka.io/docs/akka-http/current/introduction.html" should "have 137 links" in {
    val expectedResult = 137
    val browser = JsoupBrowser()
    val site = browser.get("https://doc.akka.io/docs/akka-http/current/introduction.html")
    val result = Main.getLinksOfPage(site).length
    assert(expectedResult, result)
  }

}
