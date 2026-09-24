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

package com.example.taskflow.tasks.data

import com.example.taskflow.tasks.data.local.LocalTaskDataSource
import com.example.taskflow.tasks.data.remote.RemoteTaskDataSource
import com.example.taskflow.tasks.domain.model.Task
import com.example.taskflow.tasks.domain.repository.TaskRepository
import javax.inject.Inject
import com.example.taskflow.tasks.data.mapper.toDomain
import com.example.taskflow.tasks.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class TaskRepositoryImpl
@Inject
constructor(
    private val remoteDataSource: RemoteTaskDataSource,
    private val localDataSource: LocalTaskDataSource
) : TaskRepository {

    override fun observeTasks(projectId: Long): Flow<List<Task>> {
        return localDataSource.observeTasks(projectId)
            .map { entities ->
                entities.map {
                    it.toDomain()
                }
            }
    }

    override suspend fun refreshTasks(projectId: Long) {
        val tasks = remoteDataSource.getTasks(projectId)
        localDataSource.replaceTasks(projectId, tasks.map { it.toEntity() })
    }

    override suspend fun createTask(projectId: Long, title: String) {
        val task = remoteDataSource.createTask(projectId, title)
        localDataSource.saveTask(task.toEntity())
    }

    override suspend fun updateTask(
        taskId: Long,
        title: String?,
        completed: Boolean?
    ) {
        val task = remoteDataSource.updateTask(taskId, title, completed)
        localDataSource.saveTask(task.toEntity())
    }

    override suspend fun deleteTask(taskId: Long) {
        remoteDataSource.deleteTask(taskId)
        localDataSource.deleteTask(taskId)
    }

}