package scala3byexamples.section3

import java.util.concurrent.Executors
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration.*
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

  case class Profile(id: String, name: String) {
    def sendMessaage(anotherProfile: Profile, msg: String) =
      println(s"${this.name} sending message to ${anotherProfile.name}:  $msg")

  }
  object SocialNetwork {
    // "databases
    val names = Map(
      "id1" -> "Bob",
      "id2" -> "Alice",
      "id3" -> "John",
      "id4" -> "Jack"
    )
    val friends = Map(
      "id1" -> "id2"
    )
    val random = new Random()
    // "API"
    def fetchProfile(id: String): Future[Profile] = Future {
      // fetch from the database
      Thread.sleep((random.nextInt(300)))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {

      Thread.sleep((random.nextInt(400)))
      val bestFriendId = friends(profile.id)
      Profile(bestFriendId, names(bestFriendId))
    }
  }

  // problem : sending to a message to my best friend
  def sendMessageToBestFriend(id: String, msg: String): Unit = {
    // 1- call fetchProfile
    // 2- call fetchBestFriend
    // 3- call profile.sendMessage(bestFriend)
    val profileFuture = SocialNetwork.fetchProfile(id)
    profileFuture.onComplete {
      case Success(profile) => // TODO step 2 and 3
        val friendProfileFuture = SocialNetwork.fetchBestFriend(profile)
        friendProfileFuture.onComplete {
          case Success(friendProfile) =>
            profile.sendMessaage(friendProfile, msg)
          case Failure(ex) => ex.printStackTrace()
        }
      case Failure(ex) => ex.printStackTrace()
    }
  }

  // onComplete is a hassle
  // solution: functional composition
  def sendMessageToBestFriend_v2(id: String, msg: String): Unit = {
    SocialNetwork
      .fetchProfile(id)
      .flatMap(profile =>
        SocialNetwork
          .fetchBestFriend(profile)
          .map(bestFriend => profile.sendMessaage(bestFriend, msg))
      )

  }

  def sendMessageToBestFriend_v3(id: String, msg: String): Unit =
    for {
      profile <- SocialNetwork.fetchProfile(id)
      bestFriend <- SocialNetwork.fetchBestFriend(profile)
    } yield profile.sendMessaage(bestFriend, msg)

  // fallbacks
  val recoverProfile: Future[Profile] =
    SocialNetwork.fetchProfile("unknown id").recover { case e: Throwable =>
      Profile("dummy-id", "dummy-name")
    }

  // if both futures fail, exception is from second future
  val recoverWithProfile: Future[Profile] =
    SocialNetwork.fetchProfile("unknown id").recoverWith { case e: Throwable =>
      SocialNetwork.fetchProfile("dummy-id")
    }

  // if both futures fail, exception is from first future

  val fallBackProfile: Future[Profile] = SocialNetwork
    .fetchProfile("unknown id")
    .fallbackTo(SocialNetwork.fetchProfile("dummy-id"))

  /*
  Block for a future
   */

  case class User(name: String)
  case class Transaction(
      sender: String,
      receiver: String,
      amount: Double,
      status: String
  )

  object BankingApp {
    // "APIs"

    def fetchUser(name: String): Future[User] = Future {
      // simulate database
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(
        user: User,
        mechantName: String,
        amount: Double
    ): Future[Transaction] = Future {
      // simulate payment
      Thread.sleep(1000)
      Transaction(user.name, mechantName, amount, "SUCCESS")
    }

    // external api //synchrounous not best design
    def purchase(
        userName: String,
        item: String,
        merchantName: String,
        price: Double
    ): String = {
      /*
      1. fetch user
      2. create transaction
      3.WAIT for the txn to finish
       */
      val transactionStatusFuture: Future[String] = for {
        user <- fetchUser(userName)
        transaction <- createTransaction(user, merchantName, price)
      } yield transaction.status

      // blocking call
      Await.result(transactionStatusFuture, 2.seconds)
      // throws TimeoutException if the future doesn't finish within 2s

    }
  }

  /*
  Promises
   */
  def demoPromise(): Unit = {
    val promise = Promise[Int]()
    val futureInside: Future[Int] = promise.future

    // thread 1 - "consumer" : monitor the future for completion

    futureInside.onComplete {
      case Success(value) =>
        println(s" [consumer] I have been completed with $value")

      case Failure(ex) => ex.printStackTrace()
    }

    // thread 2 - "producer"

    val producerThread = new Thread(() => {
      println("[producer] Crunching numbers..")
      Thread.sleep(1000)
      // "fulfil" the promise
      promise.success(42)
      println(" [producer] I am done")
    })

    producerThread.start()
  }

  /** Exercises 1) fulfil a future IMMEDIATELY with a value 2)in sequence: make
    * sure first Future has been completed before returning the second 3)
    * first(fa, fb) => new Future with the value of the first Future to complete
    * 4) last(fa, fb) => new Future with the value of the last Future to
    * complete 5) retry an action returning a Future until a predicate holds
    * true
    */

  // 1
  def completeImmediately[A](value: A): Future[A] = Future(
    value
  ) // async completion as soon as possible
  def completeImmediately_v2[A](value: A): Future[A] =
    Future.successful(value) // synchronous  completion
  // 2
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] =
    first.flatMap(_ => second)

  // 3
  def firstFuture[A](f1: Future[A], f2: Future[A]): Future[A] = {
    val promise = Promise[A]()
    f1.onComplete(result1 => promise.tryComplete(result1))
    f2.onComplete(result2 => promise.tryComplete(result2))
    promise.future
  }

  // 4
  def last[A](f1: Future[A], f2: Future[A]): Future[A] = {
    val bothPromise = Promise[A]()
    val lastPromise = Promise[A]()

    f1.onComplete(result =>
      if (!bothPromise.tryComplete(result)) lastPromise.tryComplete(result)
    )
    f2.onComplete(result =>
      if (!bothPromise.tryComplete(result)) lastPromise.tryComplete(result)
    )
    lastPromise.future
  }

  // 5
  def retryUntil[A](
      action: () => Future[A],
      predicate: A => Boolean
  ): Future[A] = action().filter(predicate).recoverWith { case _ =>
    retryUntil(action, predicate)
  }

  def testRetries(): Unit = {
    val random = new Random()
    val action = () =>
      Future {
        Thread.sleep(100)
        val nextVal = random.nextInt(100)
        println(s"generated $nextVal")
        nextVal
      }

    val predicate = (n: Int) => n % 3 == 0
    retryUntil(action, predicate).foreach(finalResult =>
      println(s"Settled at $finalResult")
    )
  }
  def main(args: Array[String]): Unit = {
    testRetries()
    lazy val first = Future {
      Thread.sleep(1000)
      1
    }
    lazy val second = Future {
      Thread.sleep(100)
      2
    }
    // println(Await.result(last(first, second), 2.seconds))
    // demoPromise()
//    sendMessageToBestFriend_v3("id1", "hello")
//    println("purchasing...")
//    println(BankingApp.purchase("sabuj", "iPhone", "Apple", 786.5))
//    println("purchase complete")
    // println(futureInstantResult)
    Thread.sleep(3000)
    executor.shutdown()
  }

}
