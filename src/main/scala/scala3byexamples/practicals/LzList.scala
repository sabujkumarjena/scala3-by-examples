package scala3byexamples.practicals

import scala.annotation.tailrec

// Lazily evaluated, potentially INFINITE linked list
abstract class LzList[A] {
  def isEmpty: Boolean
  def head: A
  def tail: LzList[A]

  // utilities
  infix def #::(element: A): LzList[A] // prepending
  infix def ++(another: => LzList[A]): LzList[A] // TODO warning

  // classics
  def foreach(f: A => Unit): Unit = {
    @tailrec
    def foreachNTR(f: A => Unit, list: LzList[A]): Unit = {
      if (list.isEmpty) ()
      else {
        f(list.head)
        foreachNTR(f, list.tail)
      }
    }

    foreachNTR(f, this)
  }
  def map[B](f: A => B): LzList[B]
  def flatMap[B](f: A => LzList[B]): LzList[B]
  def filter(predicate: A => Boolean): LzList[A]
  def withFilter(predicate: A => Boolean) = filter(predicate)

  def take(n: Int): LzList[A] // takes first n elements
  def takeAsList(n: Int): List[A] = take(n).toList
  def toList: List[A] = {
    @tailrec
    def toListAux(remaining: LzList[A], acc: List[A]): List[A] =
      if (remaining.isEmpty)
        acc.reverse
      else
        toListAux(remaining.tail, remaining.head :: acc)

    toListAux(this, List())
  }
}

case class LzEmpty[A]() extends LzList[A] {
  def isEmpty: Boolean = true

  def head: A = throw new NoSuchElementException

  def tail: LzList[A] = throw new NoSuchElementException

  // utilities
  infix def #::(element: A): LzList[A] = new LzCons(element, this)

  infix def ++(another: => LzList[A]): LzList[A] = another

  // classics

  def map[B](f: A => B): LzList[B] = LzEmpty()

  def flatMap[B](f: A => LzList[B]): LzList[B] = LzEmpty()

  def filter(predicate: A => Boolean): LzList[A] = this

  def take(n: Int): LzList[A] = if (n == 0) then this
  else
    throw new RuntimeException(
      s" Cannot take $n elements from an empty lazy List"
    )

}

class LzCons[A](hd: => A, tl: => LzList[A]) extends LzList[A] {
  def isEmpty: Boolean = false

  // use call by need
  override lazy val head: A = hd

  override lazy val tail: LzList[A] = tl

  // utilities
  infix def #::(element: A): LzList[A] = new LzCons(element, this)

  infix def ++(another: => LzList[A]): LzList[A] =
    new LzCons(head, tail ++ another) // TODO warning

  // classics

  def map[B](f: A => B): LzList[B] = new LzCons(f(head), tail.map(f))

  def flatMap[B](f: A => LzList[B]): LzList[B] = f(head) ++ tail.flatMap(f)

  def filter(predicate: A => Boolean): LzList[A] = if (predicate(head))
    LzCons(head, tail.filter(predicate))
  else tail.filter(predicate)

  def take(n: Int): LzList[A] = if (n == 0) LzEmpty()
  else if (n == 1) new LzCons[A](head, LzEmpty())
  else
    new LzCons(head, tail.take(n - 1)) // takes first n elements

}

object LzList {
  def generate[A](start: A)(generator: A => A): LzList[A] =
    new LzCons(start, generate(generator(start))(generator))

  def from[A](list: List[A]): LzList[A] =
    if (list.isEmpty) LzEmpty() else new LzCons[A](list.head, from(list.tail))

  def apply[A](elems: A*): LzList[A] = from(elems.toList)
}
object LzListWhiteBoard {
  def main(args: Array[String]): Unit = {
    val naturals = LzList.generate(1)(n => n + 1) // INFINIE list of naturals
    println(naturals.takeAsList(20))
    // naturals.take(5000).foreach(println)
    println(naturals.map(_ * 2).takeAsList(10))
    println(naturals.flatMap(x => LzList(x, x + 1)).takeAsList(10))
    println(naturals.filter(_ < 10).takeAsList(9))
    // println(naturals.filter(_ < 10).takeAsList(10)) //crash with SO or infinite recursion

    val combinationLazy = for {
      n <- LzList(1, 2, 3)
      s <- LzList("black", "white")
    } yield s"$n-$s"
    println(combinationLazy.toList)

    // lazy list of fibonaci number
    def fib(n1: Int, n2: Int): LzList[Int] =
      new LzCons(n1, fib(n2, n1 + n2))

    println(fib(1, 2).takeAsList(10))

    /**
     lazy list of prime numbers by Eratosthenes's sieve
     [2, 3, 4, 5, 6, 7, 8 , 9, 10, 11, 12, 13,...]
     [(2), 3, 5, 7, 9, 11, 13, 15, 17,....] //remove multiple of 2
     [(2,3), 5, 7, 11, 13, 17,...] //remove muliple of 3
     */
    def sieve(list: LzList[Int]): LzList[Int] = {
      val head = list.head
      val tail = list.tail.filter(x => x % head != 0)
      new LzCons(head, sieve(tail))
    }
   println(sieve(naturals.tail).takeAsList(10000))
  }
}
