/*
 *
 * TaskFlow - An Android project demonstrating modern Clean Architecture,
 * Jetpack Compose, and Unidirectional Data Flow.
 *
 * Copyright (C) 2026 Khoa Ngo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.taskflow.tasks.data.remote

import javax.inject.Inject

internal class RemoteTaskDataSource @Inject constructor (
    private val remoteApi: TaskApi
) {
    suspend fun getTasks(projectId: Long): List<TaskDto> {
        return remoteApi.getTasks(projectId)
    }

    suspend fun createTask(projectId: Long, title: String): TaskDto {
        return remoteApi.createTask(projectId, CreateTaskRequest(title))
    }

    suspend fun updateTask(taskId: Long, title: String?, completed: Boolean?): TaskDto {
        val request = UpdateTaskRequest(title, completed)
        return remoteApi.updateTask(taskId, request)
    }

    suspend fun deleteTask(taskId: Long) {
        remoteApi.deleteTask(taskId)
    }
}