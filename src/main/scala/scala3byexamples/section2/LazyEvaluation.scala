package scala3byexamples.section2
object LazyEvaluation {

  /*
  Example 1 : call by need
   */
  lazy val x: Int = {
    println("Hello")
    42
  }

  // lazy DELAYS the evaluation of a value until the first use
  // evaluation occurs once in both lazy val or val

  // call by need = call by name + lazy values
  def byNameMethod(n: => Int): Int =
    n + n + n + 1

  def retriveMagicValue() = {
    println("waiting..")
    Thread.sleep(1000)
    42
  }

  def demoByName(): Unit = {
    println(byNameMethod(retriveMagicValue()))
    // retriveMagicValue() + retriveMagicValue() + retriveMagicValue() + 1
  }

  def byNeedMethod(n: => Int): Int = {
    lazy val lazyN = n // memoization
    lazyN + lazyN + lazyN + 1
  }

  def demoByNeed(): Unit = {
    println(byNeedMethod(retriveMagicValue()))
  }

  /*
  Example 2 : withFilter
   */
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val aList = List(10, 23, 45, 15, 26)

  def demoFilter(): Unit = {
    val lt30 = aList.filter(lessThan30)
    val gt20 = lt30.filter(greaterThan20)
    println(gt20)
  }
  def demoWithFilter(): Unit = {
    val lt30 = aList.withFilter(lessThan30)
    val gt20 = lt30.withFilter(greaterThan20)
    println(gt20.map(identity))
  }

  def demoForComprehension(): Unit = {
    val forComp = for {
      n <- aList if lessThan30(n) && greaterThan20(n)
    } yield n
    println(forComp)
  }
  def main(args: Array[String]): Unit = {
    println(x)
    println(x)

    demoByName()
    demoByNeed()
    demoFilter()
    demoWithFilter()
    demoForComprehension()
  }
}
