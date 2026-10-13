package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoachCategory
import com.example.ui.components.GritHeaderLogo
import com.example.ui.theme.GritBackground
import com.example.ui.theme.GritBackgroundSecondary
import com.example.ui.theme.GritBorder
import com.example.ui.theme.GritCard
import com.example.ui.theme.GritPrimaryAccent
import com.example.ui.theme.GritSilver
import com.example.ui.theme.GritTextMuted
import com.example.ui.theme.GritTextPrimary
import com.example.ui.theme.GritTextSecondary

@Composable
fun ExploreScreen(
    onSelectCategory: (CoachCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf(
        CoachCategory.STUDY,
        CoachCategory.PRODUCTIVITY,
        CoachCategory.MOTIVATION,
        CoachCategory.GOAL,
        CoachCategory.GROWTH,
        CoachCategory.GENERAL
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                ) {
                    GritHeaderLogo()

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Explore",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = GritSilver
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Choose a coaching mode or start a general chat.",
                        fontSize = 14.sp,
                        color = GritTextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            items(modes, key = { it.id }) { mode ->
                ExploreModeCard(
                    category = mode,
                    onClick = { onSelectCategory(mode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ExploreModeCard(
    category: CoachCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GritCard)
            .border(1.dp, GritBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(18.dp)
            .testTag("explore_mode_${category.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GritBackgroundSecondary)
                    .border(1.dp, GritBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = GritPrimaryAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GritTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = category.subtitle,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = GritTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open mode",
                tint = GritTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
