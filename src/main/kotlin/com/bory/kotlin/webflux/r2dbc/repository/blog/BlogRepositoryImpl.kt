package com.bory.kotlin.webflux.r2dbc.repository.blog

import com.bory.kotlin.webflux.r2dbc.domain.BlogWithAccount
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class BlogRepositoryImpl(private val databaseClient: DatabaseClient) : BlogRepositoryCustom {
  override fun findAllWithJoin(): Flux<BlogWithAccount> =
      databaseClient.sql("""
      SELECT A.id, A.title, A.contents, A.created_at, A.created_by, A.modified_at, A.modified_by,
             B.id creator_id, B.email creator_email, B.name creator_name, 
             C.id modifier_id, C.email modifier_email, C.name modifier_name
        FROM blog A
       INNER JOIN account B on A.created_by = B.id
       INNER JOIN account C on A.modified_by = C.id
       ORDER BY A.id DESC
    """.trimIndent())
          .map { row, _ ->
            BlogWithAccount.fromRow(row)
          }.all()
}
