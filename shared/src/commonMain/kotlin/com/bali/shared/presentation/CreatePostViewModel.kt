package com.bali.shared.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bali.shared.domain.repository.BaliRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreatePostViewModel(
    private val repository: BaliRepository,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _postSuccess = MutableStateFlow(false)
    val postSuccess = _postSuccess.asStateFlow()

    fun createPost(content: String, imageUrl: String?) {
        val villageId = sharedViewModel.currentUser.value?.villageGroup?.id ?: "1"
        if (villageId == null) {
            _error.value = "User village not found. Cannot create post."
            return
        }

        if (content.isBlank() && imageUrl.isNullOrBlank()) {
            _error.value = "You must provide either text or an image."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _postSuccess.value = false
            try {
                // The repository was updated to use villageId to match your curl example
                val success = repository.createPost(content, imageUrl, villageId)
                if (success) {
                    _postSuccess.value = true
                } else {
                    _error.value = "Failed to create post. Please try again."
                }
            } catch (e: Exception) {
                println("❌ CreatePostViewModel: Error creating post: ${e.message}")
                _error.value = e.message ?: "An unknown error occurred."
            } finally {
                _loading.value = false
            }
        }
    }

    fun resetPostSuccess() {
        _postSuccess.value = false
    }
}
