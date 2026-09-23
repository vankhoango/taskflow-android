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

package com.example.taskflow.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS projects (
                id INTEGER NOT NULL,
                name TEXT NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO projects (id, name) VALUES (1, 'Default')
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE tasks_new (
                id INTEGER NOT NULL,
                projectId INTEGER NOT NULL,
                title TEXT NOT NULL,
                completed INTEGER NOT NULL,
                PRIMARY KEY(id),
                FOREIGN KEY(projectId)
                    REFERENCES projects(id)
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO tasks_new (
                id,
                projectId,
                title,
                completed
            )
            SELECT
                id,
                1,
                title,
                completed
            FROM tasks
            """.trimIndent()
        )
        db.execSQL(
            "DROP TABLE tasks"
        )
        db.execSQL(
            "ALTER TABLE tasks_new RENAME TO tasks"
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_tasks_projectId
            ON tasks(projectId)
            """.trimIndent()
        )
    }
}