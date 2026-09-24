package com.example.taskflow.core.database.project

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Upsert
    suspend fun upsertProject(project: ProjectEntity)

    @Query("SELECT * FROM projects ORDER BY name")
    fun observeProjects(): Flow<List<ProjectEntity>>

    @Upsert
    suspend fun upsertProjects(
        projects: List<ProjectEntity>,
    )

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects()

    @Transaction
    suspend fun replaceProjects(
        projects: List<ProjectEntity>,
    ) {
        deleteAllProjects()
        upsertProjects(projects)
    }

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProject(projectId: Long)
}