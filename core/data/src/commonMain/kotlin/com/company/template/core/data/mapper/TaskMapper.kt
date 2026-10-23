package com.company.template.core.data.mapper

import com.company.template.core.data.network.model.TaskDto
import com.company.template.core.domain.model.Task

fun TaskDto.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)
