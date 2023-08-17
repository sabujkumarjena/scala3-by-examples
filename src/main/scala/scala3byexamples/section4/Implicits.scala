package scala3byexamples.section4

object Implicits {

  // given/using clauses
  // the ability to pass arguments automatically (implicitly) by the compiler

  // implicit arg -> using arg
  // implicit val -> given
  trait Semigroup[A] {
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(implicit semigroup: Semigroup[A]): A =
    list.reduce(semigroup.combine)

  implicit val intSemigroup: Semigroup[Int] = new Semigroup[Int]:
    override def combine(x: Int, y: Int): Int = x + y

  val sumOf10 = combineAll((1 to 10).toList)
  // extension methods
//  extension  (number: Int)
//    def isEven = number % 2 == 0
  // extension methods = implicit class

  implicit class MyInteger(num: Int) {
    // extension methods here
    def isEven = num % 2 == 0
  }
  val is25Even = 25.isEven // new MyInt(25).isEven

  // implicit conversion - SUPER DANGEROUS, difficult to debug
  case class Employee(name: String) {
    def greet(): String = s"Hi, I'm $name"
  }

  implicit def string2Person(x: String): Employee = Employee(
    x
  ) // implicit conversion
  val bob = Employee("Bob")
  val bobSaysHi = bob.greet()
  val bobSaysHi_v2 =
    "bob".greet() // string2Person("bob").greet()

  // the goal of "implicit def" was too synthesize NEW implicit values

  implicit def semigroupOfOption[A](implicit
      semigroup: Semigroup[A]
  ): Semigroup[Option[A]] =
    new Semigroup[Option[A]] {
      override def combine(x: Option[A], y: Option[A]): Option[A] = for {
        valueX <- x
        valueY <- y
      } yield semigroup.combine(valueX, valueY)
    }

//  given semigroupOfOption[A](using
//      semigroup: Semigroup[A]
//  ): Semigroup[Option[A]] with
//    override def combine(x: Option[A], y: Option[A]): Option[A] = for {
//      valueX <- x
//      valueY <- y
//    } yield semigroup.combine(valueX, valueY)

// AMBIGUOUS  between implicit conversion and implicit defination (to synthesize new implicit values)
  /*
  Why implicits will be phased out:
    - the implicit keyword has many different meanings
    - conversions are easy to abuse
    - implicits are very hard to track down while debugging (givens also not trivial, but they are explicitly imported
   */

  /// organising implicits == organising contextul abstractions
  def main(args: Array[String]): Unit = {
    println(sumOf10)
    println(is25Even)
    println(bobSaysHi)
    println(bobSaysHi_v2)
  }

}
