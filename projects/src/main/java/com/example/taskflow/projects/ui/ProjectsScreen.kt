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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskflow.projects.domain.Project

@Composable
fun ProjectsScreen(
    uiState: ProjectUiState,
    onProjectClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Project) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancelEditor: () -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp),
    ) {

        uiState.projects.forEach { project ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onProjectClick(project.id)
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = project.name,
                    modifier = Modifier.weight(1f),
                )

                TextButton(
                    onClick = {
                        onEditClick(project)
                    },
                ) {
                    Text("Edit")
                }

                TextButton(
                    onClick = {
                        onDeleteClick(project.id)
                    },
                ) {
                    Text("Delete")
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp),
        )

        Button(
            onClick = onAddClick,
        ) {
            Text("Add Project")
        }
    }

    //Add/Edit dialog
    if (uiState.showEditor) {
        AlertDialog(
            onDismissRequest = onCancelEditor,
            title = {
                Text(
                    if (uiState.editingProjectId == null) {
                        "Add Project"
                    } else {
                        "Edit Project"
                    }
                )
            },
            text = {
                OutlinedTextField(
                    value = uiState.projectName,
                    onValueChange = onNameChange,
                    label = {
                        Text("Name")
                    },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onSave,
                    enabled = uiState.projectName.isNotBlank(),
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onCancelEditor,
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    //Delete dialog
    if (uiState.deletingProjectId != null) {
        AlertDialog(
            onDismissRequest = onCancelDelete,
            title = {
                Text("Delete Project?")
            },
            text = {
                Text(
                    "This will also delete the project's tasks."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmDelete,
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onCancelDelete,
                ) {
                    Text("Cancel")
                }
            },
        )
    }
}

