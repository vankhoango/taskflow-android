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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskflow.auth.domain.AuthState

@Composable
fun AuthGateRoute(
    onAuthenticated: () -> Unit,
    onUnauthenticated: () -> Unit,
    viewModel: AuthGateViewModel = hiltViewModel(),
) {
    val authState =
        viewModel.authState.collectAsStateWithLifecycle()

    when (authState.value) {

        AuthState.Loading -> {
            // Wait for DataStore.
            // We can add a splash/progress UI later.
        }

        AuthState.Unauthenticated -> {
            LaunchedEffect(Unit) {
                onUnauthenticated()
            }
        }

        is AuthState.Authenticated -> {
            LaunchedEffect(Unit) {
                onAuthenticated()
            }
        }
    }
}