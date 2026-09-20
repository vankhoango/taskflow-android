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

package com.example.taskflow.tasks.domain.usecase

import com.example.taskflow.tasks.domain.TaskValidator
import com.example.taskflow.tasks.domain.model.Task
import com.example.taskflow.tasks.domain.repository.TaskRepository
import javax.inject.Inject

class ToggleTaskUseCase
    @Inject
    constructor (private val repository: TaskRepository) {
    operator fun invoke(task: Task): Boolean {
        if (!task.completed && !TaskValidator.canComplete(task)) {
            return false
        }

        val updatedTask = task.copy(completed = !task.completed)
        repository.updateTask(updatedTask)
        return true
    }
}