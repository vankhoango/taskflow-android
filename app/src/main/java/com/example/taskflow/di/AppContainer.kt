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

package com.example.taskflow.di

import com.example.taskflow.data.TaskRepositoryImpl
import com.example.taskflow.data.source.InMemoryTaskDataSource
import com.example.taskflow.domain.repository.TaskRepository
import com.example.taskflow.domain.usecase.AddTaskUseCase
import com.example.taskflow.domain.usecase.ClearCompletedTaskUseCase
import com.example.taskflow.domain.usecase.DeleteTaskUseCase
import com.example.taskflow.domain.usecase.GetTasksUseCase
import com.example.taskflow.domain.usecase.ToggleTaskUseCase
import com.example.taskflow.ui.TaskViewModelFactory

class AppContainer {
    private val dataSource by lazy {  InMemoryTaskDataSource() }

    private val taskRepository: TaskRepository by lazy { TaskRepositoryImpl(dataSource) }

    val getTasksUseCase by lazy { GetTasksUseCase(taskRepository) }

    val addTaskUseCase by lazy { AddTaskUseCase(taskRepository) }

    val toggleTaskUseCase by lazy { ToggleTaskUseCase(taskRepository) }

    val deleteTaskUseCase by lazy { DeleteTaskUseCase(taskRepository) }

    val clearCompletedTaskUseCase by lazy { ClearCompletedTaskUseCase(taskRepository) }

    val taskViewModelFactory
        get() = TaskViewModelFactory(
            addTaskUseCase = addTaskUseCase,
            clearCompletedTaskUseCase = clearCompletedTaskUseCase,
            deleteTasksUseCase = deleteTaskUseCase,
            getTasksUseCase = getTasksUseCase,
            toggleTaskUseCase = toggleTaskUseCase,
        )

}