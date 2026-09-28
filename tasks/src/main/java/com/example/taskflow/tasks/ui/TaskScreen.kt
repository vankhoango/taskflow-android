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

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.taskflow.tasks.R
import com.example.taskflow.tasks.domain.TaskError
import com.example.taskflow.tasks.domain.model.Task
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.emptyFlow

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskScreen(
    state: TaskUiState,
    effectFlow: SharedFlow<TaskUiEffect>,
    onEvent: (TaskUiEvent) -> Unit
) {

    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        effectFlow.collect { e ->
            when (e) {
                is TaskUiEffect.ShowError -> {
                    val message = context.getString(e.errorStringResource)
                    snackbar.showSnackbar(message)
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Tasks", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onEvent(TaskUiEvent.BackClicked)
                    }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(TaskUiEvent.AddTaskClicked)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Task"
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            state.errorMessage?.let {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                TaskListView(
                    state,
                    onEvent = onEvent,
                )
            }
        }
    }

    if (state.taskEditorUiState.editing) {
        TaskEditor(
            taskEditorState = state.taskEditorUiState,
            onTextChange = {
                onEvent(TaskUiEvent.TitleChanged(it))
            },
            onSaveClick = {
                onEvent(TaskUiEvent.SaveNewTaskClicked)
            },
            onCancelClick = {
                onEvent(TaskUiEvent.CancelEditClicked)
            }
        )
    }
}

@Composable
internal fun TaskEditor(
    taskEditorState: TaskEditorUiState,
    onTextChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {

    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (taskEditorState.editingMode) stringResource(R.string.edit_task)
                    else stringResource(R.string.new_task),
                )
                OutlinedTextField(
                    value = taskEditorState.editingTaskTitle,
                    onValueChange = onTextChange,
                    label = { Text(stringResource(R.string.task)) }
                )
                if (taskEditorState.savingFailed) {
                    taskEditorState.error?.let {
                        Text(
                            text = stringResource(taskEditorState.error),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onCancelClick) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete"
                            )
                            Text(
                                text = stringResource(R.string.cancel),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }

                    val disabled = taskEditorState.editingTaskTitle.isEmpty()
                            || taskEditorState.isSaving

                    Button(
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = onSaveClick,
                        enabled = !disabled
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (taskEditorState.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Add"
                                )
                            }
                            Text(
                                text = stringResource(R.string.save),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskListView(
    state: TaskUiState,
    onEvent: (TaskUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp)
    ) {
        Text("TaskFlow")

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Compeleted Tasks: ${state.completedCount} / ${state.tasks.size}",
                modifier = Modifier.weight(1.0f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedButton(
                onClick = {
                    onEvent(TaskUiEvent.ClearCompletedClick)
                }
            ) {
                Text("Clear")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.tasks) {
                TaskItem(
                    it,
                    onClick = {
                        onEvent(TaskUiEvent.ToggleTaskClicked(it))
                    },
                    onEdit = { task ->
                        onEvent(TaskUiEvent.EditTaskClicked(task))
                    },
                    onRemove = {
                        onEvent(TaskUiEvent.DeleteClicked(it))
                    }
                )
            }
        }

    }
}

@Composable
internal fun TaskItem(task: Task, onClick: () -> Unit, onEdit: (Task) -> Unit,
onRemove: (Task) -> Unit) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable(true, onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onClick() }
            )
            Text(task.title, modifier = Modifier.weight(1.0f))
            IconButton(onClick = {onEdit(task)}) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit"
                )
            }
            IconButton(onClick = { onRemove(task) }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}

@Composable
@Preview
fun TaskScreenPreview() {
    val state = TaskUiState(
        tasks = listOf(
            Task(1, "Task 1", true),
            Task(2, "This is task 2", false)
        ),
        isLoading = true,
    )
    TaskScreen(
        state = state,
        effectFlow = MutableSharedFlow(),
        onEvent = {},
    )
}