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

import com.example.taskflow.tasks.domain.model.Task

internal sealed interface TaskUiEvent {
    data class ToggleTaskClicked(val task: Task): TaskUiEvent
    data object AddTaskClicked: TaskUiEvent
    data class EditTaskClicked (val task: Task): TaskUiEvent
    data object SaveNewTaskClicked: TaskUiEvent
    data class TitleChanged(val newTitle: String): TaskUiEvent
    data object CancelEditClicked: TaskUiEvent
    data class DeleteClicked(val task: Task): TaskUiEvent
    data object ClearCompletedClick: TaskUiEvent
    data object BackClicked: TaskUiEvent

}