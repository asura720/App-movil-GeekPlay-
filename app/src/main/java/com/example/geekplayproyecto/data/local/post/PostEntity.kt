package com.example.geekplayproyecto.data.local.post

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.geekplayproyecto.data.local.user.UserEntity
import com.example.geekplayproyecto.domain.geekplay.Category
import com.example.geekplayproyecto.domain.geekplay.Post

@Entity(
    tableName = "posts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("authorId")]
)
data class PostEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val category: String,
    val authorId: Long,
    val publishedAt: Long,
    val imageUrl: String?
)

// ✅ Clase para el JOIN con datos del autor y likes
data class PostWithDetails(
    @Embedded val post: PostEntity,
    val authorName: String,
    val authorProfileImageUrl: String?,
    val likesCount: Int
)

fun PostWithDetails.toDomain() = Post(
    id = post.id,
    title = post.title,
    summary = post.summary,
    content = post.content,
    category = Category.valueOf(post.category),
    authorName = authorName,
    authorProfileImageUrl = authorProfileImageUrl,
    publishedAt = post.publishedAt,
    likes = likesCount,
    imageUrl = post.imageUrl
)

fun Post.toEntity(authorId: Long) = PostEntity(
    id = id,
    title = title,
    summary = summary,
    content = content,
    category = category.name,
    authorId = authorId,
    publishedAt = publishedAt,
    imageUrl = imageUrl
)
