package com.bali.shared.data.repository

import com.bali.shared.data.network.BaliApi
import com.bali.shared.domain.model.Post
import com.bali.shared.domain.model.User
import com.bali.shared.domain.repository.BaliRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BaliRepositoryImpl(private val api: BaliApi) : BaliRepository {
    override suspend fun getFeed(page: Int, size: Int): List<Post> {
        return api.getFeed(page, size)
    }

    override fun getFeedFlow(page: Int, size: Int): Flow<List<Post>> = flow {
        emit(api.getFeed(page, size))
    }

    override suspend fun getProfile(): User {
        return api.getMe()
    }

    override suspend fun updateProfile(
        username: String?,
        email: String?,
        address: String?,
        villageId: String,
        profileImageUrl: String?
    ): User {
        return api.updateProfile(username, email, address, villageId, profileImageUrl)
    }

    override suspend fun createPost(content: String, imageUrl: String?, villageId: String): Boolean {
        return api.createPost(content, imageUrl, villageId)
    }
}
