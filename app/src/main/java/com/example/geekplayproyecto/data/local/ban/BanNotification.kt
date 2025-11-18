package com.example.geekplayproyecto.data.local.ban

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.geekplayproyecto.data.local.user.UserEntity

@Entity(
    tableName = "ban_notifications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BanNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Long,
    val reason: String,
    val duration: String,
    val appealGuide: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
