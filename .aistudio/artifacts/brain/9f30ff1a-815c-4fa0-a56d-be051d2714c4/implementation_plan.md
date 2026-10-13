# GritCoach — Premium Dark AI Coaching App

GritCoach is an AI personal coaching application engineered to help users cultivate discipline, study effectively, sharpen productivity, establish actionable goals, and overcome procrastination. Built with a dark graphite, silver, and icy-blue aesthetic inspired by high-end performance tools, it pairs instant guest access with Room local persistence, multi-mode coaching prompts, and real Gemini AI integration.

## User Review & Critical Decisions

> [!IMPORTANT]
> The architectural and design parameters confirmed during initial discovery are incorporated into this implementation plan:

- **Confirmed Decision 1 (Persistence)**: Local Room database with instant guest access and optional local profile/sign-in. Conversations and user preferences are persistently retained offline on the device without requiring mandatory initial sign-in.
- **Confirmed Decision 2 (Onboarding)**: Cinematic Welcome screen with immediate one-tap entry into coaching modes, getting the user to the core value proposition in under 3 seconds.
- **Confirmed Decision 3 (Coaching Tone)**: Disciplined, structured, and action-oriented tone featuring concise steps, accountability prompts, and clear milestones across all coaching specializations.

---

## 1. Overview & Core Concept

- **What It Does**: GritCoach delivers specialized, high-leverage coaching across 5 distinct domains (Study & Learning, Productivity, Motivation, Goal Setting, and Personal Growth) along with a General Coach mode. Users converse in real-time, receiving tailored, structured action plans with message copy, regeneration, search, and persistent conversation history.
- **Target Audience / Persona**: High-performing students, engineers, founders, and professionals who demand clarity, focus, and ruthless consistency without fluff.
- **Key Value**: Replaces scattered notes and generic chat bots with an intentional, focused coaching environment optimized for habit execution, deep work, and discipline.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Launch & Welcome Screen**: Cinematic dark mountain landscape with silver "G" emblem, "GritCoach" serif title, "YOUR AI. YOUR GROWTH." tagline, and two actions: "Get Started" (icy-blue pill) and "Log In" (subtle slate border).
2. **Home Dashboard**:
   - Top bar with silver GritCoach wordmark and profile avatar.
   - Welcoming heading: *"Welcome back, Let's make today count."*
   - Quick-action AI prompt input with circular icy-blue send button.
   - Two-column grid of coaching category cards with silver and icy-blue iconography (Study & Learning, Productivity, Motivation, Goal Setting, Personal Growth).
   - "Your Daily Focus" card featuring a mountain backdrop and the quote: *"Discipline today, freedom tomorrow."*
   - Bottom navigation bar: Home, Explore, History, Profile.
