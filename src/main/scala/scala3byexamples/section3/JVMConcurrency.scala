package scala3byexamples.section3

import java.util.concurrent.Executors

object JVMConcurrency {
  def basicThreads(): Unit = {
    val runnable = new Runnable:
      override def run(): Unit =
        println("waiting")
        Thread.sleep(5000)
        println("running on some thread")
    // threads on JVM
    val aThread = new Thread(runnable)
    aThread.start() // will run the runnable on some JVM thread
    // JVM thread == OS thread (soon to change via Project Loom
    Thread.sleep(2000)
    aThread.join()
    println("main thread")
  }

  // order of operations is NOT guaranteed
  def orderOfExecution(): Unit = {
    val helloThread = new Thread(() =>
      (1 to 100).foreach(_ => println("hello"))
    )
    val byeThread = new Thread(() => (1 to 100).foreach(_ => println("bye")))
    helloThread.start() // non blocking call
    byeThread.start() // non blocking call
  }

  // executors
  def demoExecutors(): Unit = {
    val threadPool = Executors.newFixedThreadPool(4)
    // submit a computation
    threadPool.execute(() => println("something in thread pool - 1"))
    threadPool.execute(() => {
      println("something in thread pool-2")
      Thread.sleep(1000)
      println("done after one second")
    })

    threadPool.execute { () =>
      println("something in thread pool-3")
      Thread.sleep(1000)
      println("almost done")
      Thread.sleep(1000)
      println("done after 2 second")
    }

    threadPool.execute(() => {
      println("something in thread pool-4")
      Thread.sleep(1000)
      println("done after one second")
    })
    threadPool.execute(() => {
      println("something in thread pool-5")
      Thread.sleep(1000)
      println("done after one second")
    })
    threadPool.execute{() =>
      println("something in thread pool-6")
      Thread.sleep(1000)
      println("done after one second")
    }
    threadPool.shutdown()
    // threadPool.execute(() => println("This should not appear")) // should throw an exception "RejectedExecutionException" in the calling thread
  }
  def main(args: Array[String]): Unit = {
    // basicThreads()
    // orderOfExecution()
    demoExecutors()
  }
}
