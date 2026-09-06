package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.SavedSquadEntity
import com.example.data.local.UserProfileEntity
import com.example.ui.DraftViewModel
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCardElevated
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandRose
import com.example.ui.theme.BrandYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PersonalCabinetScreen(
    viewModel: DraftViewModel,
    modifier: Modifier = Modifier
) {
    val savedSquads by viewModel.savedSquads.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var notesText by remember(userProfile?.tacticalNotes) {
        mutableStateOf(userProfile?.tacticalNotes ?: "")
    }

    val profile = userProfile ?: UserProfileEntity()

    val avatarColors = listOf(BrandYellow, BrandCyan, BrandEmerald, Color(0xFFA855F7), BrandRose)
    val userAvatarColor = avatarColors.getOrElse(profile.avatarColorIndex) { BrandYellow }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrandDark)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. User Profile Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(userAvatarColor.copy(alpha = 0.2f))
                                .border(2.dp, userAvatarColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.managerName.take(2).uppercase(),
                                color = userAvatarColor,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = profile.managerName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF422006))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "DS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = BrandYellow
                                    )
                                }
                            }

                            Text(
                                text = "Команда: ${profile.teamName}",
                                fontSize = 12.sp,
                                color = BrandCyan,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = profile.division,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(BrandCardElevated)
                                .testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Редактировать профиль",
                                tint = BrandYellow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Bio / Motto
                    if (profile.bio.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandDark)
                                .border(1.dp, BrandBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "\"${profile.bio}\"",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }

                    // Stat Metrics in Profile
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ProfileStatBadge(
                            label = "СОХРАНЕНО",
                            value = "${savedSquads.size}",
                            color = BrandYellow,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatBadge(
                            label = "СПЕЦИАЛИЗАЦИЯ",
                            value = profile.favoriteRace,
                            color = BrandCyan,
                            modifier = Modifier.weight(1.3f)
                        )
                        ProfileStatBadge(
                            label = "ФАВОРИТ",
                            value = profile.favoriteRole,
                            color = BrandEmerald,
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }
            }
        }

        // 2. Saved Squads Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📁 СОХРАНЁННЫЕ СОСТАВЫ И ДРАФТЫ (${savedSquads.size})",
                    color = BrandYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Room SQLite",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Saved Squads List or Empty State
        if (savedSquads.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BrandCard),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = BrandYellow,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Пока нет сохранённых составов",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Соберите состав на вкладке «Ростер» и нажмите кнопку «Сохранить в ЛК».",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(savedSquads, key = { it.id }) { squad ->
                SavedSquadCard(
                    squad = squad,
                    onLoad = { viewModel.loadSquad(squad) },
                    onDelete = { viewModel.deleteSquad(squad.id) }
                )
            }
        }

        // 3. DS Tactical Strategy Notebook
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.NoteAdd, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
                            Text(
                                text = "БЛОКНОТ DS / ТАКТИЧЕСКИЕ ЗАМЕТКИ",
                                color = BrandEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.saveUserProfile(
                                    managerName = profile.managerName,
                                    teamName = profile.teamName,
                                    division = profile.division,
                                    avatarColorIndex = profile.avatarColorIndex,
                                    favoriteRace = profile.favoriteRace,
                                    favoriteRole = profile.favoriteRole,
                                    bio = profile.bio,
                                    notes = notesText
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandEmerald,
                                contentColor = BrandDark
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(3.dp))
                            Text("Сохранить", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        placeholder = {
                            Text(
                                text = "Записывайте заметки по этапам, рискам вееров, связкам спринтеров...",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = profile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, team, div, colorIdx, race, role, bio ->
                viewModel.saveUserProfile(
                    managerName = name,
                    teamName = team,
                    division = div,
                    avatarColorIndex = colorIdx,
                    favoriteRace = race,
                    favoriteRole = role,
                    bio = bio,
                    notes = notesText
                )
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
fun ProfileStatBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BrandDark)
            .border(1.dp, BrandBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 8.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SavedSquadCard(
    squad: SavedSquadEntity,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(squad.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BrandBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = BrandCard)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = squad.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "${squad.raceName} • $dateStr",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить состав",
                        tint = BrandRose,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(BrandDark)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Бюджет", fontSize = 9.sp, color = TextMuted)
                    Text(
                        "${squad.totalSpent} / ${squad.budgetLimit}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (squad.totalSpent > squad.budgetLimit) BrandRose else BrandCyan
                    )
                }
                Column {
                    Text("Ожидание E[Pts]", fontSize = 9.sp, color = TextMuted)
                    Text(
                        String.format("%.1f", squad.expectedPoints),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = BrandYellow
                    )
                }
                Button(
                    onClick = onLoad,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandYellow,
                        contentColor = BrandDark
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                    Text(" Загрузить", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (squad.notes.isNotBlank()) {
                Text(
                    text = "Заметка: ${squad.notes}",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    currentProfile: UserProfileEntity,
    onDismiss: () -> Unit,
    onSave: (name: String, team: String, div: String, colorIdx: Int, race: String, role: String, bio: String) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.managerName) }
    var team by remember { mutableStateOf(currentProfile.teamName) }
    var division by remember { mutableStateOf(currentProfile.division) }
    var colorIndex by remember { mutableStateOf(currentProfile.avatarColorIndex) }
    var favoriteRace by remember { mutableStateOf(currentProfile.favoriteRace) }
    var favoriteRole by remember { mutableStateOf(currentProfile.favoriteRole) }
    var bio by remember { mutableStateOf(currentProfile.bio) }

    val avatarColors = listOf(BrandYellow, BrandCyan, BrandEmerald, Color(0xFFA855F7), BrandRose)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, BrandBorder, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = BrandCard)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "👤 Редактировать профиль DS",
                    color = BrandYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя менеджера (DS)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = team,
                    onValueChange = { team = it },
                    label = { Text("Название команды") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = division,
                    onValueChange = { division = it },
                    label = { Text("Дивизион / Ранг") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Color choices
                Text(text = "Цвет эмблемы DS:", fontSize = 11.sp, color = TextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    avatarColors.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    2.dp,
                                    if (colorIndex == index) TextPrimary else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { colorIndex = index }
                        )
                    }
                }

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Девиз / Описание") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена", color = TextSecondary)
                    }
                    Button(
                        onClick = {
                            onSave(name, team, division, colorIndex, favoriteRace, favoriteRole, bio)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandYellow,
                            contentColor = BrandDark
                        )
                    ) {
                        Text("Сохранить", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
