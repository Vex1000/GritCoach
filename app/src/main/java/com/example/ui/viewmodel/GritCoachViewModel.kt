package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CoachCategory
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import com.example.data.model.UserProfile
import com.example.data.repository.GritCoachRepository
import com.example.ui.components.NavTab
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Welcome : Screen()
    data object OnboardingTransition : Screen()
    data class Main(val tab: NavTab = NavTab.HOME) : Screen()
    data class Chat(val conversationId: String, val category: CoachCategory) : Screen()
}

class GritCoachViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = GritCoachRepository(
        conversationDao = database.conversationDao(),
        messageDao = database.messageDao()
    )

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Welcome)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val conversations: StateFlow<List<ConversationEntity>> = _searchQuery
        .flatMapLatest { query -> repository.searchConversations(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeConversation = MutableStateFlow<ConversationEntity?>(null)
    val activeConversation: StateFlow<ConversationEntity?> = _activeConversation.asStateFlow()

    private val _activeCategory = MutableStateFlow(CoachCategory.GENERAL)
    val activeCategory: StateFlow<CoachCategory> = _activeCategory.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMessages: StateFlow<List<MessageEntity>> = _activeConversation
        .flatMapLatest { conv ->
            if (conv != null) repository.getMessages(conv.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showGuestLimitDialog = MutableStateFlow(false)
    val showGuestLimitDialog: StateFlow<Boolean> = _showGuestLimitDialog.asStateFlow()

    fun navigateTo(screen: Screen) {
        _errorMessage.value = null
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun dismissGuestLimitDialog() {
        _showGuestLimitDialog.value = false
    }

    fun startNewChat(category: CoachCategory, initialPrompt: String? = null) {
        viewModelScope.launch {
            _errorMessage.value = null
            val newConv = repository.createConversation(
                title = if (!initialPrompt.isNullOrBlank()) {
                    if (initialPrompt.length > 28) initialPrompt.take(25) + "..." else initialPrompt
                } else {
                    "${category.title} Session"
                },
                category = category
            )
            _activeConversation.value = newConv
            _activeCategory.value = category
            _currentScreen.value = Screen.Chat(conversationId = newConv.id, category = category)

            if (!initialPrompt.isNullOrBlank()) {
                sendMessage(initialPrompt)
            }
        }
    }

    fun openConversation(conversationId: String) {
        viewModelScope.launch {
            _errorMessage.value = null
            val conv = repository.getConversation(conversationId)
            if (conv != null) {
                val cat = CoachCategory.fromId(conv.categoryId)
                _activeConversation.value = conv
                _activeCategory.value = cat
                _currentScreen.value = Screen.Chat(conversationId = conv.id, category = cat)
            }
        }
    }

    fun sendMessage(promptText: String) {
        val trimmed = promptText.trim()
        if (trimmed.isBlank() || _isGenerating.value) return

        val conv = _activeConversation.value ?: return

        // Check guest limits
        val currentProfile = _userProfile.value
        if (currentProfile.isLimitReached) {
            _showGuestLimitDialog.value = true
            return
        }

        _isGenerating.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = repository.sendUserMessageAndGetAiResponse(
                conversationId = conv.id,
                userPrompt = trimmed,
                category = _activeCategory.value
            )

            result.onSuccess {
                // Increment guest usage
                if (!currentProfile.isSignedIn) {
                    _userProfile.value = currentProfile.copy(
                        guestMessagesUsed = currentProfile.guestMessagesUsed + 1
                    )
                }
            }.onFailure { ex ->
                _errorMessage.value = ex.message ?: "Failed to generate coaching response"
            }

            _isGenerating.value = false
        }
    }

    fun retryLastMessage() {
        val conv = _activeConversation.value ?: return
        if (_isGenerating.value) return

        _isGenerating.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = repository.regenerateLastResponse(
                conversationId = conv.id,
                category = _activeCategory.value
            )
            result.onFailure { ex ->
                _errorMessage.value = ex.message ?: "Failed to regenerate response"
            }
            _isGenerating.value = false
        }
    }

    fun renameConversation(id: String, newTitle: String) {
        viewModelScope.launch {
            repository.updateConversationTitle(id, newTitle.trim())
            if (_activeConversation.value?.id == id) {
                _activeConversation.value = _activeConversation.value?.copy(title = newTitle.trim())
            }
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_activeConversation.value?.id == id) {
                _activeConversation.value = null
                _currentScreen.value = Screen.Main(NavTab.HISTORY)
            }
        }
    }

    fun clearAllConversations() {
        viewModelScope.launch {
            repository.clearAllConversations()
            _activeConversation.value = null
        }
    }

    fun signInAsUser(name: String, email: String) {
        _userProfile.value = _userProfile.value.copy(
            name = name.ifBlank { "Achiever" },
            email = email.ifBlank { "achiever@gritcoach.ai" },
            plan = "GritCoach Pro",
            isSignedIn = true
        )
        _showGuestLimitDialog.value = false
    }

    fun signOut() {
        _userProfile.value = UserProfile()
        _currentScreen.value = Screen.Welcome
    }
}
