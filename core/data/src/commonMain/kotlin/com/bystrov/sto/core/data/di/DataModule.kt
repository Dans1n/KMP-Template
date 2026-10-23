package com.bystrov.sto.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bystrov.sto.core.data.database.AppDatabase
import com.bystrov.sto.core.data.database.getRoomDatabase
import com.bystrov.sto.core.data.network.HttpClientFactory
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
}
