package com.company.template.core.data.repository

import com.company.template.core.data.mapper.toDomain
import com.company.template.core.data.network.api.TaskApi
import com.company.template.core.data.network.model.CreateTaskRequest
import com.company.template.core.domain.model.Task
import com.company.template.core.domain.repository.TaskRepository
import com.company.template.core.domain.util.DataError
import com.company.template.core.domain.util.Result
import com.company.template.core.domain.util.map

class TaskRepositoryImpl(
    private val taskApi: TaskApi
) : TaskRepository {

    override suspend fun getTasks(): Result<List<Task>, DataError.Remote> {
        return taskApi.getTasks().map { dtoList ->
            dtoList.map { it.toDomain() }
        }
    }

    override suspend fun getTaskById(id: String): Result<Task, DataError.Remote> {
        return taskApi.getTaskById(id).map { it.toDomain() }
    }

    override suspend fun createTask(title: String): Result<Task, DataError.Remote> {
        return taskApi.createTask(CreateTaskRequest(title = title)).map { it.toDomain() }
    }

    override suspend fun deleteTask(id: String): Result<Unit, DataError.Remote> {
        return taskApi.deleteTask(id)
    }

    override suspend fun toggleTask(id: String): Result<Task, DataError.Remote> {
        return taskApi.toggleTask(id).map { it.toDomain() }
    }
}
