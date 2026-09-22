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

import com.example.taskflow.tasks.data.remote.RemoteTaskDataSource
import com.example.taskflow.tasks.data.source.InMemoryTaskDataSource
import com.example.taskflow.tasks.domain.model.Task
import com.example.taskflow.tasks.domain.repository.TaskRepository
import javax.inject.Inject
import com.example.taskflow.tasks.data.mapper.toDomain

internal class TaskRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: RemoteTaskDataSource,
        private val inMemoryDataSource: InMemoryTaskDataSource
    ) : TaskRepository {

    override suspend fun getTasks(): List<Task> {
        return remoteDataSource
            .getTasks()
            .map {
                it.toDomain()
            }
    }

    override fun addTask(title: String) {
        /*
        val task = Task(
            title = title,
            id = dataSource.generateId()
        )
        return dataSource.addTask(task)
         */
    }

    override fun updateTask(task: Task) {
        //return dataSource.updateTask(task)
    }

    override fun deleteTask(task: Task) {
        //return dataSource.removeTask(task)
    }
}