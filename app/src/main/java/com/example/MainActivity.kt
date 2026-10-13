package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CoachCategory
import com.example.ui.components.GritBottomNav
import com.example.ui.components.GritEmblem
import com.example.ui.components.NavTab
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CinematicOnboardingScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.GritBackground
import com.example.ui.theme.GritBorder
import com.example.ui.theme.GritCard
import com.example.ui.theme.GritCoachTheme
import com.example.ui.theme.GritInput
import com.example.ui.theme.GritPrimaryAccent
import com.example.ui.theme.GritSilver
import com.example.ui.theme.GritTextMuted
import com.example.ui.theme.GritTextPrimary
import com.example.ui.theme.GritTextSecondary
import com.example.ui.viewmodel.GritCoachViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: GritCoachViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GritCoachTheme {
                GritCoachApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GritCoachApp(viewModel: GritCoachViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val activeConversation by viewModel.activeConversation.collectAsStateWithLifecycle()
    val activeCategory by viewModel.activeCategory.collectAsStateWithLifecycle()
    val activeMessages by viewModel.activeMessages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val showGuestLimitDialog by viewModel.showGuestLimitDialog.collectAsStateWithLifecycle()

    var guestLoginName by remember { mutableStateOf("") }
    var guestLoginEmail by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GritBackground)
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                if (initialState is Screen.OnboardingTransition && targetState is Screen.Main) {
                    (fadeIn(animationSpec = tween(450, easing = FastOutSlowInEasing)) +
                            slideInVertically(animationSpec = tween(450, easing = FastOutSlowInEasing)) { it / 15 })
                        .togetherWith(fadeOut(animationSpec = tween(350, easing = FastOutSlowInEasing)))
                } else if (initialState is Screen.Welcome && targetState is Screen.OnboardingTransition) {
                    fadeIn(animationSpec = tween(350, easing = FastOutSlowInEasing))
                        .togetherWith(fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)))
                } else {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                }
            },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                is Screen.Welcome -> {
                    WelcomeScreen(
                        onGetStarted = { viewModel.navigateTo(Screen.OnboardingTransition) },
                        onLogIn = { viewModel.navigateTo(Screen.Main(NavTab.PROFILE)) }
                    )
                }

                is Screen.OnboardingTransition -> {
                    CinematicOnboardingScreen(
                        onTransitionComplete = {
                            viewModel.navigateTo(Screen.Main(NavTab.HOME))
                        }
                    )
                }

                is Screen.Main -> {
                    BackHandler(enabled = screen.tab != NavTab.HOME) {
                        viewModel.navigateTo(Screen.Main(NavTab.HOME))
                    }

                    Scaffold(
                        bottomBar = {
                            GritBottomNav(
                                currentTab = screen.tab,
                                onTabSelected = { tab ->
                                    viewModel.navigateTo(Screen.Main(tab))
                                }
                            )
                        },
                        containerColor = GritBackground
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        ) {
                            when (screen.tab) {
                                NavTab.HOME -> {
                                    HomeScreen(
                                        userName = userProfile.name,
                                        onCategoryClick = { category ->
                                            viewModel.startNewChat(category)
                                        },
                                        onStartPrompt = { prompt ->
                                            viewModel.startNewChat(CoachCategory.GENERAL, prompt)
                                        },
                                        onProfileClick = {
                                            viewModel.navigateTo(Screen.Main(NavTab.PROFILE))
                                        }
                                    )
                                }

                                NavTab.EXPLORE -> {
                                    ExploreScreen(
                                        onSelectCategory = { category ->
                                            viewModel.startNewChat(category)
                                        }
                                    )
                                }

                                NavTab.HISTORY -> {
                                    HistoryScreen(
                                        conversations = conversations,
                                        searchQuery = searchQuery,
                                        onSearchQueryChange = { query ->
                                            viewModel.setSearchQuery(query)
                                        },
                                        onOpenConversation = { id ->
                                            viewModel.openConversation(id)
                                        },
                                        onRenameConversation = { id, newTitle ->
                                            viewModel.renameConversation(id, newTitle)
                                        },
                                        onDeleteConversation = { id ->
                                            viewModel.deleteConversation(id)
                                        },
                                        onStartNewChat = {
                                            viewModel.startNewChat(CoachCategory.GENERAL)
                                        }
                                    )
                                }

                                NavTab.PROFILE -> {
                                    ProfileScreen(
                                        userProfile = userProfile,
                                        onNavigateToHistory = {
                                            viewModel.navigateTo(Screen.Main(NavTab.HISTORY))
                                        },
                                        onSignIn = { name, email ->
                                            viewModel.signInAsUser(name, email)
                                        },
                                        onSignOut = {
                                            viewModel.signOut()
                                        },
                                        onClearAllData = {
                                            viewModel.clearAllConversations()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                is Screen.Chat -> {
                    ChatScreen(
                        conversation = activeConversation,
                        category = activeCategory,
                        messages = activeMessages,
                        isGenerating = isGenerating,
                        errorMessage = errorMessage,
                        onSendMessage = { prompt ->
                            viewModel.sendMessage(prompt)
                        },
                        onRetry = {
                            viewModel.retryLastMessage()
                        },
                        onNewChat = {
                            viewModel.startNewChat(activeCategory)
                        },
                        onBack = {
                            viewModel.navigateTo(Screen.Main(NavTab.HOME))
                        }
                    )
                }
            }
        }

        // Guest Limit Reached Sign-Up Prompt Dialog
        if (showGuestLimitDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissGuestLimitDialog() },
                containerColor = GritCard,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GritEmblem(size = 32.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Guest Limit Reached",
                            color = GritSilver,
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "You have utilized your free guest messages. Sign in with your name and email to continue with unlimited AI coaching and persistent history.",
                            color = GritTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Name", fontSize = 11.sp, color = GritTextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GritInput)
                                .border(1.dp, GritBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            BasicTextField(
                                value = guestLoginName,
                                onValueChange = { guestLoginName = it },
                                textStyle = TextStyle(color = GritTextPrimary, fontSize = 13.sp),
                                cursorBrush = SolidColor(GritPrimaryAccent),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("guest_limit_name_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Email", fontSize = 11.sp, color = GritTextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GritInput)
                                .border(1.dp, GritBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            BasicTextField(
                                value = guestLoginEmail,
                                onValueChange = { guestLoginEmail = it },
                                textStyle = TextStyle(color = GritTextPrimary, fontSize = 13.sp),
                                cursorBrush = SolidColor(GritPrimaryAccent),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("guest_limit_email_input")
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.signInAsUser(guestLoginName, guestLoginEmail)
                        }
                    ) {
                        Text("Sign In & Continue", color = GritPrimaryAccent)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissGuestLimitDialog() }) {
                        Text("Later", color = GritTextMuted)
                    }
                }
            )
        }
    }
}
