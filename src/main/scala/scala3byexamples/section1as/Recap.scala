package scala3byexamples.section1as

import scala.annotation.tailrec

object Recap {

  // values, types, expressions
  val aCondition = true
  val anExpression =
    if (aCondition) 42 else 55 // expressions evaluates to a value

  // types: Int, String, Double, Boolean
  // only value of type Unit is () which is equivalent to void in other language
  val aUnit = println("I am Sabuj")

  // functions
  def aFunction(x: Int): Int = x + 1

  // recursion, stack, tail
  // @tailrec makes sure the function is tail recursive. If the function is not tail recursive the compilation will fail
  @tailrec def fact(n: Int, acc: Int): Int =
    if (n <= 0) acc
    else fact(n - 1, n * acc)

  val fact5 = fact(5, 1)

  // object -oriented programming

  class Animal
  class Bird extends Animal

  val aBird: Animal = new Bird

  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Tiger extends Animal with Carnivore {
    override infix def eat(a: Animal): Unit = println("I eat only meat")
  }

  val aTiger: Animal = new Tiger
  val bTiger: Tiger = new Tiger

  // method notation
  val cTiger = new Tiger
  cTiger.eat(aBird)
  cTiger eat aTiger // infix notation

  // anonymous classes

  val aCarnivore = new Carnivore:
    override def eat(a: Animal): Unit = println("I am an anonymous carnivore")

  aCarnivore.eat(aBird)
  aCarnivore eat aTiger

  // generics
  abstract class LinkList[A] {
    // type A is known inside the implementation
  }
//singletons and companions

  object LinkList // companion object, used for instance-independent(static) fields/methods

  // case classes

  case class Employee(name: String, empId: Int)

  // enums
  enum RGBColors {
    case RED, GREEN, BLUE
  }

  // exceptions and try/catch/finally

  def throwException(): Int = throw new RuntimeException

  val aPossibleException =
    try {
      // code that may fail
      throwException()
    } catch {
      case e: Exception => println ("I caught an exception")
    } finally {
      // closing resources
      println("Some important logs")
    }

  //functional programming

  def main(args: Array[String]): Unit = {
    println(fact5)
    bTiger.eat(bTiger)
  }

}
