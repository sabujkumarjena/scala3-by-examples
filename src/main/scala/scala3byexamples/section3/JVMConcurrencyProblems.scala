package scala3byexamples.section3

object JVMConcurrencyProblems {

  def runInParallel(): Unit = {
    var x = 0

    val thread1 = new Thread(() => x = 1)

    val thread2 = new Thread(() => x = 2)

    thread1.start()
    thread2.start()
    println(x) // race condition
  }

  case class BankAccount(var amount: Int)

  def buy(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    /*
    involves 3 steps:
    - read old value
    - compute result
    - write new value
     */
    bankAccount.amount -= price
  }

  def buySafe(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    bankAccount
      .synchronized { // does not allow multiple threads to run the critical section AT THE SAME TIME
        bankAccount.amount -= price // critical section
      }
  }
  /*
Example of race condition:
th1(shoe)
- reads amout 50000
-compute result 50000 - 3000 = 47000
th2(iPhone)
-reads amount 50000
-compute result 50000 - 4000 = 46000
th1(shoe)
-writes amount 47000
th2(iPhone)
-writes amount 46000
   */

  def demoBank(): Unit = {
    (1 to 10000).foreach { _ =>
      val account = BankAccount(50000)
      val thread1 = new Thread(() => buy(account, "shoe", 3000))
      val thread2 = new Thread(() => buy(account, "iPhone", 4000))
      thread1.start()
      thread2.start()
      thread1.join()
      thread2.join()
      if (account.amount != 43000)
        println(s"Banking is broken: ${account.amount}")
    }
  }

  /** Exercises 1 - create "inception threads" thread 1 -> thread 2 -> thread 3
    * ..... each thread prints "hello from thread $I" print all messages in
    * reverse order
    *
    * 2- what's the min/max value of x 3- "sleep fallacy"
    */

  // 1- inception threads

  def inceptionThreads(maxThread: Int, i: Int = 1): Thread = new Thread(() => {
    if (i < maxThread) {
      val newThread = inceptionThreads(maxThread, i + 1)
      newThread.start()
      newThread.join()
    }
    println(s"Hello from thread $i")
  })

  // max = 100,
  // min = 1 when all thread read x at same time
  // all threads (in parrallel ) compute 0 + 1 = 1
  // all threads try to writw x = 1
  def minMaxX(): Unit = {
    var x = 0
    val threads = (1 to 100).map(_ => new Thread(() => x += 1))
    threads.map(_.start())
    threads.map(_.join())
    println(x)
  }
//3
  /*
almost always, msg = " Scala ia awsome"
is it guarenteed ? No

main thread:
  msg = " Scala sucks"
  thread.start()
  sleep(1001) - yields execution
thread:
   sleep(1000) - yields execution
OS gives the CPU to some important thread, takes > 2s
OS gives the CPU back to the main thread
main thread:
  println(msg) // "scala sucks"
thread:
  msg = "Scala is awsome"
   */
  def demoSleepFallacy(): Unit = {
    var msg = ""
    val thread = new Thread(() => {
      Thread.sleep(1000)
      msg = "Scala is awesome"
    })
    msg = "Scala sucks"
    thread.start()
    Thread.sleep(1001)
    // solition: join the worker thread
    thread.join()
    println(msg)
  }
  def main(args: Array[String]): Unit = {
    // runInParallel()
    // demoBank()
    // minMaxX()
    demoSleepFallacy()
    // inceptionThreads(10).start()
  }

}
