package de.hpi.mocc

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala._
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.backuity.clist.{CliMain, opt}
import org.apache.flink.api.java.functions.FunctionAnnotation.ForwardedFields
import org.apache.flink.configuration.Configuration

import scala.collection.JavaConverters._

object CellCluster extends CliMain[Unit] {

  var input: String = opt[String](default = "./data/opencellid_data/berlin.csv")
  var output: String = opt[String](default = "./out/clusters.csv")
  var iterations: Int = opt[Int](default = 10)
  var mnc: Seq[Int] = opt[Seq[Int]](default = Seq())
  var k: Int = opt[Int](default = -1)

  def run: Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    val towers = env.readCsvFile[Tower](input, ignoreFirstLine = true)
    val gsmUMTSTowers = towers.filter(tower =>
      tower.radio == "GSM" || tower.radio == "UMTS" && (mnc.isEmpty || mnc.contains(tower.net)))
    val lteTowers = towers.filter(tower => tower.radio == "LTE")
    k = if (k != -1) k else lteTowers.count().toInt

    val points = gsmUMTSTowers.map(tower => Point(tower.lon, tower.lat))
    val centroids = lteTowers
      .first(k)
      .map(tower => Centroid(tower.cell, tower.lon, tower.lat))

    val finalCentroids = centroids.iterate(iterations)({ currentCentroids =>
      points
        .map(new SelectNearestCenter).withBroadcastSet(currentCentroids, "centroids")
        .map { x => (x._1, x._2, 1L) }.withForwardedFields("_1; _2")
        .groupBy(0)
        .reduce { (p1, p2) => (p1._1, p1._2.add(p2._2), p1._3 + p2._3) }.withForwardedFields("_1")
        .map { x => new Centroid(x._1, x._2.div(x._3)) }.withForwardedFields("_1->id")
    })

    val clusteredPoints =
      points
        .map(new SelectNearestCenter)
        .withBroadcastSet(finalCentroids, "centroids")
          .map(out => (out._1, out._2.x, out._2.y))

    clusteredPoints.writeAsCsv(output, "\n", ",").setParallelism(1)
    env.execute("CellCluster")
  }
}

// *************************************************************************
//     DATA TYPES
// *************************************************************************

case class Tower (
  radio: String,
  mcc: Int,
  net: Int,
  area: Int,
  cell: Int,
  unit: Int,
  lon: Double,
  lat: Double,
  range: Int,
  samples: Int,
  changeable: Int,
  created: Int,
  updated: Int,
  averageSignal: Int
)

trait Coordinate extends Serializable {

  var x: Double
  var y: Double

  def add(other: Coordinate): this.type = {
    x += other.x
    y += other.y
    this
  }

  def div(other: Long): this.type = {
    x /= other
    y /= other
    this
  }

  def euclideanDistance(other: Coordinate): Double =
    Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y))

  def clear(): Unit = {
    x = 0
    y = 0
  }

  override def toString: String =
    s"$x $y"

}

case class Point(var x: Double = 0, var y: Double = 0) extends Coordinate

case class Centroid(var id: Int = 0, var x: Double = 0, var y: Double = 0) extends Coordinate {

  def this(id: Int, p: Point) {
    this(id, p.x, p.y)
  }

  override def toString: String =
    s"$id ${super.toString}"

}

/** Determines the closest cluster center for a data point. */
@ForwardedFields(Array("*->_2"))
final class SelectNearestCenter extends RichMapFunction[Point, (Int, Point)] {
  private var centroids: Traversable[Centroid] = null

  /** Reads the centroid values from a broadcast variable into a collection. */
  override def open(parameters: Configuration) {
    centroids = getRuntimeContext.getBroadcastVariable[Centroid]("centroids").asScala
  }

  def map(p: Point): (Int, Point) = {
    var minDistance: Double = Double.MaxValue
    var closestCentroidId: Int = -1
    for (centroid <- centroids) {
      val distance = p.euclideanDistance(centroid)
      if (distance < minDistance) {
        minDistance = distance
        closestCentroidId = centroid.id
      }
    }
    (closestCentroidId, p)
  }

}