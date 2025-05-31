package com.badai.sqlmapautomator.data.dao

import androidx.room.*
import com.badai.sqlmapautomator.data.models.Target
import com.badai.sqlmapautomator.data.models.TargetStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetDao {
    @Query("SELECT * FROM targets ORDER BY createdAt DESC")
    fun getAllTargets(): Flow<List<Target>>

    @Query("SELECT * FROM targets WHERE status = :status")
    fun getTargetsByStatus(status: TargetStatus): Flow<List<Target>>

    @Query("SELECT * FROM targets WHERE id = :id")
    suspend fun getTargetById(id: Long): Target?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: Target): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTargets(targets: List<Target>)

    @Update
    suspend fun updateTarget(target: Target)

    @Delete
    suspend fun deleteTarget(target: Target)

    @Query("DELETE FROM targets WHERE id = :id")
    suspend fun deleteTargetById(id: Long)

    @Query("DELETE FROM targets")
    suspend fun deleteAllTargets()

    @Query("SELECT COUNT(*) FROM targets WHERE status = :status")
    suspend fun getTargetCountByStatus(status: TargetStatus): Int
}