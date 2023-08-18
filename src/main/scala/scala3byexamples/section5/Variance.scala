package scala3byexamples.section5

object Variance {

  class Animal
  class Dog(name: String) extends Animal

  // Variance question for List: id Dog extends Animal, then should a List[Dog] "extend" List[Animal]
  // for List, YES - List is COVARIANT

  val lancy = new Dog("Lancy")
  val lucky = new Dog("Lucky")
  val snowy = new Dog("Snowy")

  val anAnimal: Animal = lancy // ok , Dog <: Animal
  val myDogs: List[Animal] = List(
    lancy,
    lucky,
    snowy
  ) // ok - List is Covariant: a list of dogs is a list of animals

  // define covariant type
  class MyList[+A] // MyList is COVARIANT in A
  val aListOfAnimals: MyList[Animal] = new MyList[Dog]

  // if NO, then the type is INVARIANT

  trait Semigroup[A] { // no marker = INVARIANT
    def combine(x: A, y: A): A
  }

  // java generics are INVARIANT as java doesn't have variance concept
  // val aJavaList : java.util.ArrayList[Animal] = new java.util.ArrayList[Dog] //type mismatch: java generics are all INVARIANT

  // CONTRAVARIANCE
//id Dog <: Animal, Vet[Animal] <:Vet[Dog] //vet of animal is also vet of dog
  trait Vet[-A] { // contravariant in A
    def heal(animal: A): Boolean
  }

  // if the vet can treat any animal, she/he can treat my dog too
  val myVet: Vet[Dog] = new Vet[Animal] {
    override def heal(animal: Animal): Boolean = {
      println("Hey there, you are all good..")
      true
    }
  }

  val healLancy = myVet.heal(lancy)

  /*
  Rule of thumb:
  - if your type PRODUCES or RETRIEVES a value (e.g. a list), then it should be COVARIANT
  - if your type ACTS ON or CONSUMES a value (e.g. a vet), then it should be CONTRAVARIANT
  - otherwise, INVARIANT
   */

  /** Exercises
    */
//1 - which types should be invariant, covariant, contravariant

  class RandomGenerator[+A] // produces values: Covariant
  class MyOption[+A] // similar to Option[A]

  class JSONSerializer[-A] // consumes values and turn them strings

  trait MyFunction[-A, +B] // similar to Function1[A, B]

//2 - add variance modifiers to this "library"

  abstract class LList[+A] {
    def head: A
    def tail: LList[A]
  }

  case object EmptyList extends LList[Nothing] {
    override def head = throw new NoSuchElementException

    override def tail = throw new NoSuchElementException
  }

  case class Cons[+A](override val head: A, override val tail: LList[A])
      extends LList[A]

  val aList: LList[Int] = EmptyList //fine
  val anotherList: LList[String] = EmptyList //also fine
  // Nothing <: A, then LList[Nothing] <: LList[A]
  def main(args: Array[String]): Unit = {}

}
