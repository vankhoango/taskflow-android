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

package com.example.taskflow.projects.data.local

import android.database.sqlite.SQLiteException
import com.example.taskflow.core.database.project.ProjectDao
import com.example.taskflow.core.database.project.ProjectEntity
import com.example.taskflow.projects.domain.ProjectError
import com.example.taskflow.projects.domain.ProjectException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class LocalProjectDataSource
@Inject constructor(private val projectDao: ProjectDao)
{
    fun observeProjects(): Flow<List<ProjectEntity>> {
        try {
            return projectDao.observeProjects()
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    suspend fun saveProjects(projects: List<ProjectEntity>,
    ) {
        try {
            projectDao.replaceProjects(projects)
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    suspend fun saveProject(project: ProjectEntity) {
        try {
            projectDao.upsertProject(project)
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    suspend fun deleteProject(projectId: Long) {
        try {
            projectDao.deleteProject(projectId)
        } catch (e: Exception) {
            throw mapException(e)
        }
    }

    private fun mapException(e: Exception): ProjectException {
        when (e) {
            is SQLiteException ->
                return ProjectException(
                    error = ProjectError.Database,
                    cause = e,
                )
            else ->
                return ProjectException(
                    error = ProjectError.Unknown,
                    cause = e)
        }
    }
}