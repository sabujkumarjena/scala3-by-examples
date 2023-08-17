package scala3byexamples.practicals

object JSONSerialisation {

  /*
    Users, posts, feeds
    Serialize to JSON
   */

  case class User(name: String, age: Int, email: String)
  import java.util.Date
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /*
  1 - intermediate data: numbers, strings, lists, dates, objects
  2 - type class to convert data to intermediate data
  3- serialize to JSON
   */

  sealed trait JSONValue {
    def stringify: String
  }
  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = "\"" + value + "\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify: String = value.toString
  }

  final case class JSONArray(value: List[JSONValue]) extends JSONValue {
    override def stringify: String =
      value.map(_.stringify).mkString("[", ",", "]")
  }

  final case class JSONObject(value: Map[String, JSONValue]) extends JSONValue {
    override def stringify: String = value
      .map { case (key, value) =>
        "\"" + key + "\"" + ":" + value.stringify
      }
      .mkString("{", ",", "}")
  }

  // part 2 - type class pattern
  // 1 - TC definition

  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }
  // 2- TC instances for String, Int, Date,User, Post, Feed
  given stringConverter: JSONConverter[String] with
    override def convert(value: String): JSONValue = JSONString(value)

  given intConverter: JSONConverter[Int] with
    override def convert(value: Int): JSONValue = JSONNumber(value)

  given dateConverter: JSONConverter[Date] with
    override def convert(value: Date): JSONValue = JSONString(value.toString)

  given userConverter: JSONConverter[User] with
    override def convert(value: User): JSONValue = JSONObject(
      Map(
        "name" -> stringConverter.convert(value.name),
        "age" -> intConverter.convert(value.age),
        "email" -> stringConverter.convert(value.email)
      )
    )

  given postConverter: JSONConverter[Post] with
    override def convert(value: Post): JSONValue = JSONObject(
      Map(
        "content" -> stringConverter.convert(value.content),
        "createdAt" -> dateConverter.convert(value.createdAt)
      )
    )

  given feedConverter: JSONConverter[Feed] with
    override def convert(feed: Feed): JSONValue = JSONObject(
      Map(
        "user" -> userConverter.convert(feed.user),
        "posts" -> JSONArray(feed.posts.map(postConverter.convert))
      )
    )
  // 3- user-facing API
  object JSONConverter {
    def convert[T](value: T)(using converter: JSONConverter[T]): JSONValue =
      converter.convert(value)

    def apply[T](using converter: JSONConverter[T]): JSONConverter[T] = converter
  }
//example
  val now = new Date(System.currentTimeMillis())
  val jack = User("Jack", 37, "jack@xyz.com")
  val feed = Feed(
    jack,
    List(
      Post("Hello, I am Jack", now),
      Post("Scala is awsome", now)
    )
  )

  // 4- extension methods
  object JSONSyntax {
    extension [T](value: T) {
      def toJSONValue(using converter: JSONConverter[T]): JSONValue =
        converter.convert(value)
      def toJSON(using converter: JSONConverter[T]): String = toJSONValue.stringify
    }
  }

  val data = JSONObject(
    Map(
      "user" -> JSONString("Bob"),
      "posts" -> JSONArray(
        List(
          JSONString(" Scala is awsome!"),
          JSONNumber(42)
        )
      )
    )
  )
  def main(args: Array[String]): Unit = {
    println(data.stringify)
    println(JSONConverter.convert(feed).stringify)
    import JSONSyntax.*
    println(feed.toJSON)
  }

}
