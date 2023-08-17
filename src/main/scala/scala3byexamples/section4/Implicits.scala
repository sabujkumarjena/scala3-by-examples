package scala3byexamples.section4

object Implicits {

  // given/using clauses
  // the ability to pass arguments automatically (implicitly) by the compiler
  trait Semigroup[A] {
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(using semigroup: Semigroup[A]): A =
    list.reduce(semigroup.combine)

  given intSemigroup: Semigroup[Int] with
    override def combine(x: Int, y: Int): Int = x + y

  val sumOf10 = combineAll((1 to 10).toList)
  // extension methods
  // implicit conversion
  def main(args: Array[String]): Unit = {
    println(sumOf10)
  }

}
