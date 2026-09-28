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

package com.example.taskflow.projects.data.remote

import com.example.taskflow.projects.domain.ProjectError
import com.example.taskflow.projects.domain.ProjectException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

internal class RemoteProjectDataSource

    @Inject constructor(private val projectApi: ProjectApi) {

    suspend fun getProjects(): List<ProjectDto> {
        try {
            return projectApi.getProjects()
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    suspend fun createProject(name: String): ProjectDto {
        try {
            val request = CreateProjectRequest(name)
            return projectApi.createProject(request)
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    suspend fun updateProject(projectId: Long, name: String): ProjectDto {
        try {
            val request = UpdateProjectRequest(name)
            return projectApi.updateProject(projectId, request)
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    suspend fun deleteProject(projectId: Long) {
        try {
            projectApi.deleteProject(projectId)
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    private fun mapException(e: Exception): ProjectException {
        when (e) {
            is HttpException ->
                return ProjectException(
                    error = when (e.code()) {
                        400 -> ProjectError.InvalidRequest
                        404 -> ProjectError.ProjectNotFound
                        in 500..599 -> ProjectError.ServerError(e.code())
                        else -> ProjectError.Unknown
                    },
                    cause = e,
                )
            is SocketTimeoutException ->
                return ProjectException(
                    error = ProjectError.Timeout,
                    cause = e,
                )
            is IOException ->
                return ProjectException(
                    error = ProjectError.NetworkUnavailable,
                    cause = e,
                )
            else ->
                return ProjectException(
                    error = ProjectError.Unknown,
                    cause = e,
                )
        }
    }
}