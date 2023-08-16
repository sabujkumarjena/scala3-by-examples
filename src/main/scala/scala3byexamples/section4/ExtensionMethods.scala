package scala3byexamples.section4

import scala.annotation.tailrec

object ExtensionMethods {
  case class Employee(name: String) {
    def greet: String = s"Hi, my name is $name , nice to meet you"
  }

  extension (string: String)
    def greetAsEmployee: String = Employee(string).greet

  val sabujGreeting = "sabuj".greetAsEmployee

  // generic extension method

  extension [A](list: List[A]) def ends: (A, A) = (list.head, list.last)

  val myListStartNEnd = List(1, 2, 3, 4).ends

  // reason: make APis very expressive
  // reason 2: enhance CERTAIN types with new capabilities
  //  => super powerful code
  trait Combinator[A] {
    def combine(x: A, y: A): A
  }

  extension [A](list: List[A])
    def combineAll(using combinator: Combinator[A]) =
      list.reduce(combinator.combine)

  given intCombinator: Combinator[Int] with {
    override def combine(x: Int, y: Int): Int = x + y
  }

  val listCombineAll = List(1, 2, 3, 4).combineAll

  // val stringCombineAll = List("I", "love", "Scala").combineAll // does not compile - no given  Combinator[String in scope

  // grouping extensions
  object GroupedExtensions {
    extension [A](list: List[A]) {
      def ends: (A, A) = (list.head, list.last)
      def combineAll(using combinator: Combinator[A]) =
        list.reduce(combinator.combine)
    }
  }

  // call extension methods directly
  val myListStartNEnd_v2 = ends(List(1, 2, 3, 4)) // same as List(1,2,3,4).ends

  /** Exercises
    *   1. Add an isPrime method to the Int type 2. Add extensions to Tree:
    *      - map(f: A => B): Tree[B] -forall(predicate: A => Boolean): Boolean
    *        -sum => sum of all elements in Tree
    */

  // 1
  extension (n: Int)
    def isPrime: Boolean = {
      @tailrec
      def isPrimeTR(potentialDiviser: Int): Boolean =
        if (potentialDiviser > n / 2) true
        else if (n % potentialDiviser == 0) false
        else isPrimeTR(potentialDiviser + 1)
      assert(n >= 0)
      if (n == 0 || n == 1) false
      else isPrimeTR(2)
    }

  // "library code" = cannot change
  sealed abstract class Tree[A]
  case class Leaf[A](value: A) extends Tree[A]
  case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
//library code end
  extension [A](tree: Tree[A]) {
    def map[B](f: A => B): Tree[B] = tree match {
      case Leaf(a)      => Leaf(f(a))
      case Branch(l, r) => Branch(l.map(f), r.map(f))
    }

    def forAll(predicate: A => Boolean): Boolean = tree match {
      case Leaf(a)      => predicate(a)
      case Branch(l, r) => l.forAll(predicate) && r.forAll(predicate)
    }
    def sum(using Combinator[A]): A = tree match {
      case Leaf(a)      => a
      case Branch(l, r) => summon[Combinator[A]].combine(l.sum, r.sum)
    }
  }

//  extension (tree: Tree[Int]) {
//    def sum: Int = tree match {
//      case Leaf(value)  => value
//      case Branch(l, r) => l.sum + r.sum
//    }
//  }
  def main(args: Array[String]): Unit = {
    val aTree: Tree[Int] = Branch(Branch(Leaf(1), Leaf(3)), Leaf(2))
    println(aTree.sum)
    println(aTree.forAll(_ > 0))
    println(aTree.map(_ + 1))
    println(listCombineAll)
    println(myListStartNEnd)
    println(myListStartNEnd_v2)
    println(sabujGreeting)
    println(21.isPrime)
    println(isPrime(23))
  }

}
