package scala3byexamples.section3

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Random, Success, Try}

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

  def main(args: Array[String]): Unit = {

    sendMessageToBestFriend_v3("id1", "hello")
    // println(futureInstantResult)
    Thread.sleep(3000)
    executor.shutdown()
  }

}
