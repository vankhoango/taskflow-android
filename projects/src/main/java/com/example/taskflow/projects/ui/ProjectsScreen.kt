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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.taskflow.projects.R.string.save
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

                IconButton(
                    onClick = {
                        onEditClick(project)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(edit)
                    )
                }

                IconButton(
                    onClick = {
                        onDeleteClick(project.id)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(delete)
                    )
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
                        stringResource(add_project)
                    } else {
                        stringResource(edit_project)
                    }
                )
            },
            text = {
                OutlinedTextField(
                    value = uiState.projectName,
                    onValueChange = onNameChange,
                    label = {
                        Text(stringResource(name))
                    },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onSave,
                    enabled = uiState.projectName.isNotBlank(),
                ) {
                    Text(stringResource(save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onCancelEditor,
                ) {
                    Text(stringResource(cancel))
                }
            },
        )
    }

    //Delete dialog
    if (uiState.deletingProjectId != null) {
        AlertDialog(
            onDismissRequest = onCancelDelete,
            title = {
                Text(stringResource(delete_project_confirm))
            },
            text = {
                Text(stringResource(delete_project_confirm_details))
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmDelete,
                ) {
                    Text(stringResource(delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onCancelDelete,
                ) {
                    Text(stringResource(cancel))
                }
            },
        )
    }
}

