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

package com.example.taskflow.tasks.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.taskflow.tasks.domain.model.Task
import com.example.taskflow.tasks.domain.usecase.AddTaskUseCase
import com.example.taskflow.tasks.domain.usecase.ClearCompletedTaskUseCase
import com.example.taskflow.tasks.domain.usecase.DeleteTaskUseCase
import com.example.taskflow.tasks.domain.usecase.GetTasksUseCase
import com.example.taskflow.tasks.domain.usecase.ToggleTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class TaskViewModel @Inject constructor (
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTasksUseCase: DeleteTaskUseCase,
    private val toggleTaskUseCase: ToggleTaskUseCase,
    private val clearCompletedTaskUseCase: ClearCompletedTaskUseCase
): ViewModel() {

    var uiState by mutableStateOf(TaskUiState())
        private set

    private fun onTaskTitleChange(title: String) {
        uiState = uiState.copy(newTaskTitle = title)
    }

    private fun addTask() {
        val success = addTaskUseCase(uiState.newTaskTitle)
        if (success) {
            uiState = uiState.copy(
                newTaskTitle = "",
                errorMessage = null
            )
            loadTasks()
        } else {
            uiState = uiState.copy(
                errorMessage = "Task title must contain 3-100 characters"
            )
        }
    }

    private fun deleteTask(task: Task) {
        deleteTasksUseCase(task)
        loadTasks()
    }

    private fun toggleTask(task: Task) {
        val success = toggleTaskUseCase(task)
        if (success) {
            loadTasks()
        }
    }

    init {
        loadTasks()
    }

    private fun loadTasks() {
        uiState = uiState.copy(
            isLoading = true
        )
        val tasks = getTasksUseCase()
        uiState = uiState.copy(
            tasks = tasks,
            isLoading = false,
        )
    }

    fun onEvent(event: TaskUiEvent) {
        when(event) {
            TaskUiEvent.AddClicked -> {
                addTask()
            }
            is TaskUiEvent.DeleteClicked -> {
                deleteTask(event.task)
            }
            is TaskUiEvent.TaskClicked -> {
                toggleTask(event.task)
            }
            is TaskUiEvent.TitleChanged -> {
                onTaskTitleChange(event.newTitle)
            }
            TaskUiEvent.ClearCompletedClick -> {
                clearCompletedTasks()
            }
        }
    }

    private fun clearCompletedTasks() {
        clearCompletedTaskUseCase()
        loadTasks()
    }
}