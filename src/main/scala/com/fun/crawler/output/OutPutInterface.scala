package com.fun.crawler.output

import com.fun.crawler.utils.OutPutObj

abstract class OutPutInterface[DATA <: OutPutObj, OUTPUT](lines:Seq[DATA], header: Seq[String]) {

  def output():OUTPUT

}
