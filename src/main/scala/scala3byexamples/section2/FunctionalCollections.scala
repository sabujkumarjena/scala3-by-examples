package scala3byexamples.section2

//sets are functions A => Boolean
val aSet: Set[String] = Set("I", "love", "Scala")
val setContainsScala = aSet("Scala") //true

//Seq extends PartialFunction[Int, A]
val aSeq: Seq[Int] = Seq(1, 2, 3, 4)
val anElem = aSeq(2) //3
//val aNonExistingElem = aSeq(20) //throws an OOBException

// Map[K,V] extends PartialFunction[K,V]
val aPhonebook: Map[String, Int] = Map(
  "Alice" -> 1234,
  "Bob" -> 789
)

val aclicePhone = aPhonebook("Alice")
//val dansPhone = aPhonebook("Dan")  //throws a NoSuchElementException
object FunctionalCollections {
  def main(args: Array[String]): Unit = {}

}
