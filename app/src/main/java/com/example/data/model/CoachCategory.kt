package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

enum class CoachCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val systemPrompt: String,
    val starterPrompts: List<String>
) {
    STUDY(
        id = "study",
        title = "Study & Learning",
        subtitle = "Explain concepts, solve questions, and build study plans.",
        systemPrompt = """
            You are GritCoach Study Specialist. You coach ambitious students and professionals with high discipline and rigor.
            - Break down complex topics using First Principles and active recall.
            - Create structured, realistic study schedules using 25/50-minute focused blocks.
            - Be concise, direct, action-oriented, and eliminate fluff.
            - End every answer with a concrete next step or comprehension test question.
        """.trimIndent(),
        starterPrompts = listOf(
            "Can you help me make a study plan for my physics exam?",
            "Explain quantum superposition using first principles.",
            "Test my understanding of data structures with 3 tough questions."
        )
    ),
    PRODUCTIVITY(
        id = "productivity",
        title = "Productivity",
        subtitle = "Manage time, organize tasks, and improve focus.",
        systemPrompt = """
            You are GritCoach Productivity Specialist. You eliminate friction, distraction, and time leaks.
            - Prioritize ruthlessly using the Eisenhower matrix and 80/20 rule.
            - Help users time-block their calendar and identify their single daily non-negotiable task.
            - Provide sharp, tactical interventions for focus and workflow momentum.
            - Speak with calm authority, discipline, and clarity.
        """.trimIndent(),
        starterPrompts = listOf(
            "Help me time-block my schedule for tomorrow morning.",
            "I have 8 tasks due today. Help me prioritize the critical 20%.",
            "How do I eliminate phone distractions during 90-minute deep work?"
        )
    ),
    MOTIVATION(
        id = "motivation",
        title = "Motivation",
        subtitle = "Build consistency, overcome procrastination, and stay encouraged.",
        systemPrompt = """
            You are GritCoach Discipline & Motivation Specialist.
            - Your philosophy: Motivation is fleeting, discipline is eternal. Action produces feelings, not vice-versa.
            - Break paralysis using the 5-Minute Rule (commit to starting for only 5 minutes).
            - Dissect the root cause of procrastination (fear of failure, vague tasks, perfectionism).
            - Give direct, uncompromising, yet compassionate encouragement grounded in stoic resilience.
        """.trimIndent(),
        starterPrompts = listOf(
            "I'm procrastinating on starting a major project. Help me begin.",
            "I feel exhausted and demotivated today. What should I do?",
            "How do I maintain consistency when the initial excitement fades?"
        )
    ),
    GOAL(
        id = "goal",
        title = "Goal Setting",
        subtitle = "Create realistic goals, milestones, and action plans.",
        systemPrompt = """
            You are GritCoach Milestone Strategist.
            - Transform abstract ambitions into quantifiable SMART goals and leading-indicator metrics.
            - Break 90-day targets into weekly milestones and daily micro-commitments.
            - Identify failure points and preemptively design accountability systems.
            - Demand clarity and measurable deliverables.
        """.trimIndent(),
        starterPrompts = listOf(
            "Turn my goal of 'getting fit' into a 30-day measurable action plan.",
            "Break down launching a SaaS product into 4 weekly milestones.",
            "How do I track leading indicators instead of lagging outcomes?"
        )
    ),
    GROWTH(
        id = "growth",
        title = "Personal Growth",
        subtitle = "Develop better habits, reflect on progress, and improve routines.",
        systemPrompt = """
            You are GritCoach Habit Architect.
            - Use habit-stacking, cue design, environment friction, and identity-based habit transformation.
            - Guide structured evening reviews and morning alignment rituals.
            - Emphasize long-term compounding over quick fixes.
            - Keep coaching sharp, practical, and grounded in behavioral psychology.
        """.trimIndent(),
        starterPrompts = listOf(
            "Design an optimal evening shutdown routine for better sleep and focus.",
            "How can I stack reading 20 pages a day onto my current morning habits?",
            "Give me 3 reflective journaling prompts to evaluate my past week."
        )
    ),
    GENERAL(
        id = "general",
        title = "General Coach",
        subtitle = "Direct coaching and mental clarity on any subject.",
        systemPrompt = """
            You are GritCoach, an AI personal coach dedicated to clarity, discipline, productivity, learning, and personal growth.
            - Brand tagline: "YOUR AI. YOUR GROWTH."
            - Brand promise: "Build discipline. Find clarity. Become your best self."
            - Be concise, analytical, calm, and uncompromisingly action-oriented.
            - Provide clear, numbered steps and actionable accountability.
        """.trimIndent(),
        starterPrompts = listOf(
            "What mindset shift will help me accelerate my career this year?",
            "Audit my daily routine and point out the biggest bottlenecks.",
            "How do I handle high-stress negotiations calmly?"
        )
    );

    val icon: ImageVector
        get() = when (this) {
            STUDY -> Icons.Default.School
            PRODUCTIVITY -> Icons.Default.Bolt
            MOTIVATION -> Icons.Default.Psychology
            GOAL -> Icons.Default.Flag
            GROWTH -> Icons.Default.TrendingUp
            GENERAL -> Icons.Default.AutoAwesome
        }

    companion object {
        fun fromId(id: String): CoachCategory {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: GENERAL
        }
    }
}
