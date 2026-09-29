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

package com.example.taskflow.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taskflow.auth.R

@Composable
fun SignUpScreen(uiState: SignUpUiState, onEvent: (SignUpUiEvent) -> Unit) {

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable {mutableStateOf("")}
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        OutlinedTextField(
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = stringResource(id = R.string.username)
                )
            },
            value = username,
            onValueChange = {
                username = it
            },
            label = {
                Text(stringResource(id = R.string.username))
            },
            singleLine = true,
        )

        OutlinedTextField(
            leadingIcon = {
                Icon(
                    Icons.Default.Password,
                    contentDescription = stringResource(id = R.string.password)
                )
            },
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(stringResource(id = R.string.password))
            },
            singleLine = true,
        )

        OutlinedTextField(
            leadingIcon = {
                Icon(
                    Icons.Default.Password,
                    contentDescription = stringResource(id = R.string.confirm_password)
                )
            },
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
            },
            label = {
                Text(stringResource(id = R.string.confirm_password))
            },
            singleLine = true,
        )
    }

    val canSignUp =
        username.isNotBlank() &&
                password.isNotEmpty() &&
                password == confirmPassword &&
                !uiState.isSubmitting

    FilledTonalButton(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        enabled = canSignUp,
        onClick = {
            onEvent(
                SignUpUiEvent.SignUpClicked(
                    username = username,
                    password = password,
                )
            )
        },
    ) {
        if (uiState.isSubmitting) {
            CircularProgressIndicator(modifier = Modifier.height(32.dp))
        } else {
            Text(stringResource(R.string.sign_up))
        }
    }
}