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

package com.example.taskflow.projects.data

import com.example.taskflow.projects.data.local.LocalProjectDataSource
import com.example.taskflow.projects.data.mapper.toDomain
import com.example.taskflow.projects.data.mapper.toEntity
import com.example.taskflow.projects.data.remote.RemoteProjectDataSource
import com.example.taskflow.projects.domain.Project
import com.example.taskflow.projects.domain.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ProjectRepositoryImpl
@Inject constructor(
    private val localProjectDataSource: LocalProjectDataSource,
    private val remoteProjectDataSource: RemoteProjectDataSource
): ProjectRepository {

    override fun observeProjects(): Flow<List<Project>> {
        return localProjectDataSource.observeProjects()
            .map { projects ->
                projects.map { it.toDomain() }
            }
    }

    override suspend fun refreshProjects() {
        val projects = remoteProjectDataSource.getProjects()
        localProjectDataSource.replaceProjects(projects.map { it.toEntity() })
    }

}