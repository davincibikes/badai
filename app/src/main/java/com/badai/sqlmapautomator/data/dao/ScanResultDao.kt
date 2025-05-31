package com.badai.sqlmapautomator.data.dao

import androidx.room.*
import com.badai.sqlmapautomator.data.models.ScanResult
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanResultDao {
    @Query("SELECT * FROM scan_results ORDER BY timestamp DESC")
    fun getAllScanResults(): Flow<List<ScanResult>>

    @Query("SELECT * FROM scan_results WHERE vulnerabilityFound = 1 ORDER BY timestamp DESC")
    fun getVulnerableResults(): Flow<List<ScanResult>>

    @Query("SELECT * FROM scan_results WHERE id = :id")
    suspend fun getScanResultById(id: Long): ScanResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanResult(scanResult: ScanResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanResults(scanResults: List<ScanResult>)

    @Update
    suspend fun updateScanResult(scanResult: ScanResult)

    @Delete
    suspend fun deleteScanResult(scanResult: ScanResult)

    @Query("DELETE FROM scan_results WHERE id = :id")
    suspend fun deleteScanResultById(id: Long)

    @Query("DELETE FROM scan_results")
    suspend fun deleteAllScanResults()

    @Query("SELECT COUNT(*) FROM scan_results WHERE vulnerabilityFound = 1")
    suspend fun getVulnerableCount(): Int

    @Query("SELECT COUNT(*) FROM scan_results")
    suspend fun getTotalScanCount(): Int
}