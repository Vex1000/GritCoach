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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoachCategory
import com.example.data.model.ConversationEntity
import com.example.ui.components.GritHeaderLogo
import com.example.ui.components.GritPrimaryButton
import com.example.ui.theme.GritBackground
import com.example.ui.theme.GritBackgroundSecondary
import com.example.ui.theme.GritBorder
import com.example.ui.theme.GritCard
import com.example.ui.theme.GritError
import com.example.ui.theme.GritInput
import com.example.ui.theme.GritPrimaryAccent
import com.example.ui.theme.GritSilver
import com.example.ui.theme.GritTextMuted
import com.example.ui.theme.GritTextPrimary
import com.example.ui.theme.GritTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    conversations: List<ConversationEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onOpenConversation: (String) -> Unit,
    onRenameConversation: (String, String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onStartNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var renamingConversationId by remember { mutableStateOf<String?>(null) }
    var renameText by remember { mutableStateOf("") }
    var deletingConversationId by remember { mutableStateOf<String?>(null) }

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy • hh:mm a", Locale.getDefault()) }

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
                        text = "Chat History",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = GritSilver
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(GritInput)
                            .border(1.dp, GritBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = GritTextMuted,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search past conversations...",
                                        color = GritTextMuted,
                                        fontSize = 14.sp
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = onSearchQueryChange,
                                    textStyle = TextStyle(
                                        color = GritTextPrimary,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.SansSerif
                                    ),
                                    cursorBrush = SolidColor(GritPrimaryAccent),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("history_search_input")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            if (conversations.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp)
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GritCard)
                                .border(1.dp, GritBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "No history",
                                tint = GritTextMuted,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (searchQuery.isNotBlank()) "No matching conversations found" else "No coaching sessions yet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = GritSilver
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (searchQuery.isNotBlank()) "Try refining your search keyword." else "Start a coaching conversation to build your history.",
                            fontSize = 13.sp,
                            color = GritTextSecondary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        GritPrimaryButton(
                            text = "Start First Chat",
                            onClick = onStartNewChat,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            } else {
                items(conversations, key = { it.id }) { conv ->
                    val category = CoachCategory.fromId(conv.categoryId)
                    var menuExpanded by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp)
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(GritCard)
                            .border(1.dp, GritBorder, RoundedCornerShape(14.dp))
                            .clickable { onOpenConversation(conv.id) }
                            .padding(14.dp)
                            .testTag("history_item_${conv.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GritBackgroundSecondary)
                                    .border(1.dp, GritBorder, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = category.title,
                                    tint = GritPrimaryAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = conv.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GritTextPrimary,
                                    maxLines = 1
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = category.title,
                                        fontSize = 11.sp,
                                        color = GritPrimaryAccent,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = dateFormat.format(Date(conv.updatedAt)),
                                        fontSize = 11.sp,
                                        color = GritTextMuted
                                    )
                                }
                            }

                            Box {
                                IconButton(onClick = { menuExpanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Options",
                                        tint = GritTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false },
                                    modifier = Modifier
                                        .background(GritBackgroundSecondary)
                                        .border(1.dp, GritBorder)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Rename", color = GritTextPrimary) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = GritSilver, modifier = Modifier.size(16.dp))
                                        },
                                        onClick = {
                                            menuExpanded = false
                                            renameText = conv.title
                                            renamingConversationId = conv.id
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Delete", color = GritError) },
                                        leadingIcon = {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = GritError, modifier = Modifier.size(16.dp))
                                        },
                                        onClick = {
                                            menuExpanded = false
                                            deletingConversationId = conv.id
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Rename Dialog
        if (renamingConversationId != null) {
            AlertDialog(
                onDismissRequest = { renamingConversationId = null },
                containerColor = GritCard,
                title = { Text("Rename Conversation", color = GritSilver, fontFamily = FontFamily.Serif) },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GritInput)
                            .border(1.dp, GritBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = renameText,
                            onValueChange = { renameText = it },
                            textStyle = TextStyle(color = GritTextPrimary, fontSize = 14.sp),
                            cursorBrush = SolidColor(GritPrimaryAccent),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (renameText.isNotBlank()) {
                                onRenameConversation(renamingConversationId!!, renameText)
                                renamingConversationId = null
                            }
                        }
                    ) {
                        Text("Save", color = GritPrimaryAccent)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { renamingConversationId = null }) {
                        Text("Cancel", color = GritTextMuted)
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        if (deletingConversationId != null) {
            AlertDialog(
                onDismissRequest = { deletingConversationId = null },
                containerColor = GritCard,
                title = { Text("Delete Conversation?", color = GritSilver, fontFamily = FontFamily.Serif) },
                text = {
                    Text(
                        "Are you sure you want to permanently delete this coaching session? This action cannot be undone.",
                        color = GritTextSecondary,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteConversation(deletingConversationId!!)
                            deletingConversationId = null
                        }
                    ) {
                        Text("Delete", color = GritError)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deletingConversationId = null }) {
                        Text("Cancel", color = GritTextMuted)
                    }
                }
            )
        }
    }
}
