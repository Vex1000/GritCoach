package com.example.data.repository

import com.example.data.local.ConversationDao
import com.example.data.local.MessageDao
import com.example.data.model.CoachCategory
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import com.example.data.remote.GeminiClient
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GritCoachRepository(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val geminiClient: GeminiClient = GeminiClient()
) {
    val allConversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversations()

    fun searchConversations(query: String): Flow<List<ConversationEntity>> {
        return if (query.isBlank()) {
            conversationDao.getAllConversations()
        } else {
            conversationDao.searchConversations(query.trim())
        }
    }

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForConversation(conversationId)
    }

    suspend fun getConversation(id: String): ConversationEntity? {
        return conversationDao.getConversationById(id)
    }

    suspend fun createConversation(title: String, category: CoachCategory): ConversationEntity {
        val entity = ConversationEntity(
            id = UUID.randomUUID().toString(),
            title = title.ifBlank { "${category.title} Coaching" },
            categoryId = category.id,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        conversationDao.insertConversation(entity)
        return entity
    }

    suspend fun updateConversationTitle(id: String, newTitle: String) {
        conversationDao.updateTitle(id, newTitle)
    }

    suspend fun deleteConversation(id: String) {
        conversationDao.deleteConversationById(id)
    }

    suspend fun clearAllConversations() {
        conversationDao.deleteAllConversations()
    }

    suspend fun sendUserMessageAndGetAiResponse(
        conversationId: String,
        userPrompt: String,
        category: CoachCategory
    ): Result<MessageEntity> {
        val now = System.currentTimeMillis()
        val userMsg = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = "user",
            content = userPrompt,
            timestamp = now
        )
        messageDao.insertMessage(userMsg)
        conversationDao.touchConversation(conversationId, now)

        return try {
            val historyEntities = messageDao.getMessagesList(conversationId)
            val history = historyEntities.map { it.role to it.content }

            val responseText = geminiClient.generateCoachingResponse(
                prompt = userPrompt,
                category = category,
                conversationHistory = history
            )

            val aiMsg = MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = "model",
                content = responseText,
                timestamp = System.currentTimeMillis()
            )
            messageDao.insertMessage(aiMsg)
            conversationDao.touchConversation(conversationId, System.currentTimeMillis())

            // If the conversation title is generic, update title to summary of prompt
            val conv = conversationDao.getConversationById(conversationId)
            if (conv != null && conv.title.endsWith("Coaching")) {
                val shortTitle = if (userPrompt.length > 32) userPrompt.take(29) + "..." else userPrompt
                conversationDao.updateTitle(conversationId, shortTitle)
            }

            Result.success(aiMsg)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun regenerateLastResponse(
        conversationId: String,
        category: CoachCategory
    ): Result<MessageEntity> {
        val messages = messageDao.getMessagesList(conversationId)
        val lastUserMessage = messages.lastOrNull { it.role == "user" }
            ?: return Result.failure(IllegalStateException("No user message to regenerate from"))

        // Delete trailing model message if present
        val lastMessage = messages.lastOrNull()
        if (lastMessage != null && lastMessage.role == "model") {
            messageDao.deleteMessageById(lastMessage.id)
        }

        return try {
            val history = messages.filter { it.id != lastMessage?.id }.map { it.role to it.content }
            val responseText = geminiClient.generateCoachingResponse(
                prompt = lastUserMessage.content,
                category = category,
                conversationHistory = history
            )

            val newAiMsg = MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = "model",
                content = responseText,
                timestamp = System.currentTimeMillis()
            )
            messageDao.insertMessage(newAiMsg)
            conversationDao.touchConversation(conversationId, System.currentTimeMillis())
            Result.success(newAiMsg)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
