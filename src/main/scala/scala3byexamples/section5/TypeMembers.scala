package scala3byexamples.section5

object TypeMembers {

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
// val, var, def, class, trait, object
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal // abstarct type member with a type bound
    type SuperBoundedAnimal >: Dog <: Animal
    type AnimalAlias = Cat // type alias
    type NestedOption =
      List[Option[Option[Int]]] // often used to alias complex/nested types
  }

  // using type member
  val ac = new AnimalCollection
  // val anAnimal: ac.AnimalType = ???

  // val cat: ac.BoundedAnimal = new Cat //BoundedAnimal might be Dog
  val aDog: ac.SuperBoundedAnimal = new Dog // ok. Dog <: SuperBoundedAnimal
  val aCat: ac.AnimalAlias = new Cat // ok, Cat == AnimalAlias

  // type members are useful to establish relationships between types
  // alternative to generics
  abstract class LList[T] {
    def add(element: T): LList[T]
  }

  abstract class MyList {
    type T
    def add(element: T): MyList
  }

  // .type
  type CatType = aCat.type
  val newCat: CatType = aCat

  def main(args: Array[String]): Unit = {}

}
