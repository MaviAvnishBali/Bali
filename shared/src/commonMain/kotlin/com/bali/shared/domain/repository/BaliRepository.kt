package com.bali.shared.domain.repository

import com.bali.shared.domain.model.Post
import com.bali.shared.domain.model.User
import kotlinx.coroutines.flow.Flow

interface BaliRepository {
    suspend fun getFeed(page: Int, size: Int): List<Post>
    fun getFeedFlow(page: Int, size: Int): Flow<List<Post>>
    suspend fun getProfile(): User
    suspend fun updateProfile(
        username: String?,
        email: String?,
        address: String?,
        villageId: String,
        profileImageUrl: String?
    ): User
    suspend fun createPost(content: String, imageUrl: String?, villageId: String): Boolean
}
