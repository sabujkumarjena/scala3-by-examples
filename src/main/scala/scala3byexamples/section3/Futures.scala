package scala3byexamples.section3

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object Futures {

  def longComputation(): Int = {
    // simulate long comutation
    Thread.sleep(1000)
    21
  }
  // thread pool (Java-specific)
  val executor = Executors.newFixedThreadPool(4)
  // ExecutionContext is scala native thread pool that run Futers
  // thread pool (Scala- specific)
  given executionContext: ExecutionContext =
    ExecutionContext.fromExecutorService(executor)

  // a future is an async computation that will finish at some point

  // Option[Try[Int]], because
  // - we don't know if we have a value
  // - if we do, that can be a failed computation
  val aFuture: Future[Int] = Future.apply(longComputation())(executionContext)

  val futureInstantResult: Option[Try[Int]] =
    aFuture.value // inspect the value of the future RIGHT NOW

    // callbacks
  aFuture.onComplete {
    case Success(value) =>
      println(s" I have completed with value : $value")
    case Failure(exception) =>
      println(s"My async computation failed: $exception")
  } // callback is executed some other thread (may be same thread)
  def main(args: Array[String]): Unit = {
    println(futureInstantResult)
    Thread.sleep(2000)
    executor.shutdown()
  }

}
