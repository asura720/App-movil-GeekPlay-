package com.example.geekplayproyecto.data.local.post

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun count(): Int

    @Query("""
        SELECT 
            posts.*,
            users.name as authorName,
            users.profileImagePath as authorProfileImageUrl,
            (SELECT COUNT(*) FROM likes WHERE likes.postId = posts.id) as likesCount
        FROM posts
        INNER JOIN users ON posts.authorId = users.id
        ORDER BY publishedAt DESC
    """)
    fun observeAll(): Flow<List<PostWithDetails>>

    @Query("""
        SELECT 
            posts.*,
            users.name as authorName,
            users.profileImagePath as authorProfileImageUrl,
            (SELECT COUNT(*) FROM likes WHERE likes.postId = posts.id) as likesCount
        FROM posts
        INNER JOIN users ON posts.authorId = users.id
        WHERE posts.category = :category
        ORDER BY publishedAt DESC
    """)
    fun observeByCategory(category: String): Flow<List<PostWithDetails>>

    @Query("""
        SELECT 
            posts.*,
            users.name as authorName,
            users.profileImagePath as authorProfileImageUrl,
            (SELECT COUNT(*) FROM likes WHERE likes.postId = posts.id) as likesCount
        FROM posts
        INNER JOIN users ON posts.authorId = users.id
        WHERE users.email = :email
        ORDER BY publishedAt DESC
    """)
    fun observeByAuthorEmail(email: String): Flow<List<PostWithDetails>>

    @Query("""
        SELECT 
            posts.*,
            users.name as authorName,
            users.profileImagePath as authorProfileImageUrl,
            (SELECT COUNT(*) FROM likes WHERE likes.postId = posts.id) as likesCount
        FROM posts
        INNER JOIN users ON posts.authorId = users.id
        WHERE posts.id = :id
        LIMIT 1
    """)
    suspend fun getById(id: String): PostWithDetails?

    @Query("""
        SELECT 
            posts.*,
            users.name as authorName,
            users.profileImagePath as authorProfileImageUrl,
            (SELECT COUNT(*) FROM likes WHERE likes.postId = posts.id) as likesCount
        FROM posts
        INNER JOIN users ON posts.authorId = users.id
        WHERE posts.title LIKE '%' || :query || '%' 
        OR posts.summary LIKE '%' || :query || '%' 
        OR posts.content LIKE '%' || :query || '%'
        ORDER BY publishedAt DESC
    """)
    fun searchPosts(query: String): Flow<List<PostWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: PostEntity)

    @Query("DELETE FROM posts")
    suspend fun clearAll()

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deleteById(postId: String)
}