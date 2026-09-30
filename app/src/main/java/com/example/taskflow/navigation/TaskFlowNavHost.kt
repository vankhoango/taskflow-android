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

package com.example.taskflow.navigation

import SignUpRoute
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taskflow.auth.ui.AuthGateRoute
import com.example.taskflow.auth.ui.LoginRoute
import com.example.taskflow.navigation.TaskFlowDestination.SIGN_UP
import com.example.taskflow.projects.ui.ProjectsRoute
import com.example.taskflow.tasks.ui.TaskRoute

@Composable
fun TaskFlowNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TaskFlowDestination.AUTH_GATE
    ) {

        composable(
            route = TaskFlowDestination.AUTH_GATE,
        ) {
            AuthGateRoute (
                onAuthenticated = {
                    navController.navigate(
                        TaskFlowDestination.PROJECTS
                    ) {
                        popUpTo(
                            TaskFlowDestination.AUTH_GATE
                        ) {
                            inclusive = true
                        }
                    }
                },
                onUnauthenticated = {
                    navController.navigate(
                        TaskFlowDestination.LOGIN
                    ) {
                        popUpTo(
                            TaskFlowDestination.AUTH_GATE
                        ) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable (
            route = TaskFlowDestination.LOGIN
        ) {
            LoginRoute(
                onLoginSucceeded = {
                    navController.navigate(
                        TaskFlowDestination.PROJECTS
                    ) {
                        popUpTo(
                            TaskFlowDestination.LOGIN
                        ) {
                            inclusive = true
                        }
                    }
                },
                onSignUpClicked = {
                    navController.navigate(SIGN_UP)
                },
            )
        }

        composable (
            route = TaskFlowDestination.SIGN_UP
        ) {
            SignUpRoute(
                onRegistrationSucceeded = {
                    navController.popBackStack()
                },
                onLoginClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable (
            route = TaskFlowDestination.PROJECTS
        ) {
            ProjectsRoute(
                onProjectDetailsClick = {
                    navController.navigate(TaskFlowDestination.tasks(it))
                }
            )
        }

        composable(
            route = TaskFlowDestination.TASKS,
            arguments = listOf(
                navArgument(TaskFlowDestination.PROJECT_ID) {
                    type = NavType.LongType
                }
            )
        ) {
            TaskRoute(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}