package com.example.geekplayproyecto.data.local.ban

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BanNotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(banNotification: BanNotification)

    @Query("SELECT * FROM ban_notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getBanNotificationsByUser(userId: Long): Flow<List<BanNotification>>

}
