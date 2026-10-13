package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GritBorder
import com.example.ui.theme.GritSilver
import com.example.ui.theme.GritTextMuted

@Composable
fun GritEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(1.dp, GritBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_grit_logo),
            contentDescription = "GritCoach Emblem",
            modifier = Modifier.size(size),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun GritWordmark(
    modifier: Modifier = Modifier,
    showTagline: Boolean = false,
    fontSize: Int = 22
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "GritCoach",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.sp,
            letterSpacing = 0.5.sp,
            color = GritSilver
        )
        if (showTagline) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "YOUR AI. YOUR GROWTH.",
                style = MaterialTheme.typography.labelSmall,
                color = GritTextMuted,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun GritHeaderLogo(
    modifier: Modifier = Modifier,
    onEmblemClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GritEmblem(size = 36.dp)
        Spacer(modifier = Modifier.width(10.dp))
        GritWordmark(fontSize = 19, showTagline = false)
    }
}
