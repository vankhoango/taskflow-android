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

package com.example.taskflow.auth.data

import com.example.taskflow.auth.data.local.AuthLocalDataSource
import com.example.taskflow.auth.data.local.SessionManager
import com.example.taskflow.auth.data.remote.RemoteAuthDataSource
import com.example.taskflow.auth.domain.AuthRepository
import com.example.taskflow.auth.domain.AuthSession
import com.example.taskflow.auth.domain.AuthState
import com.example.taskflow.auth.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteAuthDataSource: RemoteAuthDataSource,
    private val sessionManager: SessionManager,
    ) : AuthRepository {

    override fun observeAuthState(): Flow<AuthState> {
        return sessionManager.authState
    }

    override suspend fun register(
        username: String,
        password: String
    ): User {
        val response = remoteAuthDataSource.register(username, password)
        return User(
            id = response.id,
            username = response.username,
        )
    }

    override suspend fun login(
        username: String,
        password: String,
    ): AuthSession {
        val response = remoteAuthDataSource.login(
            username = username,
            password = password,
        )

        val nowEpochSeconds =
            System.currentTimeMillis() / 1000

        val session = AuthSession(
            accessToken = response.accessToken,
            tokenType = response.tokenType,
            refreshToken = response.refreshToken,
            expiresAtEpochSeconds =
                nowEpochSeconds + response.expiresIn,
        )

        sessionManager.saveSession(session)

        return session
    }

    override fun observeSession(): StateFlow<AuthState> {
        return sessionManager.authState
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }
}