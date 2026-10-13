package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CoachCategory
import com.example.ui.components.GritEmblem
import com.example.ui.components.GritHeaderLogo
import com.example.ui.theme.GritBackground
import com.example.ui.theme.GritBackgroundSecondary
import com.example.ui.theme.GritBorder
import com.example.ui.theme.GritCard
import com.example.ui.theme.GritInput
import com.example.ui.theme.GritPrimaryAccent
import com.example.ui.theme.GritSecondaryAccent
import com.example.ui.theme.GritSilver
import com.example.ui.theme.GritTextMuted
import com.example.ui.theme.GritTextPrimary
import com.example.ui.theme.GritTextSecondary

@Composable
fun HomeScreen(
    userName: String,
    onCategoryClick: (CoachCategory) -> Unit,
    onStartPrompt: (String) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var promptInput by remember { mutableStateOf("") }

    val categories = listOf(
        CoachCategory.STUDY,
        CoachCategory.PRODUCTIVITY,
        CoachCategory.MOTIVATION,
        CoachCategory.GOAL,
        CoachCategory.GROWTH
    )

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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                ) {
                    // Top App Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GritHeaderLogo()

                        // Profile Icon Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GritCard)
                                .border(1.dp, GritBorder, CircleShape)
                                .clickable(onClick = onProfileClick)
                                .testTag("home_profile_icon"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = GritSilver,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .padding(top = 20.dp)
                ) {
                    // Main Heading
                    Text(
                        text = "Welcome back,\nLet's make today count.",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 26.sp,
                        lineHeight = 34.sp,
                        color = GritSilver
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Prominent Rounded AI Input Field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(GritInput)
                            .border(1.dp, GritBorder, RoundedCornerShape(28.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (promptInput.isEmpty()) {
                                    Text(
                                        text = "What would you like help with today?",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = GritTextMuted,
                                        fontSize = 14.sp
                                    )
                                }
                                BasicTextField(
                                    value = promptInput,
                                    onValueChange = { promptInput = it },
                                    textStyle = TextStyle(
                                        color = GritTextPrimary,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.SansSerif
                                    ),
                                    cursorBrush = SolidColor(GritPrimaryAccent),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                    keyboardActions = KeyboardActions(
                                        onSend = {
                                            if (promptInput.isNotBlank()) {
                                                onStartPrompt(promptInput)
                                                promptInput = ""
                                            }
                                        }
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("home_prompt_input")
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Circular icy-blue Send button
                            val gradient = Brush.linearGradient(
                                colors = listOf(GritPrimaryAccent, GritSecondaryAccent)
                            )
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(gradient)
                                    .clickable {
                                        if (promptInput.isNotBlank()) {
                                            onStartPrompt(promptInput)
                                            promptInput = ""
                                        }
                                    }
                                    .testTag("home_send_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send Prompt",
                                    tint = GritBackground,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Section Heading
                    Text(
                        text = "COACHING SPECIALIZATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = GritTextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Two-column category cards
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Row 1: Study & Learning + Productivity
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CategoryCard(
                            category = CoachCategory.STUDY,
                            onClick = { onCategoryClick(CoachCategory.STUDY) },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryCard(
                            category = CoachCategory.PRODUCTIVITY,
                            onClick = { onCategoryClick(CoachCategory.PRODUCTIVITY) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: Motivation + Goal Setting
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CategoryCard(
                            category = CoachCategory.MOTIVATION,
                            onClick = { onCategoryClick(CoachCategory.MOTIVATION) },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryCard(
                            category = CoachCategory.GOAL,
                            onClick = { onCategoryClick(CoachCategory.GOAL) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 3: Personal Growth (Full width or paired)
                    CategoryCard(
                        category = CoachCategory.GROWTH,
                        onClick = { onCategoryClick(CoachCategory.GROWTH) },
                        modifier = Modifier.fillMaxWidth(),
                        isWide = true
                    )
                }
            }

            // "Your Daily Focus" mountain card
            item {
                Spacer(modifier = Modifier.height(26.dp))
                DailyFocusCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: CoachCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isWide: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GritCard)
            .border(1.dp, GritBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("category_card_${category.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GritBackgroundSecondary)
                    .border(1.dp, GritBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = GritPrimaryAccent,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = category.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = GritTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = category.subtitle,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = GritTextSecondary,
                maxLines = if (isWide) 2 else 2
            )
        }
    }
}

@Composable
fun DailyFocusCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .height(130.dp)
            .border(1.dp, GritBorder, RoundedCornerShape(18.dp))
    ) {
        // Dark mountain background
        Image(
            painter = painterResource(id = R.drawable.img_dark_mountains),
            contentDescription = "Daily Focus Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x99101114),
                            Color(0xEE101114)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "YOUR DAILY FOCUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = GritPrimaryAccent
                )

                Text(
                    text = "Day 1",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = GritSilver.copy(alpha = 0.7f)
                )
            }

            Text(
                text = "“Discipline today, freedom tomorrow.”",
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.2.sp,
                color = GritSilver
            )
        }
    }
}
