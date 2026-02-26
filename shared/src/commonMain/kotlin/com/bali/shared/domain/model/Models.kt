package com.bali.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val villageGroup: VillageGroup? = null,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null,
    val address: String? = null,
    val isProfileComplete: Boolean = true
)

@Serializable
data class VillageGroup(
    val id: String,
    val name: String,
    val description: String? = null
)

@Serializable
data class Post(
    val id: String,
    val content: String,
    val imageUrl: String? = null,
    val author: User,
    val villageGroup: VillageGroup,
    val likesCount: Int = 0,
    val comments: List<Comment>? = null,
    val createdAt: String
)

@Serializable
data class Comment(
    val id: String,
    val content: String,
    val author: User,
    val createdAt: String
)
