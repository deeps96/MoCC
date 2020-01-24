package de.hpi.mocc

import org.apache.flink.api.scala._
import org.backuity.clist._

object WordCount extends CliMain[Unit]{

  var input: String = opt[String](description = "Input File, that should be counted", default = "./data/tolstoy-war-and-peace.txt")
  var output: String = opt[String](description = "Output File, that contains the Word Counts", default = "./out/wordCount.csv")

  def run: Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment

    val counts = env.readTextFile(input)
      .flatMap(line =>
        line
          .toLowerCase
          .split("\\W+"))
      .filter(_.nonEmpty)
      .map { (_, 1) }
      .groupBy(0)
      .sum(1)

    counts.writeAsCsv(output, "\n", ",").setParallelism(1)
    env.execute("Word Count")
  }
}
