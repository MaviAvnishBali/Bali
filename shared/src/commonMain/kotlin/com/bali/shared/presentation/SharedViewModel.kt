package com.bali.shared.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bali.shared.domain.model.Post
import com.bali.shared.domain.model.User
import com.bali.shared.domain.repository.BaliRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedViewModel(private val repository: BaliRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost = _selectedPost.asStateFlow()

    fun fetchProfile() {
        println("📡 SharedViewModel: fetchProfile() called")
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val user = repository.getProfile()
                println("✅ SharedViewModel: fetchProfile() success. Username: '${user.username}', Email: '${user.email}'")
                _currentUser.value = user
            } catch (e: Exception) {
                println("❌ SharedViewModel: fetchProfile() failed: ${e.message}")
                _error.value = e.message ?: "Failed to fetch profile"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateProfile(
        username: String? = null,
        email: String? = null,
        address: String? = null,
        villageId: String,
        profileImageUrl: String? = null
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val updatedUser = repository.updateProfile(
                    username,
                    email,
                    address,
                    villageId,
                    profileImageUrl
                )
                _currentUser.value = updatedUser
                println("✅ SharedViewModel: updateProfile() success")
            } catch (e: Exception) {
                println("❌ SharedViewModel: updateProfile() failed: ${e.message}")
                _error.value = e.message ?: "Failed to update profile"
            } finally {
                _loading.value = false
            }
        }
    }

    fun selectPost(post: Post?) {
        _selectedPost.value = post
    }
}
