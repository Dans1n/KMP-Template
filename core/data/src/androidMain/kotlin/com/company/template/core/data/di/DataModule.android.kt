package com.company.template.core.data.di

import android.content.Context
import com.company.template.core.data.database.getDatabaseBuilder
import com.company.template.core.data.datastore.createDataStore
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetDataModule: Module = module {
    single { getDatabaseBuilder(get()) }
    single { createDataStore(get<Context>()) }
    single<HttpClientEngine> { OkHttp.create() }
}
