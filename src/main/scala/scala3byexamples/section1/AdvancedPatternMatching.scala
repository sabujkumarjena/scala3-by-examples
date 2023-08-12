package scala3byexamples.section1

object AdvancedPatternMatching {
  /*
  - constants
  - objects
  - wildcards
  - variables
  - infix patterns
  - lists
  - case classes
   */

  class Employee(val name: String, val age: Int)
  object Employee {
    def unapply(
        employee: Employee
    ): Option[
      (String, Int)
    ] = // employee match {case Employee(string, int) => ..}
      if (employee.age < 18) None
      else Some((employee.name, employee.age))

    def unapply(
        age: Int
    ): Option[String] = // int match { case Person(string) => ...}
      if (age < 18) Some("Minor") else Some("legally allowed to drive")

  }

  val sabuj = new Employee("Sabuj Kumar Jena", 41)

  val sabujPM = sabuj match { // Employee.unapply(sabuj) => Option((n,a))
    case Employee(n, a) => s" Name is $n with employee age $a"
  }

  val sabujDLStatus = sabuj.age match {
    case Employee(status) => s"Sabuj's DL status is $status"
  }

  // booleans patterns

  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val n: Int = 45

  val mathProperty = n match {
    case even()        => " an even number"
    case singleDigit() => " a single digit number"
    case _             => " no special property"
  }

  // infix patterns
  infix case class Or[A, B](a: A, b: B)
  val anEither: Int Or String = Or(5, "five")

  val infixPM = anEither match {
    case n Or s => s"$n is $s"
  }

  val aList = List(1, 2, 3)
  val listPM = aList match {
    case 1 :: rest => " alist strating with 1"
    case _         => " some non-interesting list"
  }

  // decomposing sequences

  val vararg = aList match {
    case List(1, _*) => "list starting with 1"
    case _           => " Some other list"
  }

  abstract class LinkList[A] {
    def head: A
    def tail: LinkList[A]

  }

  case class Empty[A]() extends LinkList[A] {
    def head: A = throw new NoSuchElementException
    def tail: LinkList[A] = throw new NoSuchElementException
  }
  case class Cons[A](val head: A, tail: LinkList[A]) extends LinkList[A]

  object LinkList {
    def unapplySeq[A](list: LinkList[A]): Option[Seq[A]] =
      if (list == Empty()) Some(Seq.empty)
      else unapplySeq(list.tail).map(restofSeq => list.head +: restofSeq)
  }

  val myList: LinkList[Int] = Cons(1, Cons(2, Cons(3, Empty())))

  val varargCustom = myList match {
    case LinkList(1, _*) => "list strating with 1"
    case _               => "some other list"
  }

  // custom return type for unapply
  abstract class Box[T] {
    def isEmpty: Boolean
    def get: T
  }

  object EmployeeBox {
    def unapply(emp: Employee): Box[String] = new Box[String] { // uanapply method return type  has to implement isEmpty and get
      override def isEmpty = false
      override def get = emp.name
    }
  }

  val wieredEmployeePM = sabuj match {
    case EmployeeBox(name) => s"Employee name is $name"
  }
  def main(args: Array[String]): Unit = {
    println(sabujPM)
    println(mathProperty)
    println(infixPM)
    println(varargCustom)
    println(wieredEmployeePM)
  }

}
