package com.bory.kotlin.webflux.r2dbc.repository.log

import com.bory.kotlin.webflux.r2dbc.domain.Log
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface LogRepository : ReactiveCrudRepository<Log, Long>
