package com.fun.crawler.output


import java.io.File

import com.fun.crawler.model.Site
import com.fun.crawler.utils.FileUtils


class TSVFileForSite(lines: Seq[Site], headers: Seq[String], outputPath: String) extends OutPutInterface[Site, Unit](lines, headers) {

  val delimiter: String = "\t"

  override def output(): Unit = {
      val allFileLines = Seq(headers.mkString(delimiter)) ++ lines.map(_.toOutPut().mkString(delimiter))
      FileUtils.writeFile(outputPath, allFileLines).fold(onFailure,  onSuccess)
  }

  def onSuccess(file: File): Unit = {
    val filePath = file.getAbsolutePath
    println(s"File output is created at path: $filePath")
  }

  def onFailure(throwable: Throwable): Unit = {
    println(throwable.getMessage)
  }

}
