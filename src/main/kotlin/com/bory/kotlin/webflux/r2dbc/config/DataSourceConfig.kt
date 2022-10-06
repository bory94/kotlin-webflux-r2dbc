package com.bory.kotlin.webflux.r2dbc.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.*
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.init.*
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableR2dbcRepositories
class DataSourceConfig {
  @Bean
  fun mysqlInitializer(@Qualifier("connectionFactory") connectionFactory: ConnectionFactory) =
      ConnectionFactoryInitializer().apply {
        setConnectionFactory(connectionFactory)
        setDatabasePopulator(
            ResourceDatabasePopulator(ClassPathResource("database/schema.sql"), ClassPathResource("database/data.sql"))
        )
      }

  @Bean
  fun transactionManager(@Qualifier("connectionFactory") connectionFactory: ConnectionFactory) =
      R2dbcTransactionManager(connectionFactory)
}

