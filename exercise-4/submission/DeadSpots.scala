package de.hpi.mocc

import org.apache.flink.api.scala.DataSet
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector
import org.backuity.clist.{CliMain, opt}

import math._

object DeadSpots extends CliMain[Unit] {
  var input: String = opt[String](default = "./data/opencellid_data/berlin.csv")
  var output: String = opt[String](default = "./out/deadspots.csv")
  var spots: String = opt[String](default = "./data/testspots.csv")
  var mnc: String = opt[String](default = "")

  implicit def bool2int(b:Boolean) = if (b) 1 else 0

  val R = 6371e3

  def haversine(lat1:Double, lon1:Double, lat2:Double, lon2:Double)={
    val dLat=(lat2 - lat1).toRadians
    val dLon=(lon2 - lon1).toRadians

    val a = (sin(dLat/2) * sin(dLat/2)) + (cos(lat1.toRadians) * cos(lat2.toRadians) * sin(dLon/2) * sin(dLon/2))
    val c = 2 * atan2(sqrt(a), sqrt(1-a))
    R * c
  }

  def run: Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    val parsedMnc = if(mnc.length > 0) mnc.split(",").map(_.toInt) else Array[String]()
    val cells = env.readCsvFile[Cell](input, ignoreFirstLine = true, includedFields = Array(0,2,4,6,7,8))
        .filter(cell => parsedMnc.isEmpty || parsedMnc.contains(cell.net))

    val testSpots = env.readCsvFile[Spot](spots, ignoreFirstLine = true)

    val spotsWithTowers = testSpots.cross(cells) {
      (spot, cell) =>
        val dist = haversine(spot.lat, spot.lon, cell.lat, cell.lon)
        (spot.lon, spot.lat, cell.range, dist, cell.radio)
    }.filter(entry => entry._4 <= entry._3)
    
    val spotsWithTheirRadios: DataSet[(Double, Double, Set[String])] = spotsWithTowers
        .groupBy("_1","_2")
        .combineGroup{
          (spots, out: Collector[(Double, Double, Set[String])]) =>
            var radios = Set[String]()
            var lat: Double = 0.0
            var lon: Double = 0.0
            for(spot <- spots){
              lon = spot._1
              lat = spot._2
              radios += spot._5
            }
            out.collect((lon, lat, radios))
        }

    val deduplicate: DataSet[(Double, Double, Set[String])] = spotsWithTheirRadios.groupBy("_1", "_2")
      .reduceGroup {
      (spots, out: Collector[(Double, Double, Set[String])]) =>
        var unionedRadios = Set[String]()
        var lat: Double = 0.0
        var lon: Double = 0.0
        for(spot <- spots){
          lon = spot._1
          lat = spot._2
          unionedRadios = unionedRadios.union(spot._3)
        }
        out.collect((lon, lat, unionedRadios))
    }

    val result: DataSet[(Double, Double, Int, Int, Int)] = deduplicate
        .map(elem => (elem._1, elem._2, elem._3.contains("GSM"):Int, elem._3.contains("UMTS"):Int, elem._3.contains("LTE"):Int))

    result.writeAsCsv(output, "\n", ",").setParallelism(1)
    env.execute("Flink Streaming Scala API Skeleton")
  }
}

case class Cell(
  radio: String,
  net: Int,
  cell: Int,
  lon: Double,
  lat: Double,
  range: Int)

case class Spot(
  lon: Double,
  lat: Double)
