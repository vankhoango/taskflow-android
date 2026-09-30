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

package com.example.taskflow.auth.data.local

import com.example.taskflow.auth.domain.AuthSession
import com.example.taskflow.auth.domain.AuthState
import com.example.taskflow.core.common.coroutines.ApplicationScope
import com.example.taskflow.core.network.auth.AccessTokenProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManagerImpl @Inject constructor(
    private val authLocalDataSource: AuthLocalDataSource,
    @ApplicationScope
    private val applicationScope: CoroutineScope,
) : SessionManager {

    private val _authState =
        MutableStateFlow<AuthState>(
            AuthState.Loading
        )

    override val authState: StateFlow<AuthState> =
        _authState.asStateFlow()

    init {
        applicationScope.launch {
            authLocalDataSource.session.collect { session ->
                _authState.value =
                    when {
                        session == null -> {
                            AuthState.Unauthenticated
                        }

                        session.isExpired() -> {
                            AuthState.Unauthenticated
                        }

                        else -> {
                            AuthState.Authenticated(session)
                        }
                    }
            }
        }
    }

    override fun getAccessToken(): String? {
        val state = _authState.value

        return if (state is AuthState.Authenticated) {
            state.session.accessToken
        } else {
            null
        }
    }

    override suspend fun saveSession(
        session: AuthSession,
    ) {
        authLocalDataSource.saveSession(session)
    }

    override suspend fun clearSession() {
        authLocalDataSource.clearSession()
    }

    private fun AuthSession.isExpired(): Boolean {
        val nowEpochSeconds =
            System.currentTimeMillis() / 1000

        return expiresAtEpochSeconds <= nowEpochSeconds
    }
}