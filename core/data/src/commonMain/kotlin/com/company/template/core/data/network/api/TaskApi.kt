package com.company.template.core.data.network.api

import com.company.template.core.data.network.UrlConstants
import com.company.template.core.data.network.delete
import com.company.template.core.data.network.get
import com.company.template.core.data.network.model.CreateTaskRequest
import com.company.template.core.data.network.model.TaskDto
import com.company.template.core.data.network.patch
import com.company.template.core.data.network.post
import com.company.template.core.domain.util.DataError
import com.company.template.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.patch

class TaskApi(
    private val httpClient: HttpClient
) {
    suspend fun getTasks(): Result<List<TaskDto>, DataError.Remote> {
        return httpClient.get(route = UrlConstants.Endpoints.TASKS)
    }

    suspend fun getTaskById(id: String): Result<TaskDto, DataError.Remote> {
        return httpClient.get(route = UrlConstants.Endpoints.taskById(id))
    }

    suspend fun createTask(request: CreateTaskRequest): Result<TaskDto, DataError.Remote> {
        return httpClient.post(
            route = UrlConstants.Endpoints.TASKS,
            body = request
        )
    }

    suspend fun deleteTask(id: String): Result<Unit, DataError.Remote> {
        return httpClient.delete(route = UrlConstants.Endpoints.taskById(id))
    }

    suspend fun toggleTask(id: String): Result<TaskDto, DataError.Remote> {
        return httpClient.patch(route = UrlConstants.Endpoints.toggleTask(id))
    }
}
