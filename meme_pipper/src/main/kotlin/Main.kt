import IdApi.Companion.build
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.sql.Database


fun Application.main() {
    initDB()
    routings()
}

//fun main(args: Array<String>) {
//    initDB()
//    val idApi = IdApi(1)
//    idApi.addUser()
//    val groupId = idApi.addGroup("hui")
//    idApi.addGroupToUser(groupId)
//    idApi.addPost("zalupa", groupId)

//}

fun initDB() {
    Database.connect("jdbc:mysql://localhost:3306/memebd", driver = "com.mysql.jdbc.Driver", user = "meme", password = "123333321")
    createTables()
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
    }
}

suspend fun PipelineContext<Unit,ApplicationCall>.wrapRespond(build: () -> Any) {
    try {
        call.respond(build())
    } catch (e: BadRequestException) {
        call.respond(HttpStatusCode.BadRequest)
    }
}