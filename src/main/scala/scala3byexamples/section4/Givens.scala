package scala3byexamples.section4

/* Uses for givens:
- changing program behavior depending on context, i.e. contextual abstractions
- type classes
- automatic dependency injection
- type-level programming
 */

object Givens {

  // custom sorting
  case class Employee(name: String, age: Int)

  val employees =
    List(Employee("Sabuj", 43), Employee("Bob", 49), Employee("Martin", 25))
  given employeeOrdering: Ordering[Employee] = new Ordering[Employee]:
    override def compare(x: Employee, y: Employee): Int =
      x.name.compareTo(y.name)

  val sortedEmployees =
    employees.sorted // (employeeOrdering) <-- automatically passed by the compiler

  object EmployeeAltSyntax {
    given employeeOrdering: Ordering[Employee] with {
      override def compare(x: Employee, y: Employee): Int =
        x.name.compareTo(y.name)
    }
  }

  // using clause
  trait Combinator[A] {
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(using combinator: Combinator[A]): A =
    list.reduce(combinator.combine)
  /*
  combineAll(list(1,2,3,4))
  combineAll(employees)
   */
  given intCombinator: Combinator[Int] with {
    override def combine(x: Int, y: Int): Int = x + y
  }

  val firstSum = combineAll(List(1, 2, 3, 4))

  // val combineAllEmployees = combineAll(employees) // doesn't compile - no Combinator[Employee] in scope

  // context bound
  def combineInGroupOf3[A](
      list: List[A]
  )(using Combinator[A]): List[A] =
    list
      .grouped(3)
      .map(group =>
        combineAll(group) /* given Combinator[A] passed by the compiler*/
      )
      .toList

  // context bound
  def combineInGroupOf3_v2[A: Combinator](
      list: List[A]
  ): List[A] = // A :Combiner => there is a given Combinator[A] in scope
    list
      .grouped(3)
      .map(group =>
        combineAll(group) /* given Combinator[A] passed by the compiler*/
      )
      .toList

  val gruopedSum = combineInGroupOf3_v2(List(1, 2, 3, 4, 5))

  // synthesize new given instances based on existing ones
//  given listOrdering(using intOrdering: Ordering[Int]): Ordering[List[Int]]
//  with {
//    override def compare(x: List[Int], y: List[Int]): Int = x.sum - y.sum
//  }

  // ... with generics
  given listOrderingBasedOnCombinator[A](using
      ordering: Ordering[A]
  )(using Combinator[A]): Ordering[List[A]] with {
    override def compare(x: List[A], y: List[A]) =
      ordering.compare(combineAll(x), combineAll(y))
  }

  val listOfLists = List(List(1, 2), List(1, 1), List(3, 4, 5))

  val nestedListsOrdered = listOfLists.sorted

  // pass a regular value instead of a given

  val myCombinator = new Combinator[Int] {
    override def combine(x: Int, y: Int) = x * y
  }
  val listProduct = combineAll(List(2, 3, 4, 5))(using myCombinator)

  /** Exercises: 1 - create a given for ordering Option[A] if you can order A 2
    * \- create a smmoning method that fetches the given value of your
    * particular
    */
//1-
  given optionOrdering[A](using Ordering[A]): Ordering[Option[A]] with {
    override def compare(x: Option[A], y: Option[A]): Int = (x, y) match {
      case (None, None)       => 0
      case (None, Some(_))    => -1
      case (Some(_), None)    => 1
      case (Some(a), Some(b)) => summonCustom[Ordering[A]].compare(a, b) //use sommon from library
    }
  }

  // 2 -
  def summonCustom[A](using a: A): A = a

  def main(args: Array[String]): Unit = {

    println(List(Option(3), Option.empty, Option(2), Option(1)).sorted)
    println(listProduct)
    println(nestedListsOrdered)
    println(gruopedSum)
    println(firstSum)
    println(sortedEmployees)
  }

}
