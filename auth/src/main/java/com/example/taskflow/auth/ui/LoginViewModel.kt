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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

    private val _effects =
        MutableSharedFlow<LoginUiEffect>()

    val effects: SharedFlow<LoginUiEffect> =
        _effects.asSharedFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.LoginClicked -> {
                login(
                    username = event.username,
                    password = event.password,
                )
            }
        }
    }

    private fun login(
        username: String,
        password: String,
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isSubmitting = true)
            }

            try {
                val session = loginUseCase(
                    username = username,
                    password = password,
                )

                _effects.emit(
                    LoginUiEffect.LoginSucceeded(
                        session = session,
                    )
                )
            } catch (e: Exception) {
                _effects.emit(
                    LoginUiEffect.LoginFailed(
                        message = e.message
                            ?: "Login failed",
                    )
                )
            } finally {
                _uiState.update {
                    it.copy(isSubmitting = false)
                }
            }
        }
    }
}