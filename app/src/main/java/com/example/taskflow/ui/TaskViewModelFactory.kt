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

package com.example.taskflow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskflow.domain.usecase.AddTaskUseCase
import com.example.taskflow.domain.usecase.ClearCompletedTaskUseCase
import com.example.taskflow.domain.usecase.DeleteTaskUseCase
import com.example.taskflow.domain.usecase.GetTasksUseCase
import com.example.taskflow.domain.usecase.ToggleTaskUseCase

class TaskViewModelFactory(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTasksUseCase: DeleteTaskUseCase,
    private val toggleTaskUseCase: ToggleTaskUseCase,
    private val clearCompletedTaskUseCase: ClearCompletedTaskUseCase,
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel (
                getTasksUseCase = getTasksUseCase,
                addTaskUseCase = addTaskUseCase,
                deleteTasksUseCase = deleteTasksUseCase,
                toggleTaskUseCase = toggleTaskUseCase,
                clearCompletedTaskUseCase = clearCompletedTaskUseCase,
            ) as T
        }
        throw IllegalArgumentException("Unknown Model Class")
    }
}