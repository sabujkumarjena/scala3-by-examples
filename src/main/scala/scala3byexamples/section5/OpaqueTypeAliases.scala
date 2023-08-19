package scala3byexamples.section5

object OpaqueTypeAliases {
  object SocialNetwork {
    // some data structure = "domain"
    opaque type Name = String
    object Name {
      def apply(str: String): Name = str
    }

    extension (name: Name)
      def length: Int = name.length // use String API //possible as it is defined inside
    // inside object SocialNetwork Name and String can be used interchangeably
    def addFriend(person1: Name, person2: Name) : Boolean =
      person1.length == person2.length // use the entire String API
  }
  // outside object SocialNetwork, Name and String are not related
  import SocialNetwork.*
  //val name: Name = "Sabuj" // will not compile
  // why: you don't need (or want) to have access to the entire String API for the Name type

  object Graphics {
    opaque  type Color = Int //in hex
    opaque type ColorFilter <: Color = Int

    val Red: Color = 0xFF000000
    val Green: Color = 0x00FF0000
    val Blue: Color = 0x0000FF00
    val halfTransparency: ColorFilter = 0x88 //50%

  }
  import Graphics.*
  case class OverlayFilter(c: Color)

  val fadeLayer = OverlayFilter(halfTransparency) // ColorFilter <: Color

  //how can we create instances of opaque types + how to access their APIs
  // 1- companion objects
  val aName = Name("Sabuj") //ok
  // 2- extension method
  val nameLength = aName.length

  def main(args: Array[String]): Unit = {
    println(nameLength)
  }

}
