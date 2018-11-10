import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.Application
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class BadRequestException : Exception()

data class Response(val count: Int, val page: List<String>)

class IdApi(val uid: Long) {
    val mapper = jacksonObjectMapper()

    companion object {
        fun build(rowId: String?): IdApi {
            return if (rowId == null) {
                throw BadRequestException()
            }  else {
                IdApi(rowId.toLong())
            }
        }
    }
    fun getPages() : Response  = transaction {
        val user = UserEntity.find { User.vkId eq uid }.firstOrNull() ?: throw BadRequestException()
        val groups = mapper.readValue<List<String>>(user.groups).map { GroupEntity[it.toLong()].id }
        val ans = PostEntity.find { Post.group inList groups }.map { it.url }
        Response(ans.count(), ans)
    }

    fun addGroup(url: String) : Long = transaction {
        GroupEntity.new {
            this.url = url
            this.lastRead = DateTime.now(DateTimeZone.UTC)
        }.id.value
    }

    fun addUser(): Long = transaction {
        UserEntity.new {
            this.vkId = uid
            this.groups = mapper.writeValueAsString(emptyList<String>())
        }.id.value
    }

    fun addGroupToUser(groupId: Long) = transaction {
        val user = UserEntity.find { User.vkId eq uid }.firstOrNull() ?: throw BadRequestException()
        val oldGroups = mapper.readValue<List<String>>(user.groups)
        user.groups = mapper.writeValueAsString(oldGroups + (groupId.toString()))
        user.flush()
    }

    fun addPost(url: String, groupId: Long) = transaction {
        PostEntity.new {
            this.group = GroupEntity[groupId]
            this.url = url
            this.tags
        }
    }
}