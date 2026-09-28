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

import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.taskflow.core.database.project.ProjectDao
import com.example.taskflow.core.database.project.ProjectEntity
import com.example.taskflow.core.database.task.TaskDao
import com.example.taskflow.core.database.task.TaskEntity
import com.example.taskflow.tasks.domain.TaskError
import com.example.taskflow.tasks.domain.TaskException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class LocalTaskDataSource
    @Inject constructor(
        private val taskDao: TaskDao
    ) {

    private val TAG = "LocalTaskDataSource"

    fun observeTasks(projectId: Long): Flow<List<TaskEntity>> {
        try {
            return taskDao.observeTasks(projectId)
            //throw SQLiteException("Test")
        } catch (e: Exception) {
            //Log.d("LocalTaskDataSource", "Error observing tasks: ${e.message}")
            throw mapException(e)
            //return flowOf(emptyList())
        }
    }

    suspend fun saveTask(task: TaskEntity) {
        try {
            taskDao.insertTask(task)
        } catch (e: SQLiteException) {
            throw mapException(e)
        }
    }

    suspend fun getTask(id: Long): TaskEntity? {
        try {
            return taskDao.getTask(id)
        } catch (e: SQLiteException) {
            throw mapException(e)
        }
    }

    suspend fun insertTasks(tasks: List<TaskEntity>) {
        try {
            taskDao.insertTasks(tasks)
        } catch (e: SQLiteException) {
            throw mapException(e)
        }
    }

    suspend fun updateTask(task: TaskEntity) {
        try {
            taskDao.updateTask(task)
        } catch (e: SQLiteException) {
            throw mapException(e)
        }
    }

    suspend fun deleteTask(task: TaskEntity) {
        try {
            taskDao.deleteTask(task)
        } catch (e: SQLiteException) {
            throw mapException(e)
        }
    }

    suspend fun deleteTask(id: Long) {
        try {
            Log.d(TAG, "Deleting task with id: ${id}")
            taskDao.deleteTaskById(id)
        } catch (e: SQLiteException) {
            throw mapException(e)
        }
    }

    suspend fun replaceTasks(projectId: Long, tasks: List<TaskEntity>) {
        try {
            Log.d(TAG, "Replacing tasks for project with id: ${projectId}")
            taskDao.replaceTasksForProject(projectId, tasks)
        } catch (e: SQLiteException) {
            throw mapException(e)
        }
    }

    private fun mapException(e: Exception): TaskException {
        when (e) {
            is SQLiteException ->
                return TaskException(
                    error = TaskError.Database,
                    cause = e,
                )
            else ->
                return TaskException(
                error = TaskError.Unknown,
                cause = e)
        }
    }
}