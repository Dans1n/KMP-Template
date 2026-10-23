package com.company.template.core.domain.model

data class Task(
    val id: Long,
    val title: String,
    val description: String?,
    val isCompleted: Boolean
)
