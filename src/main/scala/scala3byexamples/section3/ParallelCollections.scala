package scala3byexamples.section3

import scala.collection.parallel.*
import scala.collection.parallel.CollectionConverters.*
import scala.collection.parallel.immutable.ParVector
object ParallelCollections {

  val aList = (1 to 1000000).toList
  val anIncreamentedList = aList.map(_ + 1)
  val parList: ParSeq[Int] = aList.par
  val aParallelizedIncreamentedList =
    parList.map(_ + 1) // map, flatMap, filter, foreach, reduce, fold
  /*
  Applicable for
  - Seq
  - Vector
  - Arrays
  - Maps
  -Sets

  Use-case: faster processing
   */

  // parallel collection build explicitly
  val aParVector = ParVector[Int](1, 2, 3, 4, 5, 6)

  def measure[A](expression: => A): Long = {
    val time = System.currentTimeMillis()
    expression
    System.currentTimeMillis() - time
  }

  def compareListTransformation(): Unit = {
    val list = (1 to 20000000).toList
    println("list creation done")
    val serialTime = measure(list.map(_ + 1))
    println(s"serial time ; $serialTime")
    val parallelTime = measure(list.par.map(_ + 1))
    println(s"parallel  time ; $parallelTime")

  }

  def demoRaceConditions(): Unit = {
    var sum = 0
    (1 to 1000).toList.par.foreach(elem => sum += elem)
println(sum)
  }
  def main(args: Array[String]): Unit = {
    demoRaceConditions()
    //compareListTransformation()
  }
}
