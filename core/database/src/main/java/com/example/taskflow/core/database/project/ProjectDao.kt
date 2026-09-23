package com.example.taskflow.core.database.project

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Query("SELECT * FROM projects ORDER BY name")
    fun observeProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(
        projects: List<ProjectEntity>,
    )

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects()

    @Transaction
    suspend fun replaceProjects(
        projects: List<ProjectEntity>,
    ) {
        deleteAllProjects()
        insertProjects(projects)
    }
}