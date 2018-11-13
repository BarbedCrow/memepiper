import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.exposed.sql.transactions.transaction

class BadRequestException : Exception()

const val MEMES_COUNT = 20

data class Response(val count: Int, val page: List<String>)

@Suppress("unused")
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
        val ans = PostEntity.find { Post.group inList groups }.map { it.groupDomain }
        Response(ans.count(), ans)
    }

    fun addUser(): Long = transaction {
        UserEntity.new {
            this.vkId = uid
            this.groups = mapper.writeValueAsString(emptyList<String>())
            this.seenPostIds = mapper.writeValueAsString(emptyList<String>())

        }.id.value
    }

    fun addSeenPost(postId : String) = transaction {
        val user = UserEntity.find { User.vkId eq uid }.firstOrNull() ?: throw BadRequestException()
        val seenPostIds = mapper.readValue<List<String>>(user.seenPostIds)
        user.seenPostIds = mapper.writeValueAsString(seenPostIds + postId)
        user.flush()
    }

    fun addGroupToUser(groupId: String) = transaction {
        val user = UserEntity.find { User.vkId eq uid }.firstOrNull() ?: throw BadRequestException()
        val oldGroups = mapper.readValue<List<String>>(user.groups)
        user.groups = mapper.writeValueAsString(oldGroups + groupId)
        user.flush()
    }

    fun getMemes() : List<PostRequest> = transaction {
        val user = UserEntity.find { User.vkId eq uid }.firstOrNull() ?: throw BadRequestException()
        val seenPostIds = mapper.readValue<List<String>>(user.seenPostIds)
        val posts = PostEntity.all()
        val postsToSend = mutableListOf<PostRequest>()
        for (post in posts){
            if(seenPostIds.contains(post.postId)){
                continue
            }

            addSeenPost(post.postId)
            postsToSend.add(PostRequest(post.groupDomain, post.postId, post.urlPic, post.text))
            if (postsToSend.size == MEMES_COUNT){
                break
            }
        }

        postsToSend
    }

    fun getMemesSimilarTo(postId: String) = transaction {
        val post = PostEntity.find { Post.postId eq postId }.first()
        val similarPosts = PostEntity.find{Post.tag eq post.tag}.take(MEMES_COUNT)
    }

}