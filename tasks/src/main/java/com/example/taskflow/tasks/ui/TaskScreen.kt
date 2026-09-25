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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskflow.tasks.domain.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskScreen(
    state: TaskUiState,
    onEvent: (TaskUiEvent) -> Unit
) {
    Scaffold(
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
                colors = TopAppBarDefaults.topAppBarColors (
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    ) { innerPadding ->
        Box (modifier = Modifier.padding(innerPadding)) {
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

        LazyColumn() {
            items(state.tasks) {
                TaskItem(it,
                    onClick = {
                        onEvent(TaskUiEvent.TaskClicked(it))
                    },
                    onRemove = {
                        onEvent(TaskUiEvent.DeleteClicked(it))
                    }
                )
            }
        }

        OutlinedTextField(
            value = state.newTaskTitle,
            onValueChange = {
                onEvent(TaskUiEvent.TitleChanged(it))
            },
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            onEvent(TaskUiEvent.AddClicked)
        }) { Text("Add Task") }
    }
}

@Composable
internal fun TaskItem (task: Task, onClick:() -> Unit, onRemove: (Task) -> Unit) {
    Card {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(true, onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onClick() }
            )
            Text(task.title, modifier = Modifier.padding(start = 8.dp, end = 8.dp).weight(1.0f))
            OutlinedButton(
                onClick = { onRemove(task) }
            ) {
                Text("Delete")
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
        onEvent = {},
    )
}