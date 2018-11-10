import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.sql.BatchUpdateException

fun addPost(postReq: PostRequest) = transaction {
    try {
        PostEntity.new {

            this.groupDomain = postReq.groupDomain
            this.group = GroupEntity.find { Group.domain eq groupDomain }.firstOrNull() ?: throw BadRequestException()
            this.postId = postReq.postId
            this.urlPic = postReq.urlPic
            this.text = postReq.text
//            this.index
//            this.tag
        }
    } catch (e: BatchUpdateException) {
        println("ERROR")
    }
}

fun addGroup(domain : String) : Long = transaction {
    GroupEntity.new {
        this.domain = domain
        this.lastRead = DateTime.now(DateTimeZone.UTC)
    }.id.value
}