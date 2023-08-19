package scala3byexamples.section5

object PathDependentTypes {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def process(arg: Inner) = println(arg)
    def processGeneral(arg: Outer#Inner) = println(arg)
  }

  val outer = new Outer
  val inner: outer.Inner =
    new outer.Inner // outer.Inner is a  separate TYPE = path-dependent type

  val outerA = new Outer
  val outerB = new Outer
  // val inner2: outerA.Inner = new outerB.Inner //path-dependent types are DIFFERENT
  val innerA = new outerA.Inner
  val innerB = new outerB.Inner

  // outerA.process(innerB) //type mismatch
  outer.process(inner) // ok

  // parent-type: Outer#Inner
  outerA.processGeneral(innerA) // ok
  outerA.processGeneral(innerB) // ok outerB.Inner <: Outer#Inner

  /*
  Why :
  - type-checking/type inference, e.g. Akka Stream: Flow[Int, Int, NotUsed]#Repr
  - type-level programming
   */

  // methods with dependent types: return a different COMPILE-TIME type depending on the argument
  // no need for generics

  trait Record {
    type Key
    def defaultValue: Key
  }

  class StringRecord extends Record {
    override type Key = String

    override def defaultValue: String = ""
  }

  class IntRecord extends Record {
    override type Key = Int

    override def defaultValue = 0
  }

  // user-facing api
  def getDefaultIdentifier(record: Record): record.Key  = record.defaultValue

  val aString: String = getDefaultIdentifier(new StringRecord) // a string
  val anInt: Int = getDefaultIdentifier(new IntRecord) // an int

  // functions with dependent types

  val getDefaultIdentifierFunc: Record => Record#Key = getDefaultIdentifier
  def main(args: Array[String]): Unit = {}

}
