package com.company.template.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.company.template.core.data.database.AppDatabase
import com.company.template.core.data.database.getRoomDatabase
import com.company.template.core.data.network.HttpClientFactory
import com.company.template.core.data.network.api.TaskApi
import com.company.template.core.data.repository.TaskRepositoryImpl
import com.company.template.core.domain.repository.TaskRepository
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

expect val targetDataModule: Module

val dataModule = module {
    includes(targetDataModule)
    single<AppDatabase> { getRoomDatabase(get()) }
    single<DataStore<Preferences>> { get() }
    single { HttpClientFactory() }
    single<HttpClient> { get<HttpClientFactory>().create(engine = get()) }
    single { TaskApi(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
}
