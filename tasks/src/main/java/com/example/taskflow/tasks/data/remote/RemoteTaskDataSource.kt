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

import com.example.taskflow.tasks.domain.TaskError
import com.example.taskflow.tasks.domain.TaskException
import javax.inject.Inject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

internal class RemoteTaskDataSource @Inject constructor (
    private val remoteApi: TaskApi
) {
    suspend fun getTasks(projectId: Long): List<TaskDto> {
        try {
            return remoteApi.getTasks(projectId)
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    suspend fun createTask(projectId: Long, title: String, ): TaskDto {
        try {
            return remoteApi.createTask(
                projectId = projectId,
                request = CreateTaskRequest(title),
            )
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    private fun mapException(e: Exception): TaskException{
        when (e) {
            is HttpException ->
                return TaskException(
                    error = when (e.code()) {
                        400 -> TaskError.InvalidRequest
                        404 -> TaskError.ProjectNotFound
                        in 500..599 -> TaskError.ServerError(e.code())
                        else -> TaskError.Unknown
                    },
                    cause = e,
                )
            is SocketTimeoutException ->
                return TaskException(
                    error = TaskError.Timeout,
                    cause = e,
                )
            is IOException ->
                return TaskException(
                    error = TaskError.NetworkUnavailable,
                    cause = e,
                )
            else ->
                return TaskException(
                    error = TaskError.Unknown,
                    cause = e,
                )
        }
    }

    suspend fun updateTask(taskId: Long, title: String?, completed: Boolean?): TaskDto {
        val request = UpdateTaskRequest(title, completed)
        try {
            return remoteApi.updateTask(taskId, request)
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    suspend fun deleteTask(taskId: Long) {
        try {
            remoteApi.deleteTask(taskId)
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    suspend fun deleteCompletedTasks(projectId: Long) {
        try {
            remoteApi.clearCompletedTasks(projectId)
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

}