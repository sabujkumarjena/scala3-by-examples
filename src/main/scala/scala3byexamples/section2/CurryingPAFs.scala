package scala3byexamples.section2

object CurryingPAFs {
  // currying
  val adder: Int => Int => Int =
    x => y => x + y

  val add2: Int => Int = adder(3) // y => 3 + y
  val five = add2(3) // 5
  val five_v2 = adder(2)(3)

  // curried methods
  def curriedAdder(x: Int)(y: Int): Int = x + y

  // methods != function values
  // converting methods to functions = eta-expansion
  val add3 = curriedAdder(3)
  val seven = add3(4) // 7

  def increament(x: Int): Int = x + 1
  val aList = List(1, 2, 3)
  val increamentedList = aList.map(increament) // eta-expansion

  // underscore

  def concatenator(a: String, b: String, c: String): String = a + b + c
  val concatenatorFun = concatenator

  val insertMiddleName =
    concatenator(
      "Sabuj",
      _: String,
      "Jena"
    ) // x => concatenator("....," x, "...")
  val kumar = insertMiddleName("Kumar")
  val fillFirstNLastName = concatenator(_: String, "Kumar", _: String)
  val fullName = fillFirstNLastName("sabuj", "Jena")

  // Exercises

  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedMethod(x: Int)(y: Int) = x + y
  // a - obtain add7 function: x => x + 7 out of above 3 definition

  val add7 = (x: Int) => simpleAddFunction(x, 7)
  val add7_v2 = (x: Int) => simpleAddMethod(x, 7)
  val add7_v3 = (x: Int) => curriedMethod(x)(7)
  val add7_v4 = curriedMethod(_: Int)(7)
  val add7_v5 = simpleAddMethod(_, 7)
  val add7_v6 = simpleAddFunction.curried(7)

  // b- process a list of numbers and return their string representations under different formats

  // step-1: create a curried formatting method with a formatting string and a value
  def curriedFormatter(fmt: String)(number: Double): String = fmt.format(number)

  val piWith2Dec = "%4.2f".format(Math.PI) // 3.14
  val someDecimals = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  // methods vs functions + by-name vs 0-lambdas
  def byName(n: => Int) = n + 1
  def byLambda(f: () => Int) = f() + 1
  def method: Int = 42
  def parenMethod(): Int = 42

  byName(42) // ok
  byName(method) // 43 eta-expanded? No - method is invoked here
  byName(parenMethod()) // 43
  // byName(parenMethod) // won't compile //0-lamda is not compatible with by-name parameter
  byName((() => 42)())
  // byName(() => 42) //not ok

  // byLambda(23) // not ok
// eta-expansion: converting methods to functions
// byLambda(method) //eta-expansion is NOT possible //method can't be auto eta-expanded in case of 0-arg lamda
  byLambda(parenMethod) // eta- expansion is done
  byLambda(() => 42)
  byLambda(() => parenMethod())


  def main(args: Array[String]): Unit = {
    println(kumar)
    println(fullName)
    println(piWith2Dec)
    println(add7_v4(3))
    println(someDecimals.map(curriedFormatter(("%4.2f"))))
    println(someDecimals.map(curriedFormatter(("%4.6f"))))
    println(someDecimals.map(curriedFormatter(("%4.14f"))))
  }
}
