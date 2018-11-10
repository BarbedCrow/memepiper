import IdApi.Companion.build
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.HttpsRedirect
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.semanticmetadata.lire.sampleapp.Indexer
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.util.*
import kotlin.concurrent.schedule

object Prop {
    val prop = Properties()
}

val initialGroupIds = listOf("-29534144", "-25670128", "-92337511", "-52537634", "-72495085", "-33414947", "-51016572", "-45745333", "-86854270", "-44781847", "-66678575", "19799369", "-36775802", "-46521427")
const val UPDATE_FREQUENCY_MS = 1_000_000L
val timer = Timer()

@Suppress("unused")
fun Application.main(args: Array<String>) {
    initProperties()
    initDB()
    routings()
    runSchedule()
//    createSSL(args)
}
//fun main(args: Array<String>) {
//    runBlocking {
//        GlobalScope.launch {
//            val groupName = listOf("oldlentach", "paper.comics")
//            initProperties()
//            createUrl(groupName)
//            getPosts(groupName)
//            getIndex("https://sun9-3.userapi.com/c635106/v635106648/15613/5F5mjxYmaxM.jpg")
//        }
//    }
//}

fun createSSL(args: Array<String>) {
    val file = File("build/temporary.jks")
    if (!file.exists()) {
        file.parentFile.mkdirs()
    }
    // run embedded server
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

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
//        install(HttpsRedirect) {
//            sslPort = 8443
//        }
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
        get("/get_memes_similar/{id}&{postId}"){
            val id = call.parameters["id"]
            val postId = call.parameters["postId"]
            if (postId == null) call.respond(HttpStatusCode.BadRequest) else {
                wrapRespond {
                    build(id).getMemesSimilarTo(postId)
                }
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
        if (!writePostToDB(post)) return
    }
}

fun writePostToDB(newPost: PostRequest) : Boolean{
    val newIndexer = Indexer()
    newIndexer.indexImageFromURL(URL(newPost.urlPic))
    val posts = PostEntity.all()
    var tag : TagEntity
    for (post in posts){
        if (newPost.postId == post.postId) return false
        val indexer = Gson().fromJson(post.index, Indexer().javaClass)
        val index = newIndexer.compaire(indexer)
        if (index == 0){
            return true
        }else if (index == 1){
            addPost(newPost, post.tag, newIndexer)
            return true
        }
    }

    createNewTag(newPost, newIndexer)
    return true
}

fun createNewTag(postReq: PostRequest, indexer: Indexer) = transaction {
    val post = addPost(postReq, null, indexer)
    val tag = TagEntity.new {
        agents = jacksonObjectMapper().writeValueAsString(listOf(post.id))
    }

    post.tag = tag.id
}

fun getTag(indexer: Indexer) = transaction {
    val tags = TagEntity.all().map {
        jacksonObjectMapper().readValue<List<Long>>(it.agents).map { PostEntity[it] }
    }
    tags.map{ tag ->
         tag.map {
            val another = Gson().fromJson(it.index, Indexer().javaClass)
            val value = indexer.compaire(indexer)
            if (value == 2) {
                return@transaction null
            } else value
        }
    }
}
fun getIndex(urlPic: String): String {
    val indexer = Indexer()
    indexer.indexImageFromURL(URL(urlPic))
    val str= Gson().toJson(indexer)
    println(str)
    return str
}

data class PostRequest(
    val groupDomain: String,
    val postId: String,
    val urlPic: String,
    val text: String
)

//val loader = AutoLoader()
