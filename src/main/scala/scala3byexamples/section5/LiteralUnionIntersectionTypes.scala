package scala3byexamples.section5

object LiteralUnionIntersectionTypes {

  // 1- literal types
  val aNumber = 3
  val three: 3 = 3

  def passNumber(n: Int) = println(n)

  def passStrict(n: 3) = println(n)
  passStrict(3) // ok
  passStrict(three) // ok
  // passStrict(5) // not ok , Int <: 3

  passNumber(42)
  passNumber(three) // ok , 3 <: Int

  // literal types are available for double, boolean, string
  val pi: 3.14 = 3.14
  val tructh: true = true
  val favLang: "Scala" = "Scala"

  // literal types can be used as type arguments (just like any other types)
  def doSomething(meaning: Option[42]) = meaning.foreach(println)

  // 2 - union type
  val boolOrInt: Boolean | Int = true
  val boolOrInt_v2: Boolean | Int = 45

  def aMethod(arg: String | Int) = arg match {
    case _: String => " a string"
    case _: Int    => "a number"
  }
// type inference chooses a lower common ancestor(LCA) of the two types instead String | Int

  val stringOrInt = if (43 > 0) "a string" else 45
  val stringOrInt_v2: String | Int = if (43 > 0) "a string" else 45

  // union types + null
  type Maybe[T] = T | Null
  def handleMaybe(someValue: Maybe[String]): Int =
    if (someValue != null) someValue.length // flow typing
    else 0

  type ErrorOr[T] = T | "error"
//  def handleError(arg: ErrorOr[Int]): Unit =
//    if (arg != "error") println(arg + 1) //flow typing doen't work here
//    else println("Error!")

//3- intersection types
  class Animal
  trait Carnivore
  class Crocodile extends Animal with Carnivore
  val carnivoreAnimal: Animal & Carnivore = new Crocodile

  trait Gadget {
    def use(): Unit
  }
  trait Camera extends Gadget {
    def takePicture() = println("smile!")
    override def use() = println("snap")
  }

  trait Phone extends Gadget {
    def makePhoneCall() = println("calling...")
    override def use() = println("ring")

  }

  def useSmartDevice(sp: Camera & Phone): Unit = {
    sp.takePicture()
    sp.makePhoneCall()
    sp.use() // which use() is being called? can't tell
  }

  class SmartPhone extends Camera with Phone // diamond problem
  class CameraPhone extends Phone with Camera

  // intersection types + covariance
  trait HostConfig
  trait HostController {
    def get: Option[HostConfig]
  }

  trait PortConfig

  trait PortController {
    def get: Option[PortConfig]
  }

  def getConfigs(controller: HostController & PortController) : Option[HostConfig & PortConfig]= controller.get

  def main(args: Array[String]): Unit = {
    // doSomething(Some(30)) //not ok
    doSomething(Some(42))
    doSomething(None)
    doSomething(Option(42))
    println(boolOrInt)
    println(boolOrInt_v2)
    println(aMethod("Sabuj"))
    println(aMethod(10))
    useSmartDevice(new SmartPhone) // ring
    useSmartDevice(new CameraPhone) // snap
  }
}
