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

package com.example.taskflow.core.network.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenRefresher: TokenRefresher,
) : Authenticator {

    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {

        if (
            response.request.url.encodedPath ==
            "/auth/refresh"
        ) {
            return null
        }

        if (responseCount(response) >= 2) {
            return null
        }

        val newAccessToken =
            tokenRefresher.refreshToken()
                ?: return null

        return response.request
            .newBuilder()
            .header(
                "Authorization",
                "Bearer $newAccessToken",
            )
            .build()
    }

    private fun responseCount(
        response: Response,
    ): Int {
        var count = 1
        var priorResponse =
            response.priorResponse

        while (priorResponse != null) {
            count++
            priorResponse =
                priorResponse.priorResponse
        }

        return count
    }
}