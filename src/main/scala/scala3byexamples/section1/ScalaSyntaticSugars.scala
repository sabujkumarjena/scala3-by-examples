package scala3byexamples.section1

import scala.util.Try

object ScalaSyntaticSugars {

  // a- methods with one argument

  def singleArgMethod(n: Int): Int = n + 1

  val aMethodCall = singleArgMethod({
    // long code
    42
  })

  val aMethodCall_v2 = singleArgMethod {
    // long code
    42
  }
//example: Try, Future

  val aTryInstance = Try {
    throw new RuntimeException
  }

  // with hofs
  val squaredList = List(1, 2, 3, 4).map { x =>
    // code block
    x * x
  }

  // b - single abstract method pattern

  trait Action {
    def act(x: Int): Int
  }

  val anAction = new Action:
    // can aslo have other implemented fields/methods here
    override def act(x: Int): Int = x * x

  val anotherAction: Action =
    (x: Int) => x + 1 // new Action { def act(x: Int) =  x + 1 }

  // anotherAction() //error
  val res = anotherAction.act(2)

  // example: Runnable
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("from another thread")
  })

  val aBetterThread = new Thread(() => println("from another thread"))

  // c - method ending in : are right asociative

  val aList = 0 :: List(1, 2, 3)
  val sameList = List(1, 2, 3).::(0)

  val anotherList = 0 :: 1 :: 2 :: List(3, 4)
  val sameAnotherList = List(3, 4).::(2).::(1).::(0)

  class MyStream[T] {
    infix def -->:(value: T): MyStream[T] = this // implementation not important
  }

  val myStream = 1 -->: 2 -->: 2 -->: 3 -->: new MyStream[Int]

  // d - multi word identifier

  class Talker(name: String) {
    infix def `and then said`(gossip: String) = println(s"$name siad $gossip")
  }

  val sabuj = new Talker("Sabuj Kumar Jena")
  val sabujStatement = sabuj `and then said` "I love Scala 3"

  // example: HTTP libraries
  object `Contenet-Type` {
    val `application/json` = "application/JSON"
  }

  // e - infix types
  import scala.annotation.targetName
  @targetName("Arrpw") // for more readable bytecode + Java interop
  infix class -->[A, B]
  val compositeType: -->[Int, String] = new -->[Int, String]
  val compositeType_v2: Int --> String = new -->[Int, String] // infix notation

  // f - update()
  val anArray = Array(20, 30, 40)
  anArray.update(1, 3)
  anArray(0) = 200

  // g - mutable fields
  class Mutable {
    private var internalMember: Int = 0
    def member = internalMember // "getter" in java
    def member_=(value: Int): Unit =
      internalMember = value // "setter" in java
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // aMutableContainer.member_=(42)

  // h - variable arguments (varargs)
  def methodWithVarargs(args: Int*) = {
    // returns the number of arguments supplied
    args.length
  }

  val callWithZeroArgs = methodWithVarargs()
  val callWithOneArg = methodWithVarargs(23)
  val callWithTwoArgs = methodWithVarargs(34, 52)

  val aCollection = List(1, 2, 3, 4)

  val callWithDynamicArgs = methodWithVarargs(aCollection*)
  def main(args: Array[String]): Unit = {
    println(anArray.toList)
    println(callWithZeroArgs)
  }

}
