package com.fun.crawler.output

abstract class OutPutInterface[DATA <: OutPutObj, OUTPUT](lines:Iterable[DATA], header: Seq[String]) {

  def output():OUTPUT

}
