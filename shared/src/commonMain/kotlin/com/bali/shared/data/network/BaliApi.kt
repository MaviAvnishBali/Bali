package com.bali.shared.data.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.bali.graphql.CreatePostMutation
import com.bali.graphql.GetFeedQuery
import com.bali.graphql.LoginWithPhoneMutation
import com.bali.graphql.CompleteProfileMutation
import com.bali.graphql.GetMeQuery
import com.bali.graphql.UpdateProfileMutation
import com.bali.shared.domain.auth.AuthResult
import com.bali.shared.domain.model.Post
import com.bali.shared.domain.model.User
import com.bali.shared.domain.model.VillageGroup

class BaliApi(private val apolloClient: ApolloClient) {

    suspend fun getFeed(page: Int, size: Int): List<Post> {
        val response = apolloClient.query(GetFeedQuery(
            page = Optional.present(page),
            size = Optional.present(size)
        )).execute()

        return response.data?.feed?.filterNotNull()?.map { feedPost ->
            Post(
                id = feedPost.id,
                content = feedPost.content,
                imageUrl = feedPost.imageUrl,
                createdAt = feedPost.createdAt.toString(),
                likesCount = feedPost.likesCount,
                author = User(
                    id = "",
                    username = feedPost.author.username ?: "Anonymous",
                    email = "",
                    role = ""
                ),
                villageGroup = VillageGroup(
                    id = "",
                    name = feedPost.villageGroup.name
                )
            )
        } ?: emptyList()
    }

    suspend fun loginWithPhone(firebaseToken: String): AuthResult {
        val response = apolloClient.mutation(LoginWithPhoneMutation(firebaseToken)).execute()
        val data = response.data?.loginWithPhone
            ?: throw RuntimeException(response.errors?.firstOrNull()?.message ?: "Login failed")

        return AuthResult(
            token = data.token,
            userId = data.user.id,
            phoneNumber = data.user.phoneNumber ?: "",
            isProfileComplete = data.isProfileComplete
        )
    }

    suspend fun completeProfile(
        name: String,
        email: String?,
        address: String,
        villageId: String
    ): Boolean {
        val response = apolloClient.mutation(CompleteProfileMutation(
            name = name,
            email = Optional.presentIfNotNull(email),
            address = address,
            villageId = villageId
        )).execute()

        return response.data?.completeProfile?.isProfileComplete ?: false
    }

    suspend fun getMe(): User {
        val response = apolloClient.query(GetMeQuery())
            .fetchPolicy(FetchPolicy.NetworkOnly)
            .execute()
            
        val data = response.data?.me
            ?: throw RuntimeException(response.errors?.firstOrNull()?.message ?: "Failed to fetch profile")

        return User(
            id = data.id,
            username = data.username ?: "",
            email = data.email ?: "",
            role = data.role.name,
            phoneNumber = data.phoneNumber,
            profileImageUrl = data.profileImageUrl,
            address = data.address,
            villageGroup = data.villageGroup?.let { vg ->
                VillageGroup(
                    id = vg.id,
                    name = vg.name,
                    description = vg.description
                )
            },
            isProfileComplete = data.isProfileComplete
        )
    }

    suspend fun updateProfile(
        username: String?,
        email: String?,
        address: String?,
        villageId: String,
        profileImageUrl: String?
    ): User {
        val response = apolloClient.mutation(UpdateProfileMutation(
            username = Optional.presentIfNotNull(username),
            email = Optional.presentIfNotNull(email),
            address = Optional.presentIfNotNull(address),
            villageId = villageId,
            profileImageUrl = Optional.presentIfNotNull(profileImageUrl)
        )).execute()
        
        val data = response.data?.updateProfile
            ?: throw RuntimeException(response.errors?.firstOrNull()?.message ?: "Failed to update profile")

        return User(
            id = data.id,
            username = data.username ?: "",
            email = data.email ?: "",
            role = "", 
            phoneNumber = "",
            profileImageUrl = null,
            address = data.address,
            villageGroup = data.villageGroup?.let { vg ->
                VillageGroup(
                    id = vg.id,
                    name = vg.name
                )
            },
            isProfileComplete = data.isProfileComplete
        )
    }

    suspend fun createPost(content: String, imageUrl: String?, villageId: String): Boolean {
        val response = apolloClient.mutation(CreatePostMutation(
            content = content,
            imageUrl = Optional.presentIfNotNull(imageUrl),
            villageId = villageId
        )).execute()
        
        return response.data?.createPost != null
    }
}
