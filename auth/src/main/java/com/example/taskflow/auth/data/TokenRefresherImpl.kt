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

import com.example.taskflow.auth.data.local.SessionManager
import com.example.taskflow.auth.domain.AuthRepository
import com.example.taskflow.auth.domain.AuthState
import com.example.taskflow.core.network.auth.AuthenticationFailureHandler
import com.example.taskflow.core.network.auth.TokenRefresher
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefresherImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val authenticationFailureHandler:
    AuthenticationFailureHandler,
) : TokenRefresher {

    private val refreshLock = Any()

    override fun refreshToken(
        failedAccessToken: String,
    ): String? {
        return synchronized(refreshLock) {
            refreshTokenSynchronized(
                failedAccessToken
            )
        }
    }

    private fun refreshTokenSynchronized(
        failedAccessToken: String,
    ): String? =
        runBlocking {
            try {
                val state =
                    sessionManager.authState.value

                if (
                    state !is
                            AuthState.Authenticated
                ) {
                    return@runBlocking null
                }

                val currentAccessToken =
                    state.session.accessToken

                // Another request already refreshed
                // this access token.
                if (
                    currentAccessToken !=
                    failedAccessToken
                ) {
                    return@runBlocking currentAccessToken
                }

                authRepository.refreshSession()
            } catch (exception: HttpException) {
                if (exception.code() == 401) {
                    authenticationFailureHandler
                        .onAuthenticationFailed()
                }

                null
            } catch (exception: IOException) {
                null
            }
        }
}