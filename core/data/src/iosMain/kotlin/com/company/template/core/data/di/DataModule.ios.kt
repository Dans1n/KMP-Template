package com.company.template.core.data.di

import com.company.template.core.data.database.getDatabaseBuilder
import com.company.template.core.data.datastore.createDataStore
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetDataModule: Module = module {
    single { getDatabaseBuilder() }
    single { createDataStore() }
    single<HttpClientEngine> { Darwin.create() }
}
