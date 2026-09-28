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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taskflow.core.network.R
import com.example.taskflow.projects.R.string.add_project
import com.example.taskflow.projects.R.string.cancel
import com.example.taskflow.projects.R.string.delete
import com.example.taskflow.projects.R.string.delete_project_confirm
import com.example.taskflow.projects.R.string.delete_project_confirm_details
import com.example.taskflow.projects.R.string.edit
import com.example.taskflow.projects.R.string.edit_project
import com.example.taskflow.projects.R.string.name
import com.example.taskflow.projects.R.string.projects
import com.example.taskflow.projects.R.string.save
import com.example.taskflow.projects.domain.Project

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    uiState: ProjectUiState,
    onEvent: (ProjectUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar (
                title = {
                    Text(stringResource(projects),
                        style = MaterialTheme.typography.titleLarge)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent (ProjectUiEvent.AddProjectClicked) },
                shape = CircleShape,
            ) {
                Icon(Icons.Filled.Add, stringResource(add_project))
            }
        }
    ) { innerPadding ->
        LazyColumn (
            modifier = modifier
                .padding(innerPadding)
                .padding(12.dp),
            contentPadding = PaddingValues(
                bottom = 56.dp,
            ),
        ) {
            uiState.projects.forEach { project ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEvent(ProjectUiEvent.ProjectDetailsClicked(project.id))
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = project.name,
                            modifier = Modifier.weight(1f),
                        )

                        IconButton(
                            onClick = {
                                onEvent(ProjectUiEvent.EditProjectClicked(project))
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = stringResource(edit)
                            )
                        }

                        IconButton(
                            onClick = {
                                onEvent(ProjectUiEvent.DeleteProjectClicked(project.id))
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(delete)
                            )
                        }
                    }
                }
            }
        }

        //Add/Edit dialog
        if (uiState.showEditor) {
            AlertDialog(
                onDismissRequest = {onEvent(ProjectUiEvent.CancelEditClicked)},
                title = {
                    Text(
                        if (uiState.editingProjectId == null) {
                            stringResource(add_project)
                        } else {
                            stringResource(edit_project)
                        }
                    )
                },
                text = {
                    OutlinedTextField(
                        value = uiState.projectName,
                        onValueChange = {onEvent(ProjectUiEvent.ProjectNameChanged(it))},
                        label = {
                            Text(stringResource(name))
                        },
                        singleLine = true,
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {onEvent(ProjectUiEvent.SaveProjectClicked)},
                        enabled = uiState.projectName.isNotBlank(),
                    ) {
                        Text(stringResource(save))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {onEvent(ProjectUiEvent.CancelEditClicked)},
                    ) {
                        Text(stringResource(cancel))
                    }
                },
            )
        }

        //Delete dialog
        if (uiState.deletingProjectId != null) {
            AlertDialog(
                onDismissRequest = {onEvent(ProjectUiEvent.CancelEditClicked)},
                title = {
                    Text(stringResource(delete_project_confirm))
                },
                text = {
                    Text(stringResource(delete_project_confirm_details))
                },
                confirmButton = {
                    TextButton(
                        onClick = {onEvent(ProjectUiEvent.ConfirmDeleteClicked)},
                    ) {
                        Text(stringResource(delete))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {onEvent(ProjectUiEvent.CancelDeleteClicked)},
                    ) {
                        Text(stringResource(cancel))
                    }
                },
            )
        }
    }
}

