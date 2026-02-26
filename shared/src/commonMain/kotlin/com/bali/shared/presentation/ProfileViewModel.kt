package com.bali.shared.presentation

import androidx.lifecycle.viewModelScope
import com.bali.shared.domain.model.User
import com.bali.shared.domain.repository.BaliRepository
import com.bali.shared.presentation.base.BaseViewModel
import com.bali.shared.presentation.base.UiEffect
import com.bali.shared.presentation.base.UiIntent
import com.bali.shared.presentation.base.UiState
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

sealed class ProfileIntent : UiIntent {
    object FetchProfile : ProfileIntent()
    data class UpdateProfile(
        val username: String,
        val email: String,
        val address: String = "",
        val villageId: String = "1",
        val profileImageUrl: String = ""
    ) : ProfileIntent()
}

sealed class ProfileEffect : UiEffect {
    data class ShowError(val message: String) : ProfileEffect()
    object ProfileUpdatedSuccessfully : ProfileEffect()
}

class ProfileViewModel(private val repository: BaliRepository) :
    BaseViewModel<ProfileState, ProfileIntent, ProfileEffect>() {

    override fun createInitialState(): ProfileState = ProfileState()

    init {
        onIntent(ProfileIntent.FetchProfile)
    }

    override fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.FetchProfile -> fetchProfile()
            is ProfileIntent.UpdateProfile -> updateProfile(intent)
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                val user = repository.getProfile()
                setState { copy(user = user, isLoading = false) }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to fetch profile"
                setState { copy(error = errorMessage, isLoading = false) }
                setEffect { ProfileEffect.ShowError(errorMessage) }
            }
        }
    }

    private fun updateProfile(intent: ProfileIntent.UpdateProfile) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                val updatedUser = repository.updateProfile(
                    intent.username,
                    intent.email,
                    intent.address,
                    intent.villageId,
                    intent.profileImageUrl
                )
                setState { copy(user = updatedUser, isLoading = false) }
                setEffect { ProfileEffect.ProfileUpdatedSuccessfully }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to update profile"
                setState { copy(error = errorMessage, isLoading = false) }
                setEffect { ProfileEffect.ShowError(errorMessage) }
            }
        }
    }
}
