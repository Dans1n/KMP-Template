package com.bystrov.sto.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bystrov.sto.core.data.database.AppDatabase
import com.bystrov.sto.core.data.database.getRoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

expect val targetDataModule: Module

val dataModule = module {
    includes(targetDataModule)
    single<AppDatabase> { getRoomDatabase(get()) }
    single<DataStore<Preferences>> { get() }
}
