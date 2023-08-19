package scala3byexamples.section5

import reflect.Selectable.reflectiveSelectable

object StructuralTypes {

  type SoundMaker = { // structural type
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("barking")
  }

  class Car {
    def makeSound(): Unit = println("vroom")
  }

  val dog: SoundMaker = new Dog // ok
  val car: SoundMaker = new Car
  // compile-time duck typing

  // type refinement
  abstract class Animal {
    def eat(): String
  }

  type WalkingAnimal = Animal { // refined type
    def walk(): String
  }

  // why structural type : creating type-safe APIs for existing types following the same structure, but no connection to each other
  type JavaCloseable = java.io.Closeable
  class CustomCloseable {
    def close(): Unit = println("I am closing")
    def closeSilently(): Unit = println("not making a sound while closing")
  }

  // def closeResource(closeable:  JavaCloseable | CustomCloseable): Unit = closeable.close() // not ok

//solution: structural type
  type UnifiedCloseable = {
    def close(): Unit
  }

  def closeResource(closeable: UnifiedCloseable): Unit = closeable.close()

  val jCloseable = new JavaCloseable {
    override def close(): Unit = println(" closing java resources")
  }

  val cCloseable = new CustomCloseable

  def closeResource_v2(closeable: { def close(): Unit }) = closeable.close()
  def main(args: Array[String]): Unit = {
    dog.makeSound() // through reflection (slow)
    car.makeSound()
    closeResource(jCloseable)
    closeResource(cCloseable)
    closeResource_v2(jCloseable)
    closeResource_v2(cCloseable)
  }

}
