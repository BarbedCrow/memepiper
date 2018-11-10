import IdApi.Companion.build
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import java.util.*
import kotlin.concurrent.schedule

val initialGroupIds = listOf("oldlentach", "mnogoanekdot")
const val UPDATE_FREQUENCY_MS = 5 * 1000L
val timer = Timer()

fun Application.main() {
    initDB()
    this.routings()
    runSchedule()
}

//fun main(args: Array<String>) {
//    runSchedule()
//
//}

fun initDB() {
    Database.connect("jdbc:mysql://localhost:3306/memebd", driver = "com.mysql.jdbc.Driver", user = "meme", password = "123333321")
    TrySetInitialGroupIds()
    createTables()
}

fun TrySetInitialGroupIds(){
    var groupIds = GroupEntity.all().map { it.uid }
    for(groupId in initialGroupIds){
        if (groupIds.contains(groupId)){
            continue
        }

        addGroup(groupId)
    }
}

fun Application.routings() {
    routing {
        install(ContentNegotiation) {
            gson {}
        }
        get("hu"){

        }
        get("/get_page/{id}") {
            val id = call.parameters["id"]
            wrapRespond {
                build(id).getPages()
            }
        }
    }
}

suspend fun PipelineContext<Unit,ApplicationCall>.wrapRespond(build: () -> Any) {
    try {
        call.respond(build())
    } catch (e: BadRequestException) {
        call.respond(HttpStatusCode.BadRequest)
    }
}

fun runSchedule(){
    timer.schedule(0, UPDATE_FREQUENCY_MS){
        val groupNames = listOf("hui")
        GlobalScope.launch {
            WritePostsToDB(loader.getPosts(groupNames))
        }
    }
}

fun WritePostsToDB(posts: List<PostRequest>){
    for(post in posts){
        WritePostToDB(post)
    }
}

fun WritePostToDB(post: PostRequest){
    print(post.urlPic)
    addPost(post)
}

data class PostRequest(val idGroup : String, val urlGroup: String, val urlPost: String, val urlPic: String, val text: String)

val loader = AutoLoader()
class AutoLoader {
    suspend fun getPosts(groupNames: List<String>): List<PostRequest> {
        val req = PostRequest("oldlentach","urlGroup","urlPost", "urlPic", "text")
        return listOf(req)
    }
}