package scala3byexamples.section4

object TypeClasses {
  /*
    Small library to serialize some data to a standard format (HTML)
   */

  // v1: the OO way
  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String =
      s"<div> $name ($age yo) ,<a href=$email> <div>"
  }
  val bob = User("Bob", 21, "bob@xyz.com")
  val bob2Html = User("Bob", 21, "bob@xyz.com").toHtml
  // same for other data structures that we want to serialize
  /*
    Drawbacks:
    - only available for the types WE write
    - can only provide ONE implementation
   */
  // v2: pattern matching
  object HTMLSerializerPM {
    def serializeToHtml(value: Any): String = value match {
      case User(name, age, email) =>
        s"<div> $name ($age yo) ,<a href=$email> <div>"
      case _ => throw new IllegalArgumentException(" data type not supported")
    }
  }
  /*
    Drawbacks:
    - lost type safety
    - need to modify a SINGLE piece of code every time
    - still ONE implementation
   */
  // v3 - type class

  // part 1  - type class definition
  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }
  // part 2 - type class instances for the supported types
  given userSerializer: HTMLSerializer[User] with {
    override def serialize(value: User): String = {
      val User(name, age, email) = value
      s"<div> $name ($age yo) ,<a href=$email> <div>"
    }
  }

  val bob2Html_v2 = userSerializer.serialize(bob)
  /*e
    Benefits:
    - can define serializers for other types OUTSIDE the "library"
    - multiple serializers for the same type, pick whichever you want by organizing given type class instances appropriately
   */
  import java.util.Date
  given dateSerializer: HTMLSerializer[Date] with {
    override def serialize(date: Date): String = s"<div>${date.toString()}</div>"
  }

  object SomeOtherSerializerFunctionality { //organize givens properly
    given partialUserSerializer :HTMLSerializer[User] with {
      override def serialize(user: User): String = s"<div>${user.name}</div>"
    }
  }

  //part 3 - using the type class (user-facing API)
  object HTMLSerializer {
    def serialize[T](value: T)(using serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](using serializer: HTMLSerializer[T]): HTMLSerializer[T] = serializer
  }
  val bob2Html_v3 = HTMLSerializer.serialize(bob)
  val bob2Html_v4 = HTMLSerializer[User].serialize(bob)

  //part 4
  object HTMLSyntax {
    extension [T](value: T)
      def toHTML(using serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  /*
   Cool!!
    - extends functionality to new types that we want to support
    - flexibility to add TC instances in a different place than the definition of the TC
    - choose implementations (by importing methods)
   */
  import HTMLSyntax.*
  val bob2Html_v5 = bob.toHTML
  def main(args: Array[String]): Unit = {
    println(bob2Html)
    println(bob2Html_v2)
    println(bob2Html_v3)
    println(bob2Html_v4)
    println(bob2Html_v5)
  }

}
