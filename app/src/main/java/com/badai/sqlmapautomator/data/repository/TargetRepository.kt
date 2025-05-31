package com.badai.sqlmapautomator.data.repository

import com.badai.sqlmapautomator.data.dao.TargetDao
import com.badai.sqlmapautomator.data.models.Target
import com.badai.sqlmapautomator.data.models.TargetStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TargetRepository @Inject constructor(
    private val targetDao: TargetDao
) {
    fun getAllTargets(): Flow<List<Target>> = targetDao.getAllTargets()

    fun getTargetsByStatus(status: TargetStatus): Flow<List<Target>> = 
        targetDao.getTargetsByStatus(status)

    suspend fun getTargetById(id: Long): Target? = targetDao.getTargetById(id)

    suspend fun insertTarget(target: Target): Long = targetDao.insertTarget(target)

    suspend fun insertTargets(targets: List<Target>) = targetDao.insertTargets(targets)

    suspend fun updateTarget(target: Target) = targetDao.updateTarget(target)

    suspend fun deleteTarget(target: Target) = targetDao.deleteTarget(target)

    suspend fun deleteTargetById(id: Long) = targetDao.deleteTargetById(id)

    suspend fun deleteAllTargets() = targetDao.deleteAllTargets()

    suspend fun getTargetCountByStatus(status: TargetStatus): Int = 
        targetDao.getTargetCountByStatus(status)
}