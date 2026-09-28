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

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle
import com.example.taskflow.tasks.R
import com.example.taskflow.tasks.domain.TaskError
import com.example.taskflow.tasks.domain.TaskException
import com.example.taskflow.tasks.domain.usecase.SaveTaskUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest

@HiltViewModel
internal class TaskViewModel @Inject constructor (
    savedStateHandle: SavedStateHandle,
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val deleteTasksUseCase: DeleteTaskUseCase,
    private val toggleTaskUseCase: ToggleTaskUseCase,
    private val clearCompletedTaskUseCase: ClearCompletedTaskUseCase,
    private val refreshTasksUseCase: RefreshTasksUseCase,
): ViewModel() {

    private val TAG = "TaskViewModel"

    private val projectId: Long =
        checkNotNull(
            savedStateHandle["projectId"]
        )

    private var _uiState = MutableStateFlow(TaskUiState())
    val uiState = _uiState.asStateFlow()

    private var _uiEffect = MutableSharedFlow<TaskUiEffect>(replay = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        observeTasks()
        refreshTasks()
    }

    private fun observeTasks() {
        Log.d(TAG, "Observing tasks for projectId: ${projectId}")
        viewModelScope.launch {
            try {
                observeTasksUseCase(projectId)
                    .collect { tasks ->
                        _uiState.update {
                            it.copy(
                                tasks = tasks,
                                isLoading = false,
                            )
                        }
                    }
            } catch (ex: Exception) {
                if (ex is TaskException) {
                    val errorStringResource = ex.error.toStringResource()
                    Log.d(TAG, "Error observing tasks: ${errorStringResource}")
                    _uiEffect.emit(TaskUiEffect.ShowError(errorStringResource))
                } else {
                    throw ex
                }
            }
        }
    }

    private fun refreshTasks() {
        viewModelScope.launch {
            try {
                refreshTasksUseCase(projectId)
            } catch (e: Exception) {
                // Cached tasks remain visible.
            }
        }
    }

    private fun onTaskTitleChange(title: String) {
        _uiState.update {
            it.copy(taskEditorUiState = it.taskEditorUiState.copy(editingTaskTitle = title))
        }
    }

    private fun saveTask() {
        if (uiState.value.taskEditorUiState.editingMode) {
            saveEditTask()
        } else {
            addTask()
        }
    }

    private fun saveEditTask() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    taskEditorUiState = it.taskEditorUiState.copy(
                        isSaving = true,
                        error = null,
                        savingFailed = false,
                    )
                )
            }
            uiState.value.taskEditorUiState.editingTask?.let {
                try {
                    saveTaskUseCase(it, uiState.value.taskEditorUiState.editingTaskTitle)
                    _uiState.update {
                        it.copy(
                            taskEditorUiState = it.taskEditorUiState.copy(
                                editing = false,
                                editingTaskTitle = "",
                                isSaving = false,
                                savingFailed = false,
                                error = null,
                            )
                        )
                    }
                } catch (e: TaskException) {
                    _uiState.update {
                        it.copy(
                            taskEditorUiState = it.taskEditorUiState.copy(
                                isSaving = false,
                                savingFailed = true,
                                error = e.error.toStringResource(),
                            )
                        )
                    }
                }
            }
        }
    }

    private fun addTask() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    taskEditorUiState = it.taskEditorUiState.copy(
                        isSaving = true,
                        error = null,
                        savingFailed = false,
                    )
                )
            }
            try {
                addTaskUseCase(projectId, uiState.value.taskEditorUiState.editingTaskTitle)
                _uiState.update {
                    it.copy(
                        taskEditorUiState = it.taskEditorUiState.copy(
                            editing = false,
                            editingTaskTitle = "",
                            isSaving = false,
                            savingFailed = false,
                            error = null,
                        )
                    )
                }
            } catch (e: TaskException) {
                _uiState.update {
                    it.copy(
                        taskEditorUiState = it.taskEditorUiState.copy(
                            isSaving = false,
                            savingFailed = true,
                            error = e.error.toStringResource(),
                        )
                    )
                }
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

    private fun startAddNewTask() {
        _uiState.update {
            it.copy(taskEditorUiState = it.taskEditorUiState.copy(
                editing = true,
            ))
        }
    }

    private fun startEditingTask(task: Task) {
        _uiState.update {
            it.copy(taskEditorUiState = it.taskEditorUiState.copy(
                editing = true,
                editingMode = true,
                editingTaskTitle = task.title,
                editingTask = task,
            ))
        }
    }
    private fun cancelEditingTask() {
        _uiState.update {
            it.copy(taskEditorUiState = TaskEditorUiState())
        }
    }

    fun onEvent(event: TaskUiEvent) {
        when(event) {
            is TaskUiEvent.ToggleTaskClicked -> {
                toggleTask(event.task)
            }

            TaskUiEvent.AddTaskClicked -> {
                startAddNewTask()
            }
            TaskUiEvent.CancelEditClicked -> {
                cancelEditingTask()
            }
            TaskUiEvent.SaveNewTaskClicked -> {
                saveTask()
            }
            is TaskUiEvent.EditTaskClicked -> {
                startEditingTask(event.task)
            }

            is TaskUiEvent.DeleteClicked -> {
                deleteTask(event.task)
            }

            is TaskUiEvent.TitleChanged -> {
                onTaskTitleChange(event.newTitle)
            }
            TaskUiEvent.ClearCompletedClick -> {
                clearCompletedTasks()
            }
            else -> {}
        }
    }

    private fun clearCompletedTasks() {
        viewModelScope.launch {
            clearCompletedTaskUseCase()
        }
    }

    private fun TaskError.toStringResource() =
        when (this) {
            TaskError.InvalidTask -> R.string.error_invalid_title
            TaskError.NetworkUnavailable -> R.string.error_network
            TaskError.Timeout -> R.string.error_timeout
            TaskError.ProjectNotFound -> R.string.error_project_not_found
            is TaskError.ServerError -> R.string.error_server
            TaskError.Database -> R.string.error_database
            else -> R.string.error_unknown
        }
}