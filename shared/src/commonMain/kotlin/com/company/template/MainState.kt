package com.company.template

import com.company.template.core.domain.model.Task

data class MainState(
    val isLoggedIn: Boolean = false,
    val tasks: List<Task> = emptyList()
)
