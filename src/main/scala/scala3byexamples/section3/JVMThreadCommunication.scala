package scala3byexamples.section3

import scala.collection.mutable
import scala.util.Random

object JVMThreadCommunication {
  def main(args: Array[String]): Unit = {

    ProdConsV4.start(2, 2, 4)
  }

}

//example: producer-consumer problem

class SimpleContainer {
  private var value: Int = 0
  def isEmpty: Boolean = value == 0
  def set(newValue: Int): Unit = value = newValue
  def get: Int = {
    val res = value
    value = 0
    res
  }
}

//PC part 1: one producer, one consumer
object ProdConsV1 {
  def start(): Unit = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[consumer] is waiting")
      // busy waiting...
      while (container.isEmpty) {
        println("[consumer] waiting for a value")
      }
      println(s"[consumer] I have consumed a value: ${container.get}")
    })

    val producer = new Thread(() => {
      println("[producer] computing..")
      Thread.sleep(500)
      val value = 42
      println(s"[producer] I am producing after a long work, the value $value")
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

}

//wait + notify
object ProdConsV2 {
  def start(): Unit = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[consumer] is waiting")
      // listen for a value
      container
        .synchronized { // block all other threads trying to "lock" this object
          // thread-safe code
          if (container.isEmpty)
            container.wait() // release the lock + suspend the thread
          // reaquire the lock and continue execution
          println(s"[consumer] I have consumed a value: ${container.get}")
        }

    })

    val producer = new Thread(() => {
      println("[producer] computing..")
      Thread.sleep(500)
      val value = 42
      container.synchronized {
        println(
          s"[producer] I am producing after a long work, the value $value"
        )
        container.set(value)
        container.notify() // awaken ONE suspended thread on this object
      } // release lock
    })

    consumer.start()
    producer.start()
  }

}

//a larger container
//producer -> [___] -> consumer
object ProdConsV3 {
  def start(containerCapacity: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]

    val consumer = new Thread(() => {
      val random = new Random(System.nanoTime())

      while (true) {

        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[consumer] buffer empty,  waiting..")
            buffer.wait()
          }
          // buffer must not be empty
          val x = buffer.dequeue()
          println(s"[consumer] I've just consumed $x")

          // producer..give me more element
          buffer.notify()

        }
        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random(System.nanoTime())
      var count = 0

      while (true) {
        buffer.synchronized {
          if (buffer.size == containerCapacity) {
            println("[producer] buffer full, waiting...")
            buffer.wait()
          }
          // buffer is not empty
          val newElement = count
          count += 1
          println(s"[producer] I 'm producing $newElement")
          buffer.enqueue(newElement)
          // consumer.. consume lazy
          buffer.notify() // wakes up the consumer (if it is asleep)
        }
        Thread.sleep(random.nextInt(500))
      }
    })
    consumer.start()
    producer.start()
  }
}

/*
large container, multple producer/consumers
producer1 -> [_ _ _] -> consumer2
producer2 ->    "    -> consumer1
producer3 ->    "    -> consumer3
producer1 ->    "    -> consumer2
 */
object ProdConsV4 {

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random(System.nanoTime())
      while (true) {
        buffer.synchronized {
          /*
          one producer, 2 consumers
          producer produces 1 value in the buffer
          both consumers are waiting
          producer calls notify, awakens one consumer
          consumer deques, calls notify, awakens the other consumer
          the other consumer awakens, tries dequeueing, CRASH

          so -- we need to have constant check if buffer is empty - scenario
           */
          while (buffer.isEmpty) {
            println(s"[consumer$id] buffer is empty, waiting...")
            buffer.wait()
          }
          // buffer is non-empty

          val item = buffer.dequeue()
          println(s"[consumer$id] I've just consumed $item")
          /*
          Scenario: 2 producers, one consumer, capacity = 1
            producer1 produces a value, then waits
            producers2 sees buffer full, waits
            consumer consumes value, notifies one producer (say producer1)
            producer1 produces a value, calls notify (one consumer, one producer waiting) - signal goes to  producer 2
            producer 1 sees buffer full, waits
            producer 2 sees buffer full, waits
            deadlock
          We ned to use notifyAll
           */
          buffer.notifyAll()

        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int)
      extends Thread {
    override def run(): Unit = {
      var count = 0
      val random = new Random(System.nanoTime())
      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) { // buffer full
            println(s"[producer$id] buffer is full, waiting...")
            buffer.wait()
          }
          // buffer is not full
          println(s"[producer $id] producing $count")
          buffer.enqueue(count)
          buffer.notifyAll()
          count += 1

        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }
  def start(nProducers: Int, nConsumers: Int, containerCapacity: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val producers =
      (1 to nProducers).map(id => new Producer(id, buffer, containerCapacity))
    val consumers = (1 to nConsumers).map(id => new Consumer(id, buffer))
    consumers.map(_.start())
    producers.map(_.start())
  }
}
