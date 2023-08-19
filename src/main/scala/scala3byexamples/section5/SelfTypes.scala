package scala3byexamples.section5

object SelfTypes {
  trait Instrumentalist {
    def play(): Unit
  }

  trait Singer {
    self: Instrumentalist => // self-type: whoever implements Singer MUST also implement Instrumentalist
    // *self* name can be anything, usually called "self"
    // DO NOT confuse with a lamda
    // rest of your API
    def sing(): Unit
  }
  class LeadSinger extends Singer with Instrumentalist { // ok
    override def sing(): Unit = ()

    override def play(): Unit = ()

  }

  // not  ok because I haven't extended INSTRUMENTALIST
//  class Vocalist extends Singer {
//    override def sing(): Unit = ???
//  }

  val sunidhi = new Singer with Instrumentalist { // ok
    override def sing(): Unit = ()

    override def play(): Unit = ()
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println(" some guitar solo")
  }

  val loy =
    new Guitarist with Singer { // ok - extending Guitar <: Instrumentalist
      override def sing(): Unit = println("loy is singing with guitar")
    }

  // self-types vs inheritance
  class A
  class B extends A // B is A

  trait T
  trait S { self: T => } // S "requires a" T

  // self-type for DI = "cake pattern"

  abstract class Component {
    // main general API
  }
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val component: Component) // regulat DI

  // cake pattern

  trait ComponentLayer1 {
    // API
    def actionLayer1(x: Int): Int
  }

  trait ComponentLayer2 { self: ComponentLayer1 =>
    // some other API
    def actionLayer2(x: String): Int
  }

  trait Application { self: ComponentLayer1 with ComponentLayer2 =>
    // your main API
  }

  // example: a photo taking application API in the style of Instagram
  // layer 1 - small components

  trait Picture extends ComponentLayer1
  trait Stats extends ComponentLayer1

  // layer 2 - compose

  trait ProfilePage extends ComponentLayer2 with Picture
  trait Analytics extends ComponentLayer2 with Stats

  // layer 3 - application

  trait AnalyticsApp extends Application with Analytics

  // dependencies are specified in layers, like baking a cake
  // when you put the pieces together, you can pick a possible implementation from each layer

  // self-type: preserve the "this" instance

  class SingerWithInnerClass { self => // this
    class Voice {
      def sing() =
        this.toString // this == the voice, use self to refer SingerWithInnerclass
    }
  }

  // cyclical inheritance doesn't work
//  class X extends Y
//  class Y extends X

//cyclical dependencies
  trait X { self: Y => }
  trait Y { self: X => }
  trait Z extends X with Y
  def main(args: Array[String]): Unit = {
    loy.play()
    loy.sing()
  }

}
