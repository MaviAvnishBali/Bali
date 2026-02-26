package com.bali.shared.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bali.shared.domain.model.Post
import com.bali.shared.domain.repository.BaliRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: BaliRepository) : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        fetchFeed()
    }

    fun fetchFeed() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _posts.value = repository.getFeed(0, 20)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _loading.value = false
            }
        }
    }
}
