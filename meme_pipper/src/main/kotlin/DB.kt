import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object Post: LongIdTable() {
    val groupDomain = varchar("groupDomain", 128)
    val postId = varchar("postId", 256)
    val urlPic = varchar("urlPic", 256)
    var text = varchar("text", 4096)
    val group = reference("group", Group, ReferenceOption.CASCADE)
    val tag = varchar("tag", 256).default("")
    var index = text("index").nullable()
}

object Group: LongIdTable() {
    val lastRead = datetime("lastRead")
    val domain = varchar("domain", 128)
}

object User: LongIdTable() {
    val vkId = long("vkId")
    //List<String>
    val groups = text("groupses")
    val seenPostIds = text("seenPostIds")
}

class PostEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PostEntity>(Post)

    var groupDomain by Post.groupDomain
    var postId by Post.postId
    var urlPic by Post.urlPic
    var text by Post.text
    var group by GroupEntity referencedOn Post.group
    var tag by Post.tag
    var index by Post.index
}

class GroupEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GroupEntity>(Group)

    var lastRead by Group.lastRead
    var domain by Group.domain
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(User)
    var vkId by User.vkId
    var groups by User.groups
    var seenPostIds by User.seenPostIds
}

fun createTables()  = transaction {
    addLogger(StdOutSqlLogger)
    SchemaUtils.create(Post, Group, User)
}

