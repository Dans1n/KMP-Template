package com.company.template.feature.example

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val exampleModule = module {
    viewModelOf(::ExampleViewModel)
}
