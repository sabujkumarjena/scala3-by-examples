package scala3byexamples.section4

object OrganizingCAs {

  val aList = List(2, 3, 1, 4)
  val anOrderedList = aList.sorted

  // compiler fetches givens/EMs
  // 1- local scope
  given reverseOrdering: Ordering[Int] with
    override def compare(x: Int, y: Int): Int = y - x
  // 2 - imported scope
  case class Employee(name: String, age: Int)

  val employees =
    List(Employee("Sabuj", 43), Employee("Bob", 49), Employee("Martin", 25))

  object EmployeGivens {
    given employeeAgeOrdering: Ordering[Employee] with
      override def compare(x: Employee, y: Employee): Int = y.age - x.age

    extension (e: Employee) def greet: String = s"Hello, I am ${e.name}"
  }

  // a - import explicitly
  // import EmployeGivens.employeeAgeOrdering
  // b- import a given for a particular type
//  import EmployeGivens. {given Ordering[Employee]}
  // c- import all givens

  // warning: import EmployeGivens.*  does NOT also import given instances!
  // import  EmployeGivens.given
  // import  EmployeGivens.*

  // 3- companions of all types involved in method signature
  /*
  -Ordering
  -List
  -Employee
   */
  // def sorted[B >: A](using ord: Ordering[B]): :List[B]
  object Employee {
    given byNameOrdering: Ordering[Employee] with
      override def compare(x: Employee, y: Employee): Int =
        x.name.compareTo(y.name)

    extension (e: Employee)
      def greet: String = s"Hello, I am ${e.name} from Companion"
  }
  val sortedEmployees = employees.sorted
  /*
  Good practice tips:
  1) When you have a "default" given (only ONE that makes sense) add it in the companion object of the type
  2) When you have MANY possible givens, but ONE that is dominant (used most), add that in the companion object and the rest in other object
  3) When you have MANY possible givens and NO ONE is dominant, add them in separate objects and import them explicitly.
   */

  // Same principles apply to extension methods as well.

  /** Exercises. Create given instances for Orrdering[Purchase]
    *   - ordering by total price, descending = 50 % of code base
    *   - ordering by unit count, descending = 25% of code base
    *   - ordering by unit price, ascending = 25% of code base
    */

  case class Purchase(nUnits: Int, unitPrice: Double)
  object Purchase {
    given purchaseOrdering: Ordering[Purchase] with
      override def compare(x: Purchase, y: Purchase): Int =
        (y.nUnits * y.unitPrice - x.nUnits * x.unitPrice).asInstanceOf[Int]
  }

  object UnitCoutOrdering {
    given unitCountOrdering: Ordering[Purchase] with
      override def compare(x: Purchase, y: Purchase): Int = y.nUnits - x.nUnits
  }
  object UnitPriceOrdering {
    given Ordering[Purchase] =
      Ordering.fromLessThan((x, y) => x.unitPrice < y.unitPrice)
  }

  val purchases: List[Purchase] =
    List(Purchase(3, 12.4), Purchase(5, 1), Purchase(2, 50.5))

  // import UnitCoutOrdering.unitCountOrdering
  import UnitPriceOrdering.given
  val sortedPurchases = purchases.sorted
  def main(args: Array[String]): Unit = {
    println(sortedPurchases)
    println(anOrderedList)
    println(sortedEmployees)
    // import EmployeGivens.* //includes extension methods
    println(Employee("sabuj", 43).greet)
  }

}
