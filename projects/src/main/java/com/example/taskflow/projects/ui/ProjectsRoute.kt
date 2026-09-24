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

package com.example.taskflow.projects.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProjectsRoute(
    onProjectClick: (Long) -> Unit
) {

    val viewModel: ProjectViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProjectsScreen(
        uiState = uiState,
        onProjectClick = onProjectClick,
        onAddClick = viewModel::startCreatingProject,
        onEditClick = viewModel::startEditingProject,
        onDeleteClick = viewModel::requestDeleteProject,
        onNameChange = viewModel::changeProjectName,
        onSave = viewModel::saveProject,
        onCancelEditor = viewModel::cancelEditingProject,
        onConfirmDelete = viewModel::confirmDeleteProject,
        onCancelDelete = viewModel::cancelDeleteProject,
    )
}