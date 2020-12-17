package com.fun.crawler.output


import com.fun.crawler.model.Site
import com.fun.crawler.utils.FileUtils


class TSVFileForSite(lines: Seq[Site], headers: Seq[String], outputPath: String) extends OutPutInterface[Site, Unit](lines, headers) {

  val delimiter: String = "\t"

  override def output(): Unit = {
      val allFileLines = Seq(headers.mkString(delimiter)) ++ lines.map(_.toOutPut().mkString(delimiter))
      FileUtils.writeFile(outputPath, allFileLines)
  }
}
