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

package com.example.taskflow.tasks.data.local

import com.example.taskflow.core.database.project.ProjectDao
import com.example.taskflow.core.database.project.ProjectEntity
import com.example.taskflow.core.database.task.TaskDao
import com.example.taskflow.core.database.task.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class LocalTaskDataSource
    @Inject constructor(
        private val taskDao: TaskDao
    ) {

    fun observeTasks(projectId: Long): Flow<List<TaskEntity>> {
        return taskDao.observeTasks(projectId)
    }

    suspend fun saveTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    suspend fun getTask(id: Long): TaskEntity? {
        return taskDao.getTask(id)
    }

    suspend fun insertTasks(tasks: List<TaskEntity>) {
        taskDao.insertTasks(tasks)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteTask(id: Long) {
        taskDao.deleteTaskById(id)
    }

    suspend fun replaceTasks(projectId: Long, tasks: List<TaskEntity>) {
        taskDao.replaceTasksForProject(projectId, tasks)
    }

}