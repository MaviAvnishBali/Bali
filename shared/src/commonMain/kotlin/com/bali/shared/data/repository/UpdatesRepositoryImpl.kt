package com.bali.shared.data.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.bali.graphql.GetUpdatesQuery
import com.bali.shared.domain.model.Update
import com.bali.shared.domain.repository.UpdatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UpdatesRepositoryImpl(
    private val apolloClient: ApolloClient
) : UpdatesRepository {

    override fun getUpdates(): Flow<List<Update>> {
        return apolloClient.query(GetUpdatesQuery())
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .toFlow()
            .map { response ->
                response.data?.updates?.map {
                    Update(
                        id = it.id,
                        title = it.title,
                        message = it.message
                    )
                } ?: emptyList()
            }
    }
}
