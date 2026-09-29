package com.example.taskflow.auth.ui

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.taskflow.auth.R

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

@Composable
internal fun LoginScreen(
    state: LoginUiState,
    onEvent: (LoginUiEvent) -> Unit,
    onSignUpClicked: () -> Unit,
) {
    var username by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    OutlinedTextField(
        value = username,
        onValueChange = {
            username = it
        },
        label = {
            Text(stringResource(R.string.username))
        },
        singleLine = true,
    )

    OutlinedTextField(
        value = password,
        onValueChange = {
            password = it
        },
        label = {
            Text(stringResource(R.string.password))
        },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
    )

    val canLogin = username.isNotBlank() &&
                password.isNotEmpty() &&
                !state.isSubmitting

    Button(
        enabled = canLogin,
        onClick = {
            onEvent(
                LoginUiEvent.LoginClicked(
                    username = username,
                    password = password,
                )
            )
        },
    ) {
        if (state.isSubmitting) {
            CircularProgressIndicator()
        } else {
            Text(stringResource(R.string.log_in))
        }
    }

    TextButton(
        enabled = !state.isSubmitting,
        onClick = onSignUpClicked,
    ) {
        Text(text = stringResource(R.string.create_an_account))
    }
}