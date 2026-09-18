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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskflow.domain.model.Task

@Composable
fun TaskScreen(
    tasks: List<Task>,
    newTaskTitle: String,
    onNewTaskTitleChanged: (String) -> Unit,
    onTaskClick: (Task) -> Unit,
    onAddTaskClick: () -> Unit,
    onRemove: (Task) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text("TaskFlow")

        val completedCount = tasks.count {
            it.completed
        }
        Text("Compeleted Task: ${completedCount}")

        LazyColumn() {
            items(tasks) {
                TaskItem(it, onClick = {onTaskClick(it)}, onRemove = onRemove)
            }
        }

        OutlinedTextField(
            value = newTaskTitle,
            onValueChange = onNewTaskTitleChanged,
            label = {Text("Task Title")},
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = onAddTaskClick ) { Text("Add Task") }
    }

}

@Composable
fun TaskItem (task: Task, onClick:() -> Unit, onRemove: (Task) -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth().clickable(true, onClick=onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox (
            checked = task.completed,
            onCheckedChange = { onClick() }
        )
        Text(task.title, modifier = Modifier.padding(start = 8.dp, end = 8.dp).weight(1.0f))
        OutlinedButton(
            onClick = {onRemove(task)}
        ) {
            Text("Delete")
        }
    }
}