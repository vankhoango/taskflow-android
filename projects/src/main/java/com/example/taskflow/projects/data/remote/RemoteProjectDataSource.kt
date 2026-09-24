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

import com.example.taskflow.projects.domain.Project
import javax.inject.Inject

internal class RemoteProjectDataSource
    @Inject constructor(private val projectApi: ProjectApi) {

    suspend fun getProjects(): List<ProjectDto> {
        return projectApi.getProjects()
    }

    suspend fun createProject(name: String): ProjectDto {
        val request = CreateProjectRequest(name)
        return projectApi.createProject(request)
    }

    suspend fun updateProject(projectId: Long, name: String): ProjectDto {
        val request = UpdateProjectRequest(name)
        return projectApi.updateProject(projectId, request)
    }

    suspend fun deleteProject(projectId: Long) {
        projectApi.deleteProject(projectId)
    }


}