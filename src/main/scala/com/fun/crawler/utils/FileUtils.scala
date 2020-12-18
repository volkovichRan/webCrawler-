package com.fun.crawler.utils

import java.io.{File, PrintWriter}

import scala.util.{Failure, Success, Try}

object FileUtils {

  val newLine: String = "\n"

  def writeFile(path: String, lines: Seq[String]): Either[Throwable, File] = {
    val file = new File(path)
    Try(new PrintWriter(file)) match {
      case Success (pw) =>
        lines.foreach(line => pw.write(line + newLine))
        pw.close()
        Right(file)
      case Failure(error) => Left(error)
    }
  }

}
