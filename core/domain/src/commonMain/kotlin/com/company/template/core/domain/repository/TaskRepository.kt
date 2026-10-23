package com.company.template.core.domain.repository

import com.company.template.core.domain.model.Task
import com.company.template.core.domain.util.DataError
import com.company.template.core.domain.util.Result

interface TaskRepository {
    suspend fun getTasks(): Result<List<Task>, DataError.Remote>
    suspend fun getTaskById(id: String): Result<Task, DataError.Remote>
    suspend fun createTask(title: String): Result<Task, DataError.Remote>
    suspend fun deleteTask(id: String): Result<Unit, DataError.Remote>
    suspend fun toggleTask(id: String): Result<Task, DataError.Remote>
}
