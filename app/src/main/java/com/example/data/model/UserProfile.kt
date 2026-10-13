package com.example.data.model

data class UserProfile(
    val name: String = "Guest Explorer",
    val email: String = "guest@gritcoach.ai",
    val plan: String = "Free Guest Tier",
    val guestMessagesUsed: Int = 0,
    val guestMessageLimit: Int = 15,
    val isSignedIn: Boolean = false
) {
    val isLimitReached: Boolean
        get() = !isSignedIn && guestMessagesUsed >= guestMessageLimit
}
