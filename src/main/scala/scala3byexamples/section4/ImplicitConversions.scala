package scala3byexamples.section4

//special import
import scala.language.implicitConversions
object ImplicitConversions {
  case class Employee(name: String) {
    def greet(): String = s"Hi, I'm $name"
  }

  val bob = Employee("Bob®")
  val bobSaysHi = bob.greet()

  // special conversion instance
  given string2Employee: Conversion[String, Employee] with
    override def apply(x: String): Employee = Employee(x)

  val bobSaysHi_v2 =
    "bob".greet() // Employee("bob").greet(), automatically by the compiler

  def processEmployee(emp: Employee): String =
    if (emp.name.startsWith("S")) "OK" else "NOT OK"

  val isSabujOk = processEmployee("Sabuj")

  /*
   - auto-box-type
  - use multiple types for the same code interchangeably
   */
  def main(args: Array[String]): Unit = {
    println(bobSaysHi)
    println(bobSaysHi_v2)
    println("sabuj".greet())
    println(isSabujOk)
  }

}
