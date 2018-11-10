import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object Post: LongIdTable() {
    val url = varchar("url", 200)
    val group = reference("group", Group, ReferenceOption.CASCADE)
    val tags = text("tags").default("")
}

object Group: LongIdTable() {
    val url = varchar("url", 200)
    val lastRead = datetime("lastRead")
}

object User: LongIdTable() {
    val vkId = long("vkId")
    //List<String>
    val groups = text("groupses")
}

class PostEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PostEntity>(Post)

    var url by Post.url
    var group by GroupEntity referencedOn Post.group
    var tags by Post.group
}

class GroupEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GroupEntity>(Group)

    var lastRead by Group.lastRead
    var url by Group.url
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
