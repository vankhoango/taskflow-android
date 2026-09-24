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
import androidx.lifecycle.viewModelScope
import com.example.taskflow.tasks.domain.model.Task
import com.example.taskflow.tasks.domain.usecase.AddTaskUseCase
import com.example.taskflow.tasks.domain.usecase.ClearCompletedTaskUseCase
import com.example.taskflow.tasks.domain.usecase.DeleteTaskUseCase
import com.example.taskflow.tasks.domain.usecase.ObserveTasksUseCase
import com.example.taskflow.tasks.domain.usecase.RefreshTasksUseCase
import com.example.taskflow.tasks.domain.usecase.ToggleTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TaskViewModel @Inject constructor (
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTasksUseCase: DeleteTaskUseCase,
    private val toggleTaskUseCase: ToggleTaskUseCase,
    private val clearCompletedTaskUseCase: ClearCompletedTaskUseCase,
    private val refreshTasksUseCase: RefreshTasksUseCase,
): ViewModel() {

    val projectId = 1L;

    var uiState by mutableStateOf(TaskUiState())
        private set

    private fun onTaskTitleChange(title: String) {
        uiState = uiState.copy(newTaskTitle = title)
    }

    private fun observeTasks() {
        viewModelScope.launch {
            observeTasksUseCase(projectId).collect { tasks ->
                uiState = uiState.copy(
                    tasks = tasks,
                    isLoading = false,
                )
            }
        }
    }

    init {
        observeTasks()

        viewModelScope.launch {
            try {
                refreshTasksUseCase(projectId)
            } catch (e: Exception) {
                // Cached tasks remain visible.
            }
        }
    }

    private fun addTask() {
        viewModelScope.launch {
            val success = addTaskUseCase(projectId, uiState.newTaskTitle)
            if (success) {
                uiState = uiState.copy(
                    newTaskTitle = "",
                    errorMessage = null,
                )
            } else {
                uiState = uiState.copy(
                    errorMessage = "Task title must contain 3-100 characters",
                )
            }
        }
    }

    private fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTasksUseCase(task.id.toLong())
        }
    }

    private fun toggleTask(task: Task) {
        viewModelScope.launch {
            val success = toggleTaskUseCase(task)
        }
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
        viewModelScope.launch {
            clearCompletedTaskUseCase()
        }
    }

}