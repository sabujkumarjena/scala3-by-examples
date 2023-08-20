package scala3byexamples.section5

import scala.util.Try

object HigherKindedTypes {

  class HigherKindedType[F[_]] // hkt- type argument
  class HigherKindedType2[F[_], G[_], A]

  val hkExample = new HigherKindedType[List]
  val hkExample2 = new HigherKindedType2[List, Option, String]

  // can use hkts for methods as well

  // why: abstract libraries, e.g. Cats
  // example: Functor

  val aList = List(1, 2, 3)
  val anOption = Option(2)
  val aTry = Try(42)

  val anIncreamentedList = aList.map(_ + 1)
  val anIncreamentedOption = anOption.map(_ + 1)
  val anIncreamentedTry = aTry.map(_ + 1)

  // duplicated APIs

  def do10xList(list: List[Int]): List[Int] = list.map(_ * 10)
  def do10xOption(option: Option[Int]): Option[Int] = option.map(_ * 10)
  def do10xTry(theTry: Try[Int]): Try[Int] = theTry.map(_ * 10)

  // DRY principle

  // step 1: TC definition
  trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  // step 2: TC instances

  given listFunctor: Functor[List] with
    override def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)

  given optionFunctor: Functor[Option] with
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)

  // step 3: "user-facing" API
  def do10x[F[_]](container: F[Int])(using functor: Functor[F]): F[Int] =
    functor.map(container)(_ * 10)

  // step 4: Extension methods
  extension [F[_], A](container: F[A])(using functor: Functor[F])
    def map[B](f: A => B): F[B] = functor.map(container)(f)

  def do10x_v2[F[_]](container: F[Int])(using functor: Functor[F]): F[Int] =
    container.map(_ * 10) // map is a extension method

  def do10x_v3[F[_]: Functor](container: F[Int]): F[Int] = // context bound
    container.map(_ * 10) // map is a extension method

  /** Exercise : implement a new type class on the same structure as Functor In
    * the general API, must use for-comprehension
    */
  object Test {
    // step-1 : define TC
    trait Monad[F[_]] extends Functor[F] {
      // def unit[A](a: A): F[A]
      def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
      // def map[A, B](fa: F[A])(f: A => B): F[B] = flatMap(fa)((a: A) => unit(f(a)))
    }

    // step-2 : instances of typeclass

    given listMonad: Monad[List] with {
      // override def unit[A](a: A): List[A] = List(a)
      override def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)

      override def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] =
        fa.flatMap(f)
    }

    // step -3 : use-facing API

    def combine[F[_], A, B](fA: F[A], fB: F[B])(using
        monad: Monad[F]
    ): F[(A, B)] =
      monad.flatMap(fA)(a => monad.map(fB)(b => (a, b)))

    // step-4: extension methods

    extension [F[_], A](fa: F[A])(using monad: Monad[F]) {
      def map[B](f: A => B): F[B] = monad.map(fa)(f)
      def flatMap[B](f: A => F[B]): F[B] = monad.flatMap(fa)(f)
    }

    def combine_v2[F[_], A, B](fA: F[A], fB: F[B])(using
        monad: Monad[F]
    ): F[(A, B)] = fA.flatMap(a => fB.map(b => (a, b)))

    def combine_v3[F[_], A, B](fA: F[A], fB: F[B])(using
        monad: Monad[F]
    ): F[(A, B)] =
      for {
        a <- fA
        b <- fB
      } yield ((a, b))

    def combineList[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
      for {
        a <- listA
        b <- listB
      } yield (a, b)

    def combineOption[A, B](optA: Option[A], optB: Option[B]): Option[(A, B)] =
      for {
        a <- optA
        b <- optB
      } yield (a, b)

    def combineTry[A, B](tryA: Try[A], tryB: Try[B]): Try[(A, B)] =
      for {
        a <- tryA
        b <- tryB
      } yield (a, b)
  }
  def main(args: Array[String]): Unit = {
    println(do10x(List(1, 2, 3)))
    println(do10x(Option(3)))
    println(do10x_v2(List(1, 2, 3)))
    println(do10x_v3(List(1, 2, 3)))
    println(Test.combine_v3(List(1,2,3), List("sabuj", "Sagar")))
  }

}
