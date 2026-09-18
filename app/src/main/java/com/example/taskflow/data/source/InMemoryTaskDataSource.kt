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

package com.example.taskflow.data.source

import com.example.taskflow.domain.model.Task

class InMemoryTaskDataSource {

    private var nextId = 4

    private val tasks = mutableListOf(
        Task(
            id = 1,
            title = "Learn Kotlin",
            completed = true
        ),
        Task(
            id = 2,
            title = "Learn Jetpack Compose"
        ),
        Task(
            id = 3,
            title = "Learn Android Architecture"
        )
    )

    fun getTasks(): List<Task> {
        return tasks.toList()
    }

    fun addTask(task: Task) {
        tasks.add(task)
    }

    fun removeTask(task: Task) {
        tasks.remove(task)
    }

    fun updateTask(task: Task) {
        val index = tasks.indexOfFirst {
            it.id == task.id
        }
        if (index != -1) {
            tasks[index] = task
        }
    }

    fun generateId(): Int {
        return nextId++
    }
}