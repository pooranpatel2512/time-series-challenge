package com.pooranpatel

object Exceptions {
  case object InputFileIsNotProvided extends RuntimeException {
    override def getMessage: String = "Input file for time series data is not provided."
  }
  case class CorruptedDataPoint(dataPoint: String) extends RuntimeException {
    override def getMessage: String = s"Corrupted data point = $dataPoint"
  }
}
