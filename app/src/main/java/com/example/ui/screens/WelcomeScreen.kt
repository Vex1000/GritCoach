package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GritEmblem
import com.example.ui.components.GritPrimaryButton
import com.example.ui.components.GritSecondaryButton
import com.example.ui.theme.GritBackground
import com.example.ui.theme.GritSilver
import com.example.ui.theme.GritTextMuted
import com.example.ui.theme.GritTextPrimary
import com.example.ui.theme.GritTextSecondary

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onLogIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GritBackground)
    ) {
        // Cinematic dark mountain background
        Image(
            painter = painterResource(id = R.drawable.img_dark_mountains),
            contentDescription = "Cinematic Dark Mountain Landscape",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay to guarantee high contrast and seamless fade into #101114
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GritBackground.copy(alpha = 0.85f),
                            GritBackground.copy(alpha = 0.45f),
                            GritBackground.copy(alpha = 0.88f),
                            GritBackground
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 28.dp, vertical = 20.dp)
                .widthIn(max = 540.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Center Branding Block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                GritEmblem(size = 96.dp)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "GritCoach",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    letterSpacing = 0.5.sp,
                    color = GritSilver
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "YOUR AI. YOUR GROWTH.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    color = GritTextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Your personal AI coach for clarity, discipline, productivity, learning, and personal growth.",
                    color = GritTextSecondary,
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // Bottom Actions & Brand Promise
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                var isStarting by remember { mutableStateOf(false) }

                GritPrimaryButton(
                    text = "Get Started",
                    onClick = {
                        if (!isStarting) {
                            isStarting = true
                            onGetStarted()
                        }
                    },
                    enabled = !isStarting,
                    testTag = "welcome_get_started"
                )

                Spacer(modifier = Modifier.height(12.dp))

                GritSecondaryButton(
                    text = "Log In",
                    onClick = {
                        if (!isStarting) {
                            onLogIn()
                        }
                    },
                    testTag = "welcome_log_in"
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Build discipline. Find clarity.\nBecome your best self.",
                    color = GritTextMuted,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.3.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
