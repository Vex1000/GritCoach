package com.example.ui.screens

import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GritEmblem
import com.example.ui.theme.GritBackground
import com.example.ui.theme.GritPrimaryAccent
import com.example.ui.theme.GritSecondaryAccent
import com.example.ui.theme.GritSilver
import com.example.ui.theme.GritTextMuted
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class Particle(
    val initialX: Float, // 0f to 1f normalized
    val initialY: Float, // 0f to 1f normalized
    val radius: Float,
    val speed: Float,
    val baseAlpha: Float
)

@Composable
fun CinematicOnboardingScreen(
    onTransitionComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Intercept back button during transition
    BackHandler(enabled = true) { /* Consume */ }

    val context = LocalContext.current

    // Check system setting for reduced motion
    val isReducedMotion = remember {
        try {
            val scale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            )
            scale <= 0.05f
        } catch (_: Exception) {
            false
        }
    }

    val motivationalPhrases = remember {
        listOf(
            "Discipline starts now.",
            "Build your momentum.",
            "Become your strongest self."
        )
    }

    var phraseIndex by remember { mutableIntStateOf(0) }
    var hasCompleted by remember { mutableStateOf(false) }

    // Logo scale and alpha animations
    val logoScale = remember { Animatable(if (isReducedMotion) 1f else 0.85f) }
    val logoAlpha = remember { Animatable(if (isReducedMotion) 1f else 0f) }
    val progressAnim = remember { Animatable(0f) }

    // Floating particles state
    val particles = remember {
        val rand = Random(42)
        List(22) {
            Particle(
                initialX = rand.nextFloat(),
                initialY = rand.nextFloat(),
                radius = rand.nextFloat() * 2f + 1.2f,
                speed = rand.nextFloat() * 0.15f + 0.05f,
                baseAlpha = rand.nextFloat() * 0.45f + 0.2f
            )
        }
    }

    // Infinite transitions for glow, light sweep, and ambient floating
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val lightSweepOffset by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lightSweep"
    )

    // Master Animation Sequence Controller
    LaunchedEffect(Unit) {
        if (isReducedMotion) {
            // Respect reduced motion: short crisp sequence
            delay(300)
            phraseIndex = 1
            delay(300)
            phraseIndex = 2
            delay(400)
            if (!hasCompleted) {
                hasCompleted = true
                onTransitionComplete()
            }
            return@LaunchedEffect
        }

        // 1. Logo Scale and Fade In
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        if (!isReducedMotion) {
            logoScale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
            )
        }
    }

    // Progress line animation (runs over 2.7 seconds)
    LaunchedEffect(Unit) {
        val duration = if (isReducedMotion) 900 else 2650
        progressAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing)
        )
    }

    // Motivational phrase switching timeline (approx 2.6 - 2.8s total)
    LaunchedEffect(Unit) {
        if (!isReducedMotion) {
            delay(850)
            phraseIndex = 1
            delay(850)
            phraseIndex = 2
            delay(950)
            if (!hasCompleted) {
                hasCompleted = true
                onTransitionComplete()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GritBackground)
            .testTag("cinematic_onboarding_screen")
    ) {
        // Deep Black and Graphite radial background with subtle icy blue aura
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width * 0.5f, size.height * 0.42f)
            val glowRadius = size.width * 0.75f

            // Radial background glow behind logo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GritPrimaryAccent.copy(alpha = 0.16f * glowAlpha),
                        GritSecondaryAccent.copy(alpha = 0.08f * glowAlpha),
                        Color(0xFF15191F).copy(alpha = 0.7f),
                        GritBackground
                    ),
                    center = centerOffset,
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = centerOffset
            )

            // Soft diagonal light sweep
            if (!isReducedMotion) {
                val sweepX = size.width * lightSweepOffset
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            GritSilver.copy(alpha = 0.05f),
                            GritPrimaryAccent.copy(alpha = 0.09f),
                            Color.Transparent
                        ),
                        start = Offset(sweepX - 120f, 0f),
                        end = Offset(sweepX + 120f, size.height)
                    ),
                    start = Offset(sweepX - 120f, 0f),
                    end = Offset(sweepX + 120f, size.height),
                    strokeWidth = 240f
                )

                // Draw floating particles
                particles.forEach { p ->
                    val x = (p.initialX * size.width)
                    val y = (p.initialY * size.height - (lightSweepOffset * p.speed * size.height))
                    val wrappedY = ((y % size.height) + size.height) % size.height
                    drawCircle(
                        color = GritSilver.copy(alpha = p.baseAlpha * glowAlpha),
                        radius = p.radius,
                        center = Offset(x, wrappedY)
                    )
                }
            }
        }

        // Center Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Container with silver glow & icy-blue highlights
            Box(
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                // Subtle outer halo
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GritPrimaryAccent.copy(alpha = 0.28f * glowAlpha),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Silver G Emblem with North Star
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    GritSilver,
                                    GritPrimaryAccent.copy(alpha = 0.7f),
                                    GritSilver.copy(alpha = 0.4f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    GritEmblem(size = 92.dp)
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // GritCoach Serif Wordmark with tagline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(logoAlpha.value)
            ) {
                Text(
                    text = "GritCoach",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = 0.75.sp,
                    color = GritSilver
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "YOUR AI. YOUR GROWTH.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    color = GritTextMuted
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            // Elegant Loading Animation: Thin glowing progress track & pulsing silver dots
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(logoAlpha.value)
            ) {
                // Three animated silver dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        val dotAlpha = when ((phraseIndex + index) % 3) {
                            0 -> 0.9f
                            1 -> 0.45f
                            else -> 0.25f
                        }
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == 1) GritPrimaryAccent.copy(alpha = dotAlpha)
                                    else GritSilver.copy(alpha = dotAlpha)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Thin glowing progress bar
                Box(
                    modifier = Modifier
                        .size(width = 160.dp, height = 2.5.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF222830))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressAnim.value)
                            .height(2.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        GritSilver.copy(alpha = 0.5f),
                                        GritPrimaryAccent,
                                        GritSecondaryAccent
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Motivational Phrases with smooth crossfade
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = phraseIndex,
                    transitionSpec = {
                        if (isReducedMotion) {
                            fadeIn(tween(150)) togetherWith fadeOut(tween(150))
                        } else {
                            (fadeIn(animationSpec = tween(380, easing = FastOutSlowInEasing)) +
                                    slideInVertically(animationSpec = tween(380, easing = FastOutSlowInEasing)) { 18 })
                                .togetherWith(
                                    fadeOut(animationSpec = tween(260, easing = FastOutSlowInEasing)) +
                                            slideOutVertically(animationSpec = tween(260, easing = FastOutSlowInEasing)) { -14 }
                                )
                        }
                    },
                    label = "motivationalPhrase"
                ) { targetIdx ->
                    Text(
                        text = motivationalPhrases[targetIdx.coerceIn(0, motivationalPhrases.size - 1)],
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp,
                        letterSpacing = 0.35.sp,
                        color = GritSilver,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
