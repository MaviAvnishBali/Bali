package com.bali.shared.data.local

import com.russhwolf.settings.Settings

class SessionManager(private val settings: Settings) {

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_PHONE_NUMBER = "phone_number"
    }

    fun saveSession(token: String, userId: String, phoneNumber: String) {
        settings.putString(KEY_TOKEN, token)
        settings.putString(KEY_USER_ID, userId)
        settings.putString(KEY_PHONE_NUMBER, phoneNumber)
    }

    fun getToken(): String? = settings.getStringOrNull(KEY_TOKEN)
    fun getUserId(): String? = settings.getStringOrNull(KEY_USER_ID)
    fun getPhoneNumber(): String? = settings.getStringOrNull(KEY_PHONE_NUMBER)

    fun clearSession() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_PHONE_NUMBER)
    }

    fun isLogged(): Boolean = getToken() != null
}
