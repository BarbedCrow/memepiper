import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*

data class Response(val count: Int, val page: List<Int>)

fun Application.module() {
    routing {
        install(ContentNegotiation) {
            gson {}
        }
        get("/get_page/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                val ans = Response(id.toInt(), listOf(1, 2, 3))
                call.respond(ans)
            }
        }
    }
}