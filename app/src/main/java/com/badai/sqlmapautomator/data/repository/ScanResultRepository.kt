package com.badai.sqlmapautomator.data.repository

import com.badai.sqlmapautomator.data.dao.ScanResultDao
import com.badai.sqlmapautomator.data.models.ScanResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanResultRepository @Inject constructor(
    private val scanResultDao: ScanResultDao
) {
    fun getAllScanResults(): Flow<List<ScanResult>> = scanResultDao.getAllScanResults()

    fun getVulnerableResults(): Flow<List<ScanResult>> = scanResultDao.getVulnerableResults()

    suspend fun getScanResultById(id: Long): ScanResult? = scanResultDao.getScanResultById(id)

    suspend fun insertScanResult(scanResult: ScanResult): Long = 
        scanResultDao.insertScanResult(scanResult)

    suspend fun insertScanResults(scanResults: List<ScanResult>) = 
        scanResultDao.insertScanResults(scanResults)

    suspend fun updateScanResult(scanResult: ScanResult) = 
        scanResultDao.updateScanResult(scanResult)

    suspend fun deleteScanResult(scanResult: ScanResult) = 
        scanResultDao.deleteScanResult(scanResult)

    suspend fun deleteScanResultById(id: Long) = scanResultDao.deleteScanResultById(id)

    suspend fun deleteAllScanResults() = scanResultDao.deleteAllScanResults()

    suspend fun getVulnerableCount(): Int = scanResultDao.getVulnerableCount()

    suspend fun getTotalScanCount(): Int = scanResultDao.getTotalScanCount()
}