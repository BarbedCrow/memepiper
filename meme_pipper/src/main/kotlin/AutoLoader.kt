import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.apache.Apache
import io.ktor.client.response.readBytes

suspend fun getPosts(groupName: List<String>) = httpCall(createUrl(groupName).apply(::println))

private suspend fun httpCall(url: String): List<PostRequest> {
    println("\n$url\n")
    val client = HttpClient(Apache)
    val rowJson = String(client.call(url).response.readBytes())
    println(rowJson)
    return JsonParser().parse(rowJson).asJsonObject["response"].asJsonArray.map {
        if (!it.isJsonObject || !it.asJsonObject.has("items")) emptyList() else
        it.asJsonObject["items"].asJsonArray.map {
            val id = it.asJsonObject["from_id"].asString + it.asJsonObject["id"].asString.apply(::println)
            val fromId = it.asJsonObject["from_id"].asString
            val text = it.asJsonObject["text"].asString
            var photo: JsonElement? = null
            if (it.asJsonObject.has("attachments")) {
                photo = it.asJsonObject["attachments"].asJsonArray.firstOrNull {
                    it.asJsonObject["type"].asString == "photo"
                }
            }
            (if (photo == null) null else {
                val picUrl =
                    photo.asJsonObject["photo"].asJsonObject["sizes"].asJsonArray.last().asJsonObject["url"].asString
                PostRequest(fromId, id, picUrl, text)
            })
        }.filterNotNull().also(::println)
    }.flatten()
}

fun createUrl(groupNames: List<String>): String {
    val methodName = "execute"
    val code = createVkScript(groupNames)
    return "https://api.vk.com/method/$methodName?code=$code&access_token=$key&v=$version".format()
}

private val version = "5.87"
private val key = Prop.prop.getProperty("key")

private fun createVkScript(groupName: List<String>): String {
    val groupName2 = groupName.map { "\"$it\"" }
    return "var pages = $groupName2;" +
            "var result = [];" +
            "var i = 0;" +
            "while (i < pages.length) {" +
            "var a = API.wall.get({\"owner_id\":pages[i], \"count\":\"100\"});" +
            "result.push(a);" +
            "i = i - (-1);" +
            "}" +
            "return result;"
}
