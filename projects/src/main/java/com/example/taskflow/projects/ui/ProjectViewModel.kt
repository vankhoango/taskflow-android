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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow.projects.R
import com.example.taskflow.projects.domain.Project
import com.example.taskflow.projects.domain.ProjectError
import com.example.taskflow.projects.domain.ProjectException
import com.example.taskflow.projects.domain.usecase.CreateProjectUseCase
import com.example.taskflow.projects.domain.usecase.DeleteProjectUseCase
import com.example.taskflow.projects.domain.usecase.ObserveProjectsUseCase
import com.example.taskflow.projects.domain.usecase.RefreshProjectsUseCase
import com.example.taskflow.projects.domain.usecase.UpdateProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val observeProjectsUseCase: ObserveProjectsUseCase,
    private val refreshProjectsUseCase: RefreshProjectsUseCase,
    private val createProjectUseCase: CreateProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
) : ViewModel() {

    private val TAG = "ProjectViewModel"
    private var _uiState = MutableStateFlow(ProjectUiState())
    val uiState: StateFlow<ProjectUiState> = _uiState.asStateFlow()

    private var _uiEffect = MutableSharedFlow<ProjectUiEffect>(replay = 1)
    var uiEffect = _uiEffect.asSharedFlow()

    lateinit var onProjectDetailsClicked: (Long) -> Unit

    init {
        observeProjects()
        refreshProject()
    }

    fun onEvent(event: ProjectUiEvent) {
        when (event) {
            ProjectUiEvent.AddProjectClicked -> {
                startCreatingProject()
            }
            ProjectUiEvent.SaveProjectClicked -> {
                saveProject()
            }
            is ProjectUiEvent.ProjectDetailsClicked -> {
                onProjectDetailsClicked(event.projectId)
            }
            is ProjectUiEvent.EditProjectClicked -> {
                startEditingProject(event.project)
            }
            is ProjectUiEvent.ProjectNameChanged -> {
                changeProjectName(event.name)
            }
            ProjectUiEvent.CancelEditClicked -> {
                cancelEditingProject()
            }

            is ProjectUiEvent.DeleteProjectClicked -> {
                requestDeleteProject(event.projectId)
            }
            ProjectUiEvent.CancelDeleteClicked -> {
                cancelDeleteProject()
            }
            ProjectUiEvent.ConfirmDeleteClicked -> {
                confirmDeleteProject()
            }
        }
    }

    private fun observeProjects() {
        viewModelScope.launch {
            try {
                observeProjectsUseCase().collect { projects ->
                    _uiState.update {
                        it.copy(
                            projects = projects,
                            isLoading = false,
                        )
                    }
                }
            } catch (e: ProjectException) {
                val errorStringResource = e.error.toStringResource()
                Log.e(TAG, "Error while observing project", e.cause)
                _uiEffect.emit(ProjectUiEffect.ShowError(errorStringResource))
            }
        }
    }

    private fun refreshProject() {
        viewModelScope.launch {
            try {
                refreshProjectsUseCase()
            } catch (e: ProjectException) {
                val errorStringResource = e.error.toStringResource()
                Log.e(TAG, "Error while refreshing project", e.cause)
                _uiEffect.emit(ProjectUiEffect.ShowError(errorStringResource))
            }
        }
    }

    private fun startCreatingProject() {
        _uiState.update {
            it.copy(
                showEditor = true,
                editingProjectId = null,
                projectName = "",
            )
        }
    }

    private fun startEditingProject(
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

    private fun changeProjectName(
        name: String,
    ) {
        _uiState.update {
            it.copy(
                projectName = name,
            )
        }
    }

    private fun cancelEditingProject() {
        _uiState.update {
            it.copy(
                showEditor = false,
                editingProjectId = null,
                projectName = "",
            )
        }
    }

    private fun saveProject() {
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
                _uiEffect.emit(ProjectUiEffect.ShowMessage(R.string.project_saved))
                cancelEditingProject()
            } catch (e: ProjectException) {
                val errorStringResource = e.error.toStringResource()
                Log.e(TAG, "Error while saving project", e.cause)
                _uiEffect.emit(ProjectUiEffect.ShowError(errorStringResource))
            }
        }
    }

    private fun requestDeleteProject(
        projectId: Long,
    ) {
        _uiState.update {
            it.copy(
                deletingProjectId = projectId,
            )
        }
    }

    private fun cancelDeleteProject() {
        _uiState.update {
            it.copy(
                deletingProjectId = null,
            )
        }
    }

    private fun confirmDeleteProject() {
        val projectId =
            uiState.value.deletingProjectId ?: return

        viewModelScope.launch {
            try {
                deleteProjectUseCase(projectId)
                _uiState.update {
                    it.copy(
                        deletingProjectId = null,
                    )
                }
                _uiEffect.emit(ProjectUiEffect.ShowMessage(R.string.project_deleted))
            } catch (e: ProjectException) {
                val errorStringResource = e.error.toStringResource()
                Log.e(TAG, "Error while deleting project", e.cause)
                _uiEffect.emit(ProjectUiEffect.ShowError(errorStringResource))
            }
        }
    }

    private fun ProjectError.toStringResource() =
        when (this) {
            ProjectError.InvalidProject -> R.string.error_invalid_name
            ProjectError.NetworkUnavailable -> R.string.error_network
            ProjectError.Timeout -> R.string.error_timeout
            ProjectError.ProjectNotFound -> R.string.error_project_not_found
            is ProjectError.ServerError -> R.string.error_server
            ProjectError.Database -> R.string.error_database
            else -> R.string.error_unknown
        }
}