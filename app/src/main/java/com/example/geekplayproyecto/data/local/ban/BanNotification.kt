package com.example.geekplayproyecto.data.local.ban

// DTO simple
data class BanNotification(
    val id: Long,
    val userId: Long,
    val reason: String,
    val duration: String,
    val appealGuide: String,
    val timestamp: String?, // El servidor manda fecha legible
    val isRead: Boolean
)