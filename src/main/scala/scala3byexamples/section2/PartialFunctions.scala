package scala3byexamples.section2

object PartialFunctions {

  //partial function
  val aPartialFunction : PartialFunction[Int, Int] = { // x => x match {....}
    case 1 => 41
    case 2 => 46
    case 3 => 47
    case 7 => 49
  }

  val canCallAt33 = aPartialFunction.isDefinedAt(33)

  val liftedPF = aPartialFunction.lift //Int => Option[Int

  val anotherPF: PartialFunction[Int, Int] = {
    case 33 => 76
  }

  val pfchain = aPartialFunction.orElse(anotherPF)

  //HOF accepts PFs as arguments

  val aList = List(1,2,3, 4)

  val changedList = aList.map{
    case 1 => 4
    case 2 => 5
    case _ => 0
  }
  def main(args: Array[String]): Unit = {
    println(aPartialFunction(2))
    println(liftedPF(3))
    println(liftedPF(33))
    println(pfchain(33))
    println(changedList)
  }
}
