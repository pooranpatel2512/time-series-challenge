package com.pooranpatel

import java.text.DecimalFormat

import com.pooranpatel.Exceptions.{ CorruptedDataPoint, InputFileIsNotProvided }
import com.pooranpatel.Model.DataPoint

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.{ Failure, Success, Try }

object MainApp {

  val sumDisplayFormat = new DecimalFormat("##.#####")

  def main (args: Array[String]) {
    val inputFileLines = for {
      inputFileName <- getFileName(args)
      lines <- getLinesFromInputFile(inputFileName)
    } yield lines

    inputFileLines match {
      case Success(lines) =>
        printHeader()
        processTimeSeriesData(lines)
      case Failure(ex) => println(ex.getMessage)
    }
  }

  def processTimeSeriesData(lines: Iterator[String]): Unit = {
    var timeWindow = ListBuffer.empty[DataPoint]
    for (line <- lines) {
      val currentDatapoint = validateDataPoint(line)
      currentDatapoint match {
        case Success(dp) =>
          timeWindow = timeWindow.takeWhile(dp.timeStamp - _.timeStamp <= 60)
          println(formatDataPoint(dp, timeWindow))
          dp +=: timeWindow
        case Failure(ex) =>
          // TODO: Log exception to log file
          // println(ex.getMessage)
      }
    }
  }

  def formatDataPoint(dp: DataPoint, timeWindow: ListBuffer[DataPoint]): String = {
    var count = 1
    var sum, min, max = dp.value
    for(tdp <- timeWindow) {
      count += 1
      sum += tdp.value
      if(tdp.value < min) min = tdp.value
      if(tdp.value > max) max = tdp.value
    }
    s"${dp.timeStamp}\t${dp.value}\t$count\t${sumDisplayFormat.format(sum).padTo(8, " ").mkString}\t$min\t$max"
  }

  def getFileName(args: Array[String]): Try[String] = {
    Try {
      if (args.length == 0) throw InputFileIsNotProvided
      else args(0)
    }
  }

  def getLinesFromInputFile(fileName: String) = Try(Source.fromFile(fileName).getLines())

  def validateDataPoint(dataPointString: String): Try[DataPoint] = {
    Try {
      val rawData = dataPointString.trim.split("\t")
      if(rawData.length == 2) {
        try {
          DataPoint(rawData(0).toLong, rawData(1).toDouble)
        } catch {
          case ex: NumberFormatException => throw new CorruptedDataPoint(dataPointString)
          case ex: Throwable => throw ex
        }
      } else {
        throw new CorruptedDataPoint(dataPointString)
      }
    }
  }

  def printHeader(): Unit = {
    print("T".padTo(10, " ").mkString + "\t")
    print("V".padTo(7, " ").mkString + "\t")
    print("N".padTo(2, " ").mkString + "\t")
    print("RS".padTo(9, " ").mkString + "\t")
    print("MinV".padTo(6, " ").mkString + "\t")
    println("MinV")
    println("---------------------------------------------------")
  }
}