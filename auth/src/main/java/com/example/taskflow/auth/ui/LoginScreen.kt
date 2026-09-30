package com.example.taskflow.auth.ui

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

    val scrollState = rememberScrollState()

    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize().padding(12.dp)
                .imePadding()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(
                space = 24.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.taskflow_login),
                style = MaterialTheme.typography.headlineMedium
            )

            state.error?.let {
                Text(text = it.toMessage().asString(),
                    color = MaterialTheme.colorScheme.error
                )
            }

            LoginCard(
                state.isSubmitting,
                onLoginClicked = { username, password ->
                    onEvent(LoginUiEvent.LoginClicked(username, password))
                },
                Modifier.fillMaxWidth().padding(horizontal = 24.dp)
            )

            TextButton(
                enabled = !state.isSubmitting,
                onClick = onSignUpClicked,
            ) {
                Text(text = stringResource(R.string.create_an_account),
                    textDecoration = TextDecoration.Underline)
            }
        }
    }
}

@Composable
fun LoginCard(
    isLoggingIn: Boolean,
    onLoginClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var username by rememberSaveable {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    ElevatedCard(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(36.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = stringResource(id = R.string.username)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.fillMaxWidth(),
                value = password,
                leadingIcon = {
                    Icon(
                        Icons.Default.Password,
                        contentDescription = stringResource(id = R.string.password)
                    )
                },
                onValueChange = {
                    password = it
                },
                label = {
                    Text(stringResource(R.string.password))
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = stringResource(
                                if (passwordVisible) {
                                    R.string.password_hide
                                } else {
                                    R.string.password_show
                                }
                            ),
                        )
                    }
                },
            )

            val canLogin = username.isNotBlank() &&
                    password.isNotEmpty() &&
                    !isLoggingIn

            Button(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = canLogin,
                onClick = { onLoginClicked (username, password)},
            ) {
                if (isLoggingIn) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.log_in))
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun LoginScreenPreview() {
    LoginScreen(LoginUiState(), {}, {})
}