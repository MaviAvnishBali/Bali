package com.bali.shared.domain.repository

import com.bali.shared.domain.model.Update
import kotlinx.coroutines.flow.Flow

interface UpdatesRepository {
    fun getUpdates(): Flow<List<Update>>
}
