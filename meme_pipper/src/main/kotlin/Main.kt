import IdApi.Companion.build
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
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.FileInputStream
import java.util.*
import kotlin.concurrent.schedule

object Prop {
    val prop = Properties()
}

val initialGroupIds = listOf("-29534144", "-25670128", "-92337511", "-52537634", "-72495085", "-33414947", "-51016572", "-45745333", "-86854270", "-44781847", "-66678575", "19799369", "-36775802", "-46521427")
const val UPDATE_FREQUENCY_MS = 1_000_000L
val timer = Timer()

@Suppress("unused")
fun Application.main() {
    initProperties()
    initDB()
    routings()
    runSchedule()
}
//fun main(args: Array<String>) {
//    runBlocking {
//        GlobalScope.launch {
//            val groupName = listOf("oldlentach", "paper.comics")
//            initProperties()
//            createUrl(groupName)
//            getPosts(groupName)
//        }
//    }
//}

//fun main(args: Array<String>) {
//    initDB()
//    val idApi = IdApi(1)
//    idApi.addUser()
//    val groupId = idApi.addGroup("hui")
//    idApi.addGroupToUser(groupId)
//    idApi.addPost("zalupa", groupId)

fun initProperties(){
    Prop.prop.load(FileInputStream("src/main/resources/config.properties"))
}

fun initDB() {
    Database.connect(
        "jdbc:mysql://localhost:3306/memebd",
        driver = "com.mysql.jdbc.Driver",
        user = "meme",
        password = "123333321"
    )
    createTables()
    trySetInitialGroupIds()
}

fun trySetInitialGroupIds() = transaction {
    val groupIds = GroupEntity.all().map { it.domain }
    for (groupId in initialGroupIds) {
        if (groupIds.contains(groupId)) {
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
        get("/get_page/{id}") {
            val id = call.parameters["id"]
            wrapRespond {
                build(id).getPages()
            }
        }
        get("/get_memes/{id}") {
            val id = call.parameters["id"]
            wrapRespond {
                build(id).getMemes()
            }
        }
        get("/add_user/{id}") {
            val id = call.parameters["id"]
            wrapRespond {
                build(id).addUser()
            }
        }
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.wrapRespond(build: () -> Any) {
    try {
        call.respond(build())
    } catch (e: BadRequestException) {
        call.respond(HttpStatusCode.BadRequest)
    }
}

fun runSchedule() {
    timer.schedule(0, UPDATE_FREQUENCY_MS) {
        GlobalScope.launch {
            writePostsToDB(getPosts(initialGroupIds))
        }
    }
}

fun writePostsToDB(posts: List<PostRequest>) {
    for (post in posts) {
        writePostToDB(post)
    }
}

fun writePostToDB(post: PostRequest) {
    print(post.urlPic)
    addPost(post)
}

data class PostRequest(
    val groupDomain: String,
    val postId: String,
    val urlPic: String,
    val text: String
)

//val loader = AutoLoader()
