import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Post: LongIdTable() {
    val idGroup = varchar("idGroup", 128)
    val urlGroup = varchar("urlGroup", 256)
    val urlPost = varchar("urlPost", 256)
    val urlPic = varchar("urlPic", 256)
    var text = varchar("text", 4096)
    val group = reference("group", Group, ReferenceOption.CASCADE)
    val tag = varchar("tag", 256).default("")
    var index = text("index").default("")
}

object Group: LongIdTable() {
    val lastRead = datetime("lastRead")
    val uid = varchar("id", 128)
}

object User: LongIdTable() {
    val vkId = long("vkId")
    //List<String>
    val groups = text("groupses")
}

class PostEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PostEntity>(Post)

    var idGroup by Post.idGroup
    var urlGroup by Post.urlGroup
    var urlPost by Post.urlPost
    var urlPic by Post.urlPic
    var text by Post.text
    var group by GroupEntity referencedOn Post.group
    var tag by Post.tag
    var index by Post.index
}

class GroupEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GroupEntity>(Group)

    var lastRead by Group.lastRead
    var uid by Group.uid
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(User)
    var vkId by User.vkId
    var groups by User.groups
}

fun createTables()  = transaction {
    addLogger(StdOutSqlLogger)
    SchemaUtils.create(Post, Group, User)
}