3. **Real AI Chat Interface**:
   - Header with back navigation, active coaching mode title, and "New Chat" icon.
   - Message scroll area featuring dark graphite bubbles (#1B1F24) with subtle slate borders (#303843), silver star avatars for the AI, distinct right-aligned user bubbles, copy message action, and regenerate response.
   - Typing indicator and resilient error state with retry button.
   - Bottom input bar with attachment icon, multi-line auto-expanding field, and circular icy-blue send button.
4. **Explore Screen**:
   - Dedicated category hub detailing each domain's coaching philosophy and quick-start prompts (e.g. "Create Physics Exam Cram Schedule", "Time-Block Tomorrow", "Overcome Procrastination Loop").
5. **Chat History Screen**:
   - Search bar for past sessions.
   - Conversation list items displaying title, timestamp, category badge, and preview text.
   - Actions to reopen, rename, or delete past chats.
6. **Profile & Settings Screen**:
   - Silver "G" avatar, membership badge (Free/Guest vs. GritCoach Pro), guest message counter, clear data, AI model settings, and sign-out/reset options.

### Visual Identity & Theme Tokens

- **Dominant Surfaces**:
  - Main Background: `#101114`
  - Secondary Background: `#15191F`
  - Card & Bubble Surface: `#1B1F24`
  - Input Container: `#111820`
  - Border Stroke: `#303843`
- **Accents & Highlights**:
  - Primary Accent: `#8BADEB` (Icy blue)
  - Secondary Accent: `#688BDE` (Deeper steel blue)
  - Silver Highlights: `#E5E9F2`
- **Typography & Copy**:
  - Main Text: `#F1F3F7`
  - Secondary Text: `#9AA4B3`
  - Muted Text: `#707B8B`
  - Font Styling: Serif styling for brand wordmarks and hero titles; clean Sans-Serif with crisp tracking for body and coaching transcripts.
- **Micro-Interactions**:
  - Subtle ripple states on category cards.
  - Smooth animated typing cursor with pulsing icy-blue dot.
  - Active tab pill indicator in bottom navigation with navigation bar window inset padding.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Architecture & Persistence via Room Database**:
  - *Chosen Approach*: Room 2.7.0 with Kotlin Symbol Processing (KSP) storing `ConversationEntity` and `MessageEntity`.
  - *Why*: Provides sub-millisecond local queries, works flawlessly offline or in airplane mode, allows instant zero-friction guest usage, and eliminates authentication barriers during initial user evaluation.
  - *Alternatives Considered*: Firebase Firestore (demands mandatory Google sign-in configuration upfront which adds friction for casual guest testing).
- **Decision 2: Hybrid AI Generation Engine**:
  - *Chosen Approach*: Direct Gemini API client using `gemini-3.5-flash` with robust fallback responses if `GEMINI_API_KEY` is not yet configured in AI Studio Secrets, ensuring the application remains interactive and responsive in all testing environments.
  - *Why*: Prevents dead screens or unhandled network crashes while providing real AI coaching when keys are provided.
- **Decision 3: Edge-to-Edge Navigation Bar Inset Handling**:
  - *Chosen Approach*: Full edge-to-edge support with explicit `WindowInsets.navigationBars` padding on the custom bottom bar and top safe-insets for the header.
  - *Why*: Eliminates system gesture pill overlapping and ensures full compliance with modern Android 15/16 display standards.

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                              GritCoach UI                              │
│  ┌─────────────┐ ┌───────────────┐ ┌───────────────┐ ┌──────────────┐ │
│  │   Welcome   │ │ Home Dashboard│ │ Explore Modes │ │ Profile/Auth │ │
│  └──────┬──────┘ └───────┬───────┘ └───────┬───────┘ └───────┬──────┘ │
│         │                │                 │                 │        │
│         └────────────────┴────────┬────────┴─────────────────┘        │
│                                   ▼                                    │
│                    ┌──────────────────────────────┐                   │
│                    │    AI Chat Screen & Engine   │                   │
│                    └──────────────┬───────────────┘                   │
└───────────────────────────────────┼────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        GritCoachViewModel                              │
│  - Active Conversation State     - Guest Message Tracker               │
│  - Coaching Category Prompts     - Search & Filter Query               │
└───────────────────┬───────────────────────────────┬────────────────────┘
                    │                               │
                    ▼                               ▼
┌──────────────────────────────────────┐  ┌──────────────────────────────┐
│        Room Local Repository         │  │     Gemini AI Repository     │
│  - AppDatabase                       │  │  - Gemini REST Service       │
│  - ConversationDao (Flow reactive)   │  │  - Discipline Prompt Injector│
│  - MessageDao                        │  │  - Offline Fallback Engine   │
└──────────────────────────────────────┘  └──────────────────────────────┘
```

### Data Model & Entities

```kotlin
// Conversation Entity
@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String, // "STUDY", "PRODUCTIVITY", "MOTIVATION", "GOAL", "GROWTH", "GENERAL"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Message Entity
@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
```

### Specialized Coaching Modes

Each mode injects a strict system instruction into the Gemini request:
1. **Study & Learning**: "You are GritCoach Study Specialist. Break down difficult concepts into First Principles, active recall drills, and realistic 25-minute Pomodoro sprints. Be direct, structured, and verify understanding."
2. **Productivity**: "You are GritCoach Productivity Specialist. Eliminate friction and time-leaks. Format recommendations as Eisenhower matrices, daily non-negotiables, and time-blocks. No fluff."
3. **Motivation**: "You are GritCoach Discipline & Motivation Specialist. Focus on stoic principles, the psychology of resistance, and overcoming paralysis through the 5-minute activation rule."
4. **Goal Setting**: "You are GritCoach Milestone Strategist. Transform vague dreams into SMART deliverables, leading indicator metrics, and weekly check-ins."
5. **Personal Growth**: "You are GritCoach Habit Architect. Use atomic habit stacking, cues, friction reduction, and daily reflection logs."
6. **General**: "You are GritCoach, an uncompromising, calm, and analytical personal mentor dedicated to clarity and relentless execution."
