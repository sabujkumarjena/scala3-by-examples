package scala3byexamples.section2

import scala.annotation.targetName

object Monads {

  val f = (x: Int) => List(x, x + 1)
  val g = (x: Int) => List(x, x * 2)

  val pure = (x: Int) => List(x)

  val aList = List(1, 2, 3, 4)
  // prop 1: left identity

  val leftIdentity = pure(42).flatMap(f) == f(42)
  // prop 2 : right identity
  val rightIdentity = aList.flatMap(pure) == aList
  // prop 3 ; associativity
  /*
  [1,2,3].flatMap(x => [x, x+1]) = [1,2, 2,3, 3,4]
  [1,2,2,3,3,4].flatMap(x => [x, 2*x]) = [1,2, 2,4  2,4, 3,6,  3,6, 4,8 ]
  [1,2,3].flatMap(f).flatMap(g) = [1,2, 2,4  2,4, 3,6,  3,6, 4,8]

  [1,2,2,4] = f(1).flatMap(g)
  [2,4,3,6] = f(2).flatMap(g)
  [1,2,2,4] = f(3).flatMap(g)
  [1,2, 2,4  2,4, 3,6,  3,6, 4,8 ] = f(1).flatMap(g) ++ f(2).flatMap(g) ++ f(3).flatMap(g)
  [1,2,3].flatMap(x => f(x).flatMap(g))
   */

  val associativity =
    aList.flatMap(f).flatMap(g) == aList.flatMap(x => f(x).flatMap(g))
  // MONADS = chain dependent computations

  // Is it a monad?
  // yes PossiblyMonad is a Monad
  // interpretation: Any computation that might perform side effects
  // PossiblyMonad is IO monad
  case class PossiblyMonad[A](unsafeRun: () => A) { // 0-lamda ia computation  that produce a value and might have side effects
    def map[B](f: A => B): PossiblyMonad[B] =
      PossiblyMonad(() => f(unsafeRun()))

    def flatMap[B](f: A => PossiblyMonad[B]): PossiblyMonad[B] =
      PossiblyMonad(() => f(unsafeRun()).unsafeRun())
  }
  object PossiblyMonad {
    @targetName(
      "pure"
    ) // as apply is part of case class PossiblyMonad //so change target name of apply to pure at compile time
    def apply[A](value: => A): PossiblyMonad[A] = new PossiblyMonad(() =>
      value
    ) // new is added to get rid of tail recusion due to companion object PossiblyMonad's apply method
  }
  def possiblyMonadDemo(): Unit = {
    val aPossiblyMonad = PossiblyMonad(42)
    val f = (x: Int) => PossiblyMonad { println("increamenting monad"); x + 1 }
    val g = (x: Int) => PossiblyMonad { println("doubling monad "); x * 2 }
    val pure = (x: Int) => PossiblyMonad(x)
//prop 1: left-identity

    val leftIdentity = pure(42).flatMap(f) == f(42)
    println(leftIdentity) // false negative
    val rightIdentity = aPossiblyMonad.flatMap(pure) == aPossiblyMonad
    println(rightIdentity) // false negative
    val associativity =
      aPossiblyMonad.flatMap(f).flatMap(g) == aPossiblyMonad.flatMap(x =>
        f(x).flatMap(g)
      )
    println(associativity) // false negative
    println(PossiblyMonad(5) == PossiblyMonad(5)) // false negative

    // real tests: values produced + side effects ordering
    val leftIdentity_v2 = pure(42).flatMap(f).unsafeRun() == f(42).unsafeRun()
    println(leftIdentity_v2)
    val rightIdentity_v2 =
      aPossiblyMonad.flatMap(pure).unsafeRun() == aPossiblyMonad.unsafeRun()
    println(rightIdentity_v2)
    val associativity_v2 =
      aPossiblyMonad.flatMap(f).flatMap(g).unsafeRun() == aPossiblyMonad
        .flatMap(x => f(x).flatMap(g))
        .unsafeRun()
    println(associativity_v2)
    println(PossiblyMonad(5).unsafeRun() == PossiblyMonad(5).unsafeRun())

  }

  def possiblyMonadExample(): Unit = {
    val aPM = PossiblyMonad {
      println("my first possibly monad")
      // do some computation
      42
    }

    val anotherPM = PossiblyMonad {
      println("my second possibly monad")
      // do some computation
      "scala"
    }
    println(aPM.unsafeRun())
    println(anotherPM.unsafeRun())

    val aForComprehension = for { // computations are described but not EXECUTED
      num <- aPM
      lang <- anotherPM
    } yield s"$num-$lang"

    aForComprehension.unsafeRun()
    println(aForComprehension.unsafeRun())
  }

  def main(args: Array[String]): Unit = {
    println(leftIdentity)
    println(rightIdentity)
    println(associativity)
    possiblyMonadDemo()
    possiblyMonadExample()

  }

}
