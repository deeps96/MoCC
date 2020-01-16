package de.hpi.mocc

import org.apache.flink.api.scala.ExecutionEnvironment
import org.backuity.clist._

object WordCount extends CliMain[Unit]{

  var inputFile: String = opt[String](description = "Input File, that should be counted", default = "./data/tolstoy-war-and-peace.txt")
  var outputFile: String = opt[String](description = "Output File, that contains the Word Counts", default = "./out/wordCount.txt")

  def run: Unit = {
    // set up the batch execution environment
    val env = ExecutionEnvironment.getExecutionEnvironment
    env.execute("Flink Batch Scala API Skeleton")
  }
}
