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

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow.projects.domain.Project
import com.example.taskflow.projects.domain.usecase.CreateProjectUseCase
import com.example.taskflow.projects.domain.usecase.DeleteProjectUseCase
import com.example.taskflow.projects.domain.usecase.ObserveProjectsUseCase
import com.example.taskflow.projects.domain.usecase.RefreshProjectsUseCase
import com.example.taskflow.projects.domain.usecase.UpdateProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val observeProjectsUseCase: ObserveProjectsUseCase,
    private val refreshProjectsUseCase: RefreshProjectsUseCase,
    private val createProjectUseCase: CreateProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow(ProjectUiState())
    val uiState: StateFlow<ProjectUiState> = _uiState.asStateFlow()

    init {
        observeProjects()

        viewModelScope.launch {
            try {
                refreshProjectsUseCase()
            } catch (e: Exception) {
                // Proper network error handling comes later.
            }
        }
    }

    private fun observeProjects() {
        viewModelScope.launch {
            observeProjectsUseCase().collect { projects ->
                val selectedProjectId = uiState.value.selectedProjectId
                    ?.takeIf { selectedId ->
                        projects.any { it.id == selectedId }
                    }
                    ?: projects.firstOrNull()?.id

                _uiState.update {
                    it.copy(
                        projects = projects,
                        isLoading = false,
                        selectedProjectId = selectedProjectId,
                    )
                }
            }
        }
    }

    fun selectProject(
        projectId: Long,
    ) {
        _uiState.update {
            it.copy(
                selectedProjectId = projectId,
            )
        }
    }

    fun startCreatingProject() {
        _uiState.update {
            it.copy(
                showEditor = true,
                editingProjectId = null,
                projectName = "",
            )
        }
    }

    fun startEditingProject(
        project: Project,
    ) {
        _uiState.update {
            it.copy(
                showEditor = true,
                editingProjectId = project.id,
                projectName = project.name,
            )
        }
    }

    fun changeProjectName(
        name: String,
    ) {
        _uiState.update {
            it.copy(
                projectName = name,
            )
        }
    }

    fun cancelEditingProject() {
        _uiState.update {
            it.copy(
                showEditor = false,
                editingProjectId = null,
                projectName = "",
            )
        }
    }

    fun saveProject() {
        val name = uiState.value.projectName.trim()

        if (name.isEmpty()) {
            return
        }

        val editingProjectId =
            uiState.value.editingProjectId

        viewModelScope.launch {
            try {
                if (editingProjectId == null) {
                    createProjectUseCase(name)
                } else {
                    updateProjectUseCase(
                        projectId = editingProjectId,
                        name = name,
                    )
                }

                cancelEditingProject()
            } catch (e: Exception) {
                Log.e("Projects", "Error while saving project", e)
            }
        }
    }

    fun requestDeleteProject(
        projectId: Long,
    ) {
        _uiState.update {
            it.copy(
                deletingProjectId = projectId,
            )
        }
    }

    fun cancelDeleteProject() {
        _uiState.update {
            it.copy(
                deletingProjectId = null,
            )
        }
    }

    fun confirmDeleteProject() {
        val projectId =
            uiState.value.deletingProjectId ?: return

        viewModelScope.launch {
            try {
                deleteProjectUseCase(projectId)

                _uiState.update {
                    it.copy(
                        selectedProjectId = null,
                    )
                }
            } catch (e: Exception) {
                // Error handling later.
            }
        }
    }
}