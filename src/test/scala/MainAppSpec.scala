import com.pooranpatel.Exceptions.InputFileIsNotProvided
import com.pooranpatel.MainApp
import com.pooranpatel.Model.DataPoint
import org.scalatest.WordSpec

import scala.collection.mutable.ListBuffer
import scala.util.{ Failure, Success }

class MainAppSpec extends WordSpec {

  "The MainApp" when {
    "receives a call to getFileName method" must {
      "return an exception InputFileIsNotProvided" when {
        "input file is not provided as first command line argument" in {
          // when
          val inputFileName = MainApp.getFileName(Array.empty[String])

          // given
          assert(inputFileName == Failure(InputFileIsNotProvided))
        }
      }
      "return an input file name" when {
        "input file is provided as first command line argument" in {
          // given
          val testInputFileName = "test-input-file-name"

          // when
          val inputFileName = MainApp.getFileName(Array(testInputFileName))

          // then
          assert(inputFileName == Success(testInputFileName))
        }
      }
    }

    "receives a call to getLinesFromInputFile method" must {
      "return an exception FileNotFoundException" when {
        "provided input file does not exists" in {
          // given
          val invalidInputFileName = "invalid-file-name"

          // when
          val inputFileName = MainApp.getLinesFromInputFile(invalidInputFileName)

          // then
          assert(inputFileName.isFailure)
        }
      }
      "return an lines of input file" when {
        "provided input file exists" in {
          // given
          val testInputFileName = "src/test/resources/test-input-file.txt"

          // when
          val inputFileName = MainApp.getLinesFromInputFile(testInputFileName)

          // given
          assert(inputFileName.isSuccess)
        }
      }
    }

    "receives a call to validateDataPoint method" must {
      "return an exception CorruptedDataPoint" when {
        "invalid data point string (without separator) is given" in {
          // given
          val invalidDataPointString = "afadfadfdafsdf"

          // when
          val dataPoint = MainApp.validateDataPoint(invalidDataPointString)

          // then
          assert(dataPoint.isFailure)
        }
      }
      "return an exception CorruptedDataPoint" when {
        "invalid data point string (with string instead of number) is given" in {
          // given
          val invalidDataPointString = "adadafdsdf\t1.80215"

          // when
          val dataPoint = MainApp.validateDataPoint(invalidDataPointString)

          // then
          assert(dataPoint.isFailure)
        }
      }
      "return a valid data point" when {
        "valid data point string is given" in {
          // given
          val dataPointString = "1355270609\t1.80215"

          // when
          val dataPoint = MainApp.validateDataPoint(dataPointString)

          // then
          assert(dataPoint.isSuccess)
        }
      }
    }

    "receives a call to formatDataPoint method" must {
      "return a formatted data point string" in {
        // given
        val dataPoint1 = DataPoint(1355270609, 1.80215)
        val dataPoint2 = DataPoint(1355270621, 1.80185)
        val timeWindow1 = ListBuffer(dataPoint2, DataPoint(1355270646, 1.80195))

        // when
        val formattedDataPoint1 = MainApp.formatDataPoint(dataPoint1, timeWindow1)
        val formattedDataPoint2 = MainApp.formatDataPoint(dataPoint2, ListBuffer.empty[DataPoint])

        // then
        assert(formattedDataPoint1 == "1355270609\t1.80215\t3\t5.40595 \t1.80185\t1.80215")
        assert(formattedDataPoint2 == "1355270621\t1.80185\t1\t1.80185 \t1.80185\t1.80185")
      }
    }
  }
}
