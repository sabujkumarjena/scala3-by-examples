package scala3byexamples.section5

object VariancePositions {
  class Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // 1 - type bounds
  class Cage[A <: Animal]

  // val aCage = new Cage[String] // not ok, String is not sub type of Animal
  val aRealCage = new Cage[Dog] // ok Dog <: Animal

  class WieredContainer[A >: Animal] // A must be a supertype of Animal

  // 2 - variance positions

  // class Vet[-T](val favouriteAnimal : T) //type of val fields are in COVARIANT position

  /*
  var myCat = new Cat
  val theVet: Vet[Animal] = new Vet[Animal](myCat)
  val aDogVet: Vet[Dog] = theVet //possible, theVet is Vet[Animal]
  val aDog:Dog = aDogVet.favouriteAnimal //must be a Dog - type conflict !
   */

  // types of var fields are in COVARIANT position
  // (same reason)

  // types of var fields are in CONTRAVARIANT position
  // class MutableOption[+T](var contents: T)

  /*
   val maybeAnimal: MutableOption[Animal] = new MutableOption[Dog](new Dog)
  maybeAnimal.contens =  new Cat //  type conflict
   */

  // types of method arguments are in CONTRAVARIANT position
//  class MyList[+T] {
//    def add(element: T): MyList[T]= ???
//  }

  /*
   val animals: MyList[Animal] = new MyList[Cat]
   biggerListOfAnimals = animals.add(new Dog)  //type conflict!
   */

  class Vet[-T] {
    def heal(animal: T): Boolean = true
  }

//method return types

//abstract class Vet2[-T] {
//  def rescueAnimal(): T
//}

  /*
 val vet : Vet2[Animal] = new Vet2[Animal] {
  override def rescueAnimal() : Animal = new Cat
}

val lancyVet: Vet2[Dog] = vet //Vet2[Animal]
val rescueDog: Dog = lancyVet.rescueAnimal() // must return a Dog, returns a Cat -type conflict
   */

  /** 3 - solving variance positions problems
    */

  abstract class LList[+A] {
    def head: A
    def tail: LList[A]
    def add[U >: A](element: U): LList[U] // widen the type
  }

  // val animals: List[Cat] = list of cats
  // val newAnimals : List[Animal] = animals.ad(new Dog)

  class Vehicle
  class Car extends Vehicle
  class SuperCar extends Car

  class Garage[-A <: Vehicle] {
    def repair[B <: A](vehicle: B): B = vehicle // narrowing the type
  }

  val myGarage : Garage[Car] = new Garage[Vehicle]
  val myBMW = new Car
  val myRepairedCar = myGarage.repair(myBMW) //works, returns a car

  def main(args: Array[String]): Unit = {}
}
