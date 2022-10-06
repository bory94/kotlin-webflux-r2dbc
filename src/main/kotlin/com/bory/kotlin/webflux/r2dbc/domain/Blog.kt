package com.bory.kotlin.webflux.r2dbc.domain

import io.r2dbc.spi.Row
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import javax.validation.constraints.*

@Table("blog")
data class Blog(
    @Id
    var id: Long? = null,
    @field:NotEmpty(message = "blog.title.empty")
    @field:Size(min = 5, max = 400, message = "blog.title.size")
    var title: String?,
    @field:NotEmpty(message = "blog.contents.empty")
    @field:Size(min = 10, max = 4000, message = "blog.contents.size")
    var contents: String?,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var createdBy: Long?,
    var modifiedAt: LocalDateTime = LocalDateTime.now(),
    var modifiedBy: Long?
) {

  fun copyFrom(source: Blog): Blog {
    title = source.title
    contents = source.contents
    createdBy = source.createdBy
    modifiedAt = LocalDateTime.now()

    return this
  }
}

data class BlogWithAccount(
    var id: Long?,
    var title: String?,
    var contents: String?,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var createdBy: Account?,
    var modifiedAt: LocalDateTime = LocalDateTime.now(),
    var modifiedBy: Account?
) {
  companion object {
    fun of(blog: Blog, createdBy: Account?, modifiedBy: Account?) =
        BlogWithAccount(blog.id, blog.title, blog.contents, blog.createdAt, createdBy, blog.modifiedAt, modifiedBy)

    fun fromRow(row: Row) = BlogWithAccount(
        id = row["id"] as Long,
        title = row["title"] as String,
        contents = row["contents"] as String,
        createdAt = row["created_at"] as LocalDateTime,
        modifiedAt = row["modified_at"] as LocalDateTime,
        createdBy = Account(
            id = row["creator_id"] as Long,
            email = row["creator_email"] as String,
            name = row["creator_name"] as String,
            password = null
        ),
        modifiedBy = Account(
            id = row["modifier_id"] as Long,
            email = row["modifier_email"] as String,
            name = row["modifier_name"] as String,
            password = null
        )
    )

  }
}
