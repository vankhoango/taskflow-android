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

package com.example.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.data.source.InMemoryTaskDataSource
import com.example.taskflow.data.TaskRepositoryImpl
import com.example.taskflow.domain.usecase.AddTaskUseCase
import com.example.taskflow.domain.usecase.ClearCompletedTaskUseCase
import com.example.taskflow.domain.usecase.DeleteTaskUseCase
import com.example.taskflow.domain.usecase.GetTasksUseCase
import com.example.taskflow.domain.usecase.ToggleTaskUseCase
import com.example.taskflow.ui.TaskScreen
import com.example.taskflow.ui.TaskViewModel
import com.example.taskflow.ui.TaskViewModelFactory
import com.example.taskflow.ui.theme.TaskFlowTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TaskFlowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

private val _dataSource = InMemoryTaskDataSource()
private val _repository = TaskRepositoryImpl(_dataSource)
private val addTaskUseCase = AddTaskUseCase(_repository)
private val getTaskUseCase = GetTasksUseCase(_repository)
private val deleteTaskUseCase = DeleteTaskUseCase(_repository)
private val toggleTaskUseCase = ToggleTaskUseCase(_repository)
private val clearCompletedTaskUseCase = ClearCompletedTaskUseCase(_repository)
private val taskViewModelFactory by lazy {
    TaskViewModelFactory(
        addTaskUseCase = addTaskUseCase,
        getTasksUseCase = getTaskUseCase,
        deleteTasksUseCase = deleteTaskUseCase,
        toggleTaskUseCase = toggleTaskUseCase,
        clearCompletedTaskUseCase = clearCompletedTaskUseCase,
    )
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val taskViewModel: TaskViewModel = viewModel(
        factory = taskViewModelFactory
    )

    TaskScreen(
        taskViewModel.uiState,
        onEvent = taskViewModel::onEvent,
        modifier = modifier
    )
}
