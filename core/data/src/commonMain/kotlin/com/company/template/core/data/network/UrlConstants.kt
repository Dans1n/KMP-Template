package com.company.template.core.data.network

object UrlConstants {
    const val BASE_URL = "http://10.0.2.2:5001/api/v1"

    object Endpoints {
        const val TASKS = "/tasks"
        fun taskById(id: String) = "/$TASKS/$id"
        fun toggleTask(id: String) = "$TASKS/$id/toggle"
    }
}
