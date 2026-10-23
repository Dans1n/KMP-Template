package com.company.template.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: Long,
    val title: String,
    val description: String?,
    val isCompleted: Boolean = false,
    val createdAt: String
)

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null
)
