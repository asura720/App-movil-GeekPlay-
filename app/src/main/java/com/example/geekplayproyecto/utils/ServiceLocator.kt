package com.example.geekplayproyecto.utils

import android.content.Context
import com.example.geekplayproyecto.data.local.ban.BanNotificationRepository
import com.example.geekplayproyecto.data.repository.CommentRepository
import com.example.geekplayproyecto.data.repository.LikeRepository
import com.example.geekplayproyecto.data.repository.PostRepository
import com.example.geekplayproyecto.data.repository.UserRepository

object ServiceLocator {

    // ✅ UserRepository ya no necesita DAO
    fun provideUserRepository(context: Context): UserRepository {
        return UserRepository()
    }

    // ✅ PostRepository ahora usa Retrofit internamente
    fun providePostRepository(context: Context): PostRepository {
        return PostRepository()
    }

    // ✅ CommentRepository ahora usa Retrofit internamente
    fun provideCommentRepository(context: Context): CommentRepository {
        return CommentRepository()
    }

    // ✅ LikeRepository ahora usa Retrofit internamente
    fun provideLikeRepository(context: Context): LikeRepository {
        return LikeRepository()
    }

    // ✅ BanNotificationRepository ahora usa Retrofit internamente
    fun provideBanNotificationRepository(context: Context): BanNotificationRepository {
        return BanNotificationRepository()
    }
}