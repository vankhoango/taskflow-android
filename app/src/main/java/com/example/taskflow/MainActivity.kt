/*
 *
 *  * TaskFlow - An Android project demonstrating modern Clean Architecture,
 *  * Jetpack Compose, and Unidirectional Data Flow.
 *  *
 *  * Copyright (C) 2026 Khoa Ngo
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.taskflow.data.source.InMemoryTaskDataSource
import com.example.taskflow.domain.TaskValidator
import com.example.taskflow.domain.model.Task
import com.example.taskflow.ui.TaskScreen
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

private val dataSource = InMemoryTaskDataSource()
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    var tasks by remember {
        mutableStateOf(
            dataSource.getTasks()
        )
    }

    var newTaskTitle by remember {
        mutableStateOf("")
    }

    TaskScreen(
        tasks,

        onTaskClick = {
            val task = it.copy(completed = !it.completed)
            dataSource.updateTask(task)
            tasks = dataSource.getTasks()
        },

        onAddTaskClick = {
            if (TaskValidator.isValidTitle(newTaskTitle)) {
                val task = Task(
                    id = tasks.size + 1,
                    title = newTaskTitle,
                )
                dataSource.addTask(task)
                tasks = dataSource.getTasks()
                newTaskTitle = ""
            }
        },

        newTaskTitle = newTaskTitle,
        onNewTaskTitleChanged = {
            newTaskTitle = it
        },
        onRemove = {
            dataSource.removeTask(it)
            tasks = dataSource.getTasks()
        }
    )
}
