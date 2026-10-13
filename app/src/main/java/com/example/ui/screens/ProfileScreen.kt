package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.GritEmblem
import com.example.ui.components.GritHeaderLogo
import com.example.ui.components.GritPrimaryButton
import com.example.ui.theme.GritBackground
import com.example.ui.theme.GritBackgroundSecondary
import com.example.ui.theme.GritBorder
import com.example.ui.theme.GritCard
import com.example.ui.theme.GritError
import com.example.ui.theme.GritInput
import com.example.ui.theme.GritPrimaryAccent
import com.example.ui.theme.GritSecondaryAccent
import com.example.ui.theme.GritSilver
import com.example.ui.theme.GritTextMuted
import com.example.ui.theme.GritTextPrimary
import com.example.ui.theme.GritTextSecondary

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    onNavigateToHistory: () -> Unit,
    onSignIn: (String, String) -> Unit,
    onSignOut: () -> Unit,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSignInModal by remember { mutableStateOf(false) }
    var showAboutModal by remember { mutableStateOf(false) }
    var showClearDataConfirm by remember { mutableStateOf(false) }

    var loginName by remember { mutableStateOf("") }
    var loginEmail by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GritBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                ) {
                    GritHeaderLogo()

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Profile & Settings",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = GritSilver
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // User Identity Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(GritCard)
                            .border(1.dp, GritBorder, RoundedCornerShape(18.dp))
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GritEmblem(size = 64.dp)

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userProfile.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GritTextPrimary
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = userProfile.email,
                                    fontSize = 12.sp,
                                    color = GritTextSecondary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Plan Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GritPrimaryAccent.copy(alpha = 0.15f))
                                        .border(1.dp, GritPrimaryAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = userProfile.plan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GritPrimaryAccent
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Guest Usage Progress (if guest)
                    if (!userProfile.isSignedIn) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(GritBackgroundSecondary)
                                .border(1.dp, GritBorder, RoundedCornerShape(14.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Guest AI Messages",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = GritTextPrimary
                                    )
                                    Text(
                                        text = "${userProfile.guestMessagesUsed} / ${userProfile.guestMessageLimit}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GritPrimaryAccent
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LinearProgressIndicator(
                                    progress = {
                                        (userProfile.guestMessagesUsed.toFloat() / userProfile.guestMessageLimit).coerceIn(0f, 1f)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = GritPrimaryAccent,
                                    trackColor = GritBorder
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Sign in to unlock unlimited conversations and synced access.",
                                    fontSize = 11.sp,
                                    color = GritTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Optional GritCoach Pro Section Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF1B2330),
                                        Color(0xFF15191F)
                                    )
                                )
                            )
                            .border(1.dp, GritSecondaryAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Pro",
                                    tint = GritPrimaryAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GritCoach Pro Tier",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GritSilver
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Unlimited Gemini AI reasoning, custom habit routines, and priority token bandwidth.",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = GritTextSecondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            GritPrimaryButton(
                                text = if (userProfile.isSignedIn) "Account Active" else "Sign In / Connect Account",
                                onClick = {
                                    if (!userProfile.isSignedIn) {
                                        showSignInModal = true
                                    } else {
                                        Toast.makeText(context, "Pro plan active on this device", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.height(44.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "PREFERENCES & SUPPORT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = GritTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Menu Options
                    ProfileMenuItem(
                        icon = Icons.Default.History,
                        title = "Chat History",
                        subtitle = "View and manage past coaching sessions",
                        onClick = onNavigateToHistory
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.Psychology,
                        title = "AI Model Engine",
                        subtitle = "Google Gemini 3.5 Flash",
                        onClick = {
                            Toast.makeText(context, "Powered by Gemini AI (Configure key in AI Studio Secrets)", Toast.LENGTH_LONG).show()
                        }
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.HelpOutline,
                        title = "Help & Support",
                        subtitle = "FAQs and coaching principles",
                        onClick = {
                            Toast.makeText(context, "Contact: support@gritcoach.ai", Toast.LENGTH_SHORT).show()
                        }
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "About GritCoach",
                        subtitle = "Version 1.0 • Philosophy & Architecture",
                        onClick = { showAboutModal = true }
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.DeleteSweep,
                        title = "Clear All Local Data",
                        subtitle = "Erase all chat sessions and reset device cache",
                        tint = GritError,
                        onClick = { showClearDataConfirm = true }
                    )

                    if (userProfile.isSignedIn) {
                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.Logout,
                            title = "Sign Out",
                            subtitle = "Return to guest mode",
                            tint = GritTextMuted,
                            onClick = onSignOut
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Sign In / Register Dialog
        if (showSignInModal) {
            AlertDialog(
                onDismissRequest = { showSignInModal = false },
                containerColor = GritCard,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GritEmblem(size = 32.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Connect Account", color = GritSilver, fontFamily = FontFamily.Serif, fontSize = 18.sp)
                    }
                },
                text = {
                    Column {
                        Text(
                            "Enter your profile details to persist conversations across sessions.",
                            color = GritTextSecondary,
                            fontSize = 13.sp
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
                                value = loginName,
                                onValueChange = { loginName = it },
                                textStyle = TextStyle(color = GritTextPrimary, fontSize = 13.sp),
                                cursorBrush = SolidColor(GritPrimaryAccent),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signin_name_input")
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
                                value = loginEmail,
                                onValueChange = { loginEmail = it },
                                textStyle = TextStyle(color = GritTextPrimary, fontSize = 13.sp),
                                cursorBrush = SolidColor(GritPrimaryAccent),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signin_email_input")
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onSignIn(loginName, loginEmail)
                            showSignInModal = false
                        }
                    ) {
                        Text("Continue", color = GritPrimaryAccent)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSignInModal = false }) {
                        Text("Cancel", color = GritTextMuted)
                    }
                }
            )
        }

        // About GritCoach Modal
        if (showAboutModal) {
            AlertDialog(
                onDismissRequest = { showAboutModal = false },
                containerColor = GritCard,
                title = { Text("About GritCoach", color = GritSilver, fontFamily = FontFamily.Serif) },
                text = {
                    Column {
                        Text(
                            "“YOUR AI. YOUR GROWTH.”",
                            fontWeight = FontWeight.Bold,
                            color = GritPrimaryAccent,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "GritCoach is engineered with a dark graphite aesthetic and cool icy-blue accents. Designed to replace dopamine-driven feeds with focused mental clarity, accountability, and high-impact action blueprints.",
                            color = GritTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Persistence: Room SQLite Local Engine\nAI Engine: Gemini 3.5 Flash / Resilience Fallback",
                            color = GritTextMuted,
                            fontSize = 11.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutModal = false }) {
                        Text("Close", color = GritPrimaryAccent)
                    }
                }
            )
        }

        // Clear All Data Confirmation Modal
        if (showClearDataConfirm) {
            AlertDialog(
                onDismissRequest = { showClearDataConfirm = false },
                containerColor = GritCard,
                title = { Text("Erase All Data?", color = GritSilver, fontFamily = FontFamily.Serif) },
                text = {
                    Text(
                        "This will delete all stored conversations, messages, and preferences from this device.",
                        color = GritTextSecondary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onClearAllData()
                            showClearDataConfirm = false
                            Toast.makeText(context, "Local data cleared", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Erase", color = GritError)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDataConfirm = false }) {
                        Text("Cancel", color = GritTextMuted)
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = GritPrimaryAccent
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(GritCard)
            .border(1.dp, GritBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GritBackgroundSecondary)
                    .border(1.dp, GritBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GritTextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = GritTextMuted
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = GritTextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
