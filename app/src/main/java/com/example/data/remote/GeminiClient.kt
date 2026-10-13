package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.CoachCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateCoachingResponse(
        prompt: String,
        category: CoachCategory,
        conversationHistory: List<Pair<String, String>> // role ("user" / "model"), content
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If API key is present and not the placeholder, call real Gemini API
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "YOUR_API_KEY") {
            try {
                val responseText = callGeminiApi(apiKey, prompt, category, conversationHistory)
                if (responseText.isNotBlank()) {
                    return@withContext responseText
                }
            } catch (e: Exception) {
                Log.e("GeminiClient", "Gemini API call failed, falling back to local coach engine", e)
            }
        }

        // Fallback intelligent coaching engine
        return@withContext generateLocalGritCoaching(prompt, category)
    }

    private fun callGeminiApi(
        apiKey: String,
        prompt: String,
        category: CoachCategory,
        history: List<Pair<String, String>>
    ): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val contentsArray = JSONArray()

        // Include recent history (up to last 6 turns for optimal context)
        val recentHistory = history.takeLast(6)
        for ((role, content) in recentHistory) {
            val partObj = JSONObject().put("text", content)
            val partsArray = JSONArray().put(partObj)
            val contentObj = JSONObject()
                .put("role", if (role == "model") "model" else "user")
                .put("parts", partsArray)
            contentsArray.put(contentObj)
        }

        // Add current prompt
        val currentPart = JSONObject().put("text", prompt)
        val currentPartsArray = JSONArray().put(currentPart)
        val currentContentObj = JSONObject()
            .put("role", "user")
            .put("parts", currentPartsArray)
        contentsArray.put(currentContentObj)

        val rootJson = JSONObject().apply {
            put("contents", contentsArray)

            val systemInstruction = JSONObject().apply {
                val part = JSONObject().put("text", category.systemPrompt)
                put("parts", JSONArray().put(part))
            }
            put("systemInstruction", systemInstruction)

            val generationConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
                put("maxOutputTokens", 1200)
            }
            put("generationConfig", generationConfig)
        }

        val body = rootJson.toString().toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                throw IOException("Gemini API error code ${response.code}: $errBody")
            }
            val resBody = response.body?.string() ?: throw IOException("Empty response body")
            val json = JSONObject(resBody)
            val candidates = json.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val contentObj = firstCandidate.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return parts.getJSONObject(0).optString("text", "").trim()
                }
            }
            return ""
        }
    }

    private fun generateLocalGritCoaching(prompt: String, category: CoachCategory): String {
        val clean = prompt.trim()
        val lower = clean.lowercase()

        return when {
            lower.contains("study plan") || lower.contains("exam") || lower.contains("physics") -> {
                """
                Here is your high-discipline study blueprint:

                **Phase 1: Diagnostic Assessment (Day 1)**
                - Audit the exam syllabus against your current retention.
                - Mark chapters into: High Leverage (Red), Needs Practice (Yellow), Mastered (Green).

                **Phase 2: Active Recall Sprints (Days 2–5)**
                - Execute 3× 50-minute Pomodoro blocks daily. No multitasking, zero notifications.
                - Dedicate block 1 strictly to first principles & formulas.
                - Dedicate blocks 2 & 3 to hard past-paper problem solving under timed conditions.

                **Phase 3: Error Log & Simulation (Day 6)**
                - Maintain a ruthless "Mistake Matrix" documenting every flawed question and the exact blindspot.
                - Complete 1 full mock exam with exam-day constraints.

                **Next Immediate Step:** Tell me your exam date and the 3 chapters causing the greatest friction right now.
                """.trimIndent()
            }
            lower.contains("time-block") || lower.contains("schedule") || lower.contains("prioritize") -> {
                """
                Here is your Focus Architecture:

                **1. The Non-Negotiable Core (08:30 – 11:30)**
                - Reserve the first 3 hours of peak cognitive energy exclusively for your highest leverage deliverable.
                - Keep phone in another room or on Airplane Mode.

                **2. Administrative Batching (13:30 – 15:00)**
                - Group communication, emails, scheduling, and micro-tasks into a single compressed sprint.
                - Prevent reactive context-switching during deep work blocks.

                **3. Secondary Sprint & Review (15:30 – 17:00)**
                - Complete 1 tactical milestone.
                - Perform a 10-minute end-of-day shutdown ritual: log wins, prep tomorrow's primary objective.

                **Discipline Rule:** If a task takes under 2 minutes, execute it immediately. If it takes longer, schedule it into a defined block.
                """.trimIndent()
            }
            lower.contains("procrastinat") || lower.contains("lazy") || lower.contains("demotivated") || lower.contains("exhausted") -> {
                """
                Motivation is a fickle byproduct; discipline is a foundational habit.

                **The 5-Minute Activation Protocol:**
                1. **Strip Down the Task:** Don't think about finishing the whole project. Target only opening the document and typing the first 3 sentences.
                2. **Lower Cognitive Resistance:** Commit to working for exactly 300 seconds. If resistance persists after 5 minutes, you may step away. 92% of the time, momentum takes over.
                3. **Neutralize Physical Friction:** Clear your desk, set a physical countdown timer, and sit upright.

                Action generates emotion, never wait for emotion to generate action. Start your 5-minute timer right now.
                """.trimIndent()
            }
            lower.contains("goal") || lower.contains("habit") || lower.contains("routine") -> {
                """
                Strategic Milestone Breakdown:

                **1. Define the Leading Metric**
                - Lagging metric: The end outcome you cannot control directly.
                - Leading metric: The daily action you have 100% control over (e.g., 45 minutes of writing, 5 calls logged).

                **2. Environmental Friction Design**
                - Good habits: Reduce friction to zero (prepare materials the night before).
                - Bad habits: Increase friction exponentially (put distractions behind locks/passwords).

                **3. Accountability Matrix**
                - Review this goal every Sunday evening. A goal without weekly review is merely a wish.

                What is the single leading indicator you will measure every day this week?
                """.trimIndent()
            }
            else -> {
                when (category) {
                    CoachCategory.STUDY -> """
                        Understood. Let's tackle this systematically:
                        
                        1. **Deconstruct the Core Concept**: What is the root question or obstacle here?
                        2. **First Principles Translation**: Translate this into practical terms you could explain to a 10-year-old.
                        3. **Friction Point**: Where did your understanding specifically stall?
                        
                        Give me the exact problem or question, and we will solve it step-by-step.
                    """.trimIndent()
                    CoachCategory.PRODUCTIVITY -> """
                        Let's optimize this immediately:
                        
                        - What is the single outcome required by the end of today?
                        - What low-value busywork is currently consuming your focus?
                        - How can we eliminate 50% of the non-essential steps?
                        
                        Identify your single highest-priority bottleneck right now.
                    """.trimIndent()
                    CoachCategory.MOTIVATION -> """
                        Remember the core standard: Discipline today, freedom tomorrow.
                        
                        When resistance appears, it indicates the threshold of growth. Do not negotiate with inner weakness.
                        
                        Identify the smallest immediate action you can complete in the next 120 seconds. Take that step now.
                    """.trimIndent()
                    CoachCategory.GOAL -> """
                        Clarity is power. Let's quantify this goal:
                        
                        - **Objective**: Exactly what must be accomplished?
                        - **Deadline**: What is the non-negotiable target date?
                        - **Daily Cadence**: What daily input guarantees this output?
                        
                        Share your timeline and we will structure the roadmap.
                    """.trimIndent()
                    CoachCategory.GROWTH -> """
                        Personal growth is compounded consistency. Small improvements executed daily yield exponential mastery.
                        
                        Let's examine your current system rather than your intentions. What routine supported or undermined your progress this past week?
                    """.trimIndent()
                    CoachCategory.GENERAL -> """
                        I hear you. Let's bring absolute clarity and structure to this:
                        
                        1. **Current Reality**: Clarify the exact situation without emotional distortion.
                        2. **Strategic Objective**: Define what an optimal resolution looks like.
                        3. **Execution Plan**: Outline 2 to 3 direct actions you can take within the next 24 hours.
                        
                        Tell me what outcome you want to produce.
                    """.trimIndent()
                }
            }
        }
    }
}
