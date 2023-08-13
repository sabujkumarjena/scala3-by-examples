package scala3byexamples.practicals

import scala.annotation.tailrec

abstract class FSet[A] extends (A => Boolean) {
  // main api

  // def isEmpty(): Boolean
  def contains(a: A): Boolean
  def apply(a: A): Boolean = contains(a)

  infix def +(a: A): FSet[A]
  infix def ++(anotherSet: FSet[A]): FSet[A]

  // classics
  def map[B](f: A => B): FSet[B]
  def flatMap[B](f: A => FSet[B]): FSet[B]
  def filter(predicate: A => Boolean): FSet[A]
  def foreach(f: A => Unit): Unit

  infix def -(a: A): FSet[A]
  infix def --(anotherSet: FSet[A]): FSet[A]
  infix def &(anotherSet: FSet[A]): FSet[A]

  def unary_! : FSet[A] = new PBSet(x => !contains(x))

}
//property based set
class PBSet[A](property: A => Boolean) extends FSet[A] {
// def isEmpty(): Boolean

  def contains(a: A): Boolean = property(a)

  infix def +(a: A): FSet[A] = new PBSet(x => property(x) || x == a)

  infix def ++(anotherSet: FSet[A]): FSet[A] = new PBSet(x =>
    property(x) || anotherSet(x)
  )

  // classics
  def map[B](f: A => B): FSet[B] = politelyFail()

  def flatMap[B](f: A => FSet[B]): FSet[B] = politelyFail()

  def filter(predicate: A => Boolean): FSet[A] = new PBSet(x =>
    property(x) && predicate(x)
  )

  def foreach(f: A => Unit): Unit = politelyFail()

  infix def -(a: A): FSet[A] = filter(x => x != a)

  infix def --(anotherSet: FSet[A]): FSet[A] = filter(!anotherSet)

  infix def &(anotherSet: FSet[A]): FSet[A] = filter(anotherSet)

  // def unary_! : FSet[A] =  new PBSet(x => !contains(x)) //new PBSet(x => !property(x))

  private def politelyFail() = throw new RuntimeException(
    "I don't know if this set is iterable..."
  )
}
//case class AllInclusiveSet[A]() extends PBSet[A](_ => true)
case class Empty[A]() extends FSet[A] { // PBSet(x => false) //implement Empty() in terms of PBSet
  // override def isEmpty(): Boolean = true
  override def contains(a: A): Boolean = false

  override infix def +(a: A): FSet[A] = Cons(a, this)

  override infix def ++(anotherSet: FSet[A]): FSet[A] = anotherSet

  override def map[B](f: A => B): FSet[B] = Empty()

  override def flatMap[B](f: A => FSet[B]): FSet[B] = Empty()

  override def filter(predicate: A => Boolean): FSet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  override def toString(): String = "Set(" + ")"

  infix def -(a: A): FSet[A] = this

  infix def --(anotherSet: FSet[A]): FSet[A] = this

  infix def &(anotherSet: FSet[A]): FSet[A] = this

  // def unary_! : FSet[A] = AllInclusiveSet()

}

case class Cons[A](head: A, tail: FSet[A]) extends FSet[A] {
  // override def isEmpty(): Boolean = false
  override def contains(a: A): Boolean = a == head || tail.contains(a)

  override infix def +(a: A): FSet[A] = if (contains(a)) this else Cons(a, this)

  override infix def ++(anotherSet: FSet[A]): FSet[A] = anotherSet match {
    case Empty() => this
    case Cons(head2, tail2) =>
      if (head == head2) Cons(head, tail ++ tail2)
      else Cons(head, tail ++ anotherSet)
  }

  override def map[B](f: A => B): FSet[B] = Cons(f(head), tail.map(f))

  override def flatMap[B](f: A => FSet[B]): FSet[B] = f(head) ++ tail.flatMap(f)

  override def filter(predicate: A => Boolean): FSet[A] = if (predicate(head))
    Cons(head, tail.filter(predicate))
  else tail.filter(predicate)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  infix def -(a: A): FSet[A] =
    if (head == a) tail else Cons(head, tail - a) // tail - a + head

  infix def --(anotherSet: FSet[A]): FSet[A] = filter(!anotherSet(_))
//    anotherSet match {
//    case Empty()            => this
//    case Cons(head2, tail2) => this - head2 -- tail2
//  }

  infix def &(anotherSet: FSet[A]): FSet[A] = filter(anotherSet)
  // anotherSet -- ((this ++ anotherSet)  -- this)

  // def unary_! : FSet[A] = AllInclusiveSet() -- this
  override def toString(): String = {
    def show[A](set: FSet[A]): String = set match {
      case Empty() => ""
      case Cons(h, t) =>
        if (t == Empty()) h.toString else h.toString + ", " + show(t)
    }
    "Set(" + show(this) + ")"
  }
}

object FSet {
  def apply[A](values: A*): FSet[A] = {
    @tailrec
    def buildSet(valueSeq: Seq[A], acc: FSet[A]): FSet[A] =
      if (valueSeq.isEmpty) acc
      else buildSet(valueSeq.tail, acc + valueSeq.head)

    buildSet(values, Empty())
  }

}
val aSet: FSet[Int] = Cons(1, Cons(2, Empty()))

val setA: FSet[Int] = FSet(1, 2, 3, 4, 5)
val setB: FSet[Int] = FSet(3, 4, 5, 6, 7)
object FunctionalSetWhiteBoard {

  // val aSet =
  def main(args: Array[String]): Unit = {
    println(((aSet + 5) ++ (Empty() + 6)).toString())
    println(Empty())
    println(setB -- setA)

    val naturals = new PBSet[Int](x => true && x >= 0)
    println(naturals.contains(-1))
    println(naturals.contains(0))
  }

}
