package scala3byexamples.section4

import scala.concurrent.{ExecutionContext, Future}

object ContextFunctions {

  // defs can take using clauses
  def methodWithoutContextArguments(nonContextArgument: Int)(
      nonContextArg2: String
  ): String = "foo"
  def methodWithContextArguments(nonContextArgument: Int)(using
      nonContextArg2: String
  ): String = "bar"

  // eta-expansion
  val functionWithoutContextArguments = methodWithoutContextArguments
  // val func2 = methodWithContextArguments //doesn't work

  // context function
  val functionWithContextArguments: Int => String ?=> String =
    methodWithContextArguments

  val result = functionWithContextArguments(2)(using "Scala")

  /*
    - convert methods with using clauses to function values
    - HOF with function values taking given instances as arguments
   */
  // execution context here
  // val increamentAsync: Int => Future[Int] = x => Future(x + 1) //doesn't work without a given EC in scope

  val increamentAsync: ExecutionContext ?=> Int => Future[Int] = x =>
    Future(x + 1)
  def main(args: Array[String]): Unit = {
    println(result)
  }
}
