package com.bystrov.sto.core.data.di

import com.bystrov.sto.core.data.database.getDatabaseBuilder
import com.bystrov.sto.core.data.datastore.createDataStore
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetDataModule: Module = module {
    single { getDatabaseBuilder() }
    single { createDataStore() }
    single<HttpClientEngine> { Darwin.create() }
}
