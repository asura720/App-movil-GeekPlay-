package com.example.geekplayproyecto.data.local.ban

import kotlinx.coroutines.flow.Flow

class BanNotificationRepository(private val banNotificationDao: BanNotificationDao) {

    fun getBanNotificationsByUser(userId: Long): Flow<List<BanNotification>> {
        return banNotificationDao.getBanNotificationsByUser(userId)
    }

    suspend fun insert(banNotification: BanNotification) {
        banNotificationDao.insert(banNotification)
    }
}
