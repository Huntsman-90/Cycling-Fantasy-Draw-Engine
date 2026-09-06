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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DraftMathEngine
import com.example.data.model.Rider
import com.example.ui.DraftUiState
import com.example.ui.DraftViewModel
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCardElevated
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandRose
import com.example.ui.theme.BrandYellow
import com.example.ui.theme.Price1200Color
import com.example.ui.theme.Price200Color
import com.example.ui.theme.Price400Color
import com.example.ui.theme.Price600Color
import com.example.ui.theme.Price800Color
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RosterScreen(
    viewModel: DraftViewModel,
    uiState: DraftUiState,
    onNavigateToRiders: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrandDark)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Warning Banner if Over Budget
        if (uiState.isOverBudget) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF450A0A)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = BrandRose
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Превышен лимит бюджета на ${uiState.totalSpent - uiState.currentRace.budget} кредитов!",
                                color = Color(0xFFFECDD3),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Состав невалиден по правилам лиги (Strict 1200 Cap).",
                                color = Color(0xFFFDA4AF),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Action Toolbar: Auto-Draft Stacks & Save/Export
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder)
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
                        Text(
                            text = "⚡ ГЕНЕРАТОРЫ СТЕКОВ (AUTO-DRAFT)",
                            color = BrandYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { viewModel.sortByTieBreak() },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(BrandCardElevated)
                                    .testTag("sort_tie_break_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = "Сортировать по тай-брейку",
                                    tint = BrandCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.clearRoster() },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(BrandCardElevated)
                                    .testTag("clear_roster_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Очистить состав",
                                    tint = BrandRose,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // 3 Stack Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { viewModel.autoDraftStack("3stars") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandCardElevated,
                                contentColor = BrandYellow
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("stack_3stars_button"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⭐ 3 Звезды", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("3×1200 + 6×400", fontSize = 9.sp, color = TextSecondary)
                            }
                        }

                        Button(
                            onClick = { viewModel.autoDraftStack("2stars") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandCardElevated,
                                contentColor = BrandCyan
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("stack_2stars_button"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⭐ 2 Звезды", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Баланс + глубина", fontSize = 9.sp, color = TextSecondary)
                            }
                        }

                        Button(
                            onClick = { viewModel.autoDraftStack("arbitrage") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandCardElevated,
                                contentColor = BrandEmerald
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("stack_arbitrage_button"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💎 Arbitrage", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Max ROI / Pts", fontSize = 9.sp, color = TextSecondary)
                            }
                        }
                    }

                    // Save & Export Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { viewModel.openSaveDialog(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandCardElevated,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_squad_button")
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Сохранить в ЛК", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { viewModel.openExportDialog(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandYellow,
                                contentColor = BrandDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("export_report_button")
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Отчёт #6 (MD)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section Title: 9 Starters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ОСНОВНОЙ РОСТЕР (1° — 9°)",
                    color = BrandYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Заполнено: ${uiState.filledStartersCount}/9",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // 9 Starter Slots
        items(9) { index ->
            val rider = uiState.starters.getOrNull(index)
            val mult = DraftMathEngine.MULTIPLIERS.getOrElse(index) { 0.2 }

            StarterSlotCard(
                positionIndex = index,
                multiplier = mult,
                rider = rider,
                raceConfig = uiState.currentRace,
                canMoveUp = index > 0,
                canMoveDown = index < 8,
                onMoveUp = { viewModel.moveStarter(index, index - 1) },
                onMoveDown = { viewModel.moveStarter(index, index + 1) },
                onRemove = { viewModel.removeStarter(index) },
                onSlotClick = onNavigateToRiders
            )
        }

        // Reserve Slot (200 / 400)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "РЕЗЕРВНЫЙ СЛОТ (DNS ЗАМЕНА)",
                    color = BrandCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Правила CF-EDE: цена строго 200 или 400 кредитов.",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            ReserveSlotCard(
                rider = uiState.reserve,
                raceConfig = uiState.currentRace,
                onRemove = { viewModel.removeReserve() },
                onSlotClick = onNavigateToRiders
            )
        }

        // Roster Summary Footer Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Текущий стек:", fontSize = 11.sp, color = TextSecondary)
                        Text(uiState.currentStackName, fontSize = 11.sp, color = BrandYellow, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Средняя цена слота:", fontSize = 11.sp, color = TextSecondary)
                        Text("${uiState.averageStarterPrice} кредитов", fontSize = 11.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Сумма взвешенных E[Pts]:", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            String.format("%.1f pts", uiState.totalWeightedPoints),
                            fontSize = 13.sp,
                            color = BrandYellow,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StarterSlotCard(
    positionIndex: Int,
    multiplier: Double,
    rider: Rider?,
    raceConfig: com.example.data.model.RaceConfig,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
    onSlotClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                if (rider != null) BrandBorder else Color(0xFF1E293B),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onSlotClick),
        colors = CardDefaults.cardColors(
            containerColor = if (rider != null) BrandCard else Color(0xFF0F172A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Position & Multiplier
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(36.dp)
            ) {
                Text(
                    text = "${positionIndex + 1}°",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (positionIndex < 3) BrandYellow else TextSecondary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BrandCardElevated)
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "×$multiplier",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = BrandCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Rider Info
            if (rider != null) {
                val score = DraftMathEngine.calculateRiderScore(rider, raceConfig)
                val roi = DraftMathEngine.calculateRiderROI(rider, raceConfig)

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = rider.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (rider.price == 1200) {
                            Badge(text = "STAR", bg = Color(0xFF422006), fg = BrandYellow)
                        } else if (rider.price <= 400 && roi >= 0.40) {
                            Badge(text = "VALUE", bg = Color(0xFF064E3B), fg = BrandEmerald)
                        }
                        if (rider.u25) {
                            Badge(text = "U25", bg = Color(0xFF0C4A6E), fg = BrandCyan)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = rider.team,
                            fontSize = 10.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(text = "•", fontSize = 10.sp, color = TextMuted)
                        Text(
                            text = rider.getMainSource(),
                            fontSize = 10.sp,
                            color = BrandCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "E[Pts]: ${String.format("%.1f", score)}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = BrandYellow
                        )
                        Text(
                            text = "ROI: ${String.format("%.2f", roi)}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = BrandEmerald
                        )
                    }
                }

                // Price Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(getPriceBgColor(rider.price))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = rider.price.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = getPriceColor(rider.price)
                    )
                }

                // Move & Remove controls
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row {
                        if (canMoveUp) {
                            IconButton(
                                onClick = onMoveUp,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ArrowDropUp, contentDescription = "Вверх", tint = TextSecondary)
                            }
                        }
                        if (canMoveDown) {
                            IconButton(
                                onClick = onMoveDown,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Вниз", tint = TextSecondary)
                            }
                        }
                    }
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Удалить", tint = BrandRose, modifier = Modifier.size(14.dp))
                    }
                }
            } else {
                // Empty slot state
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Слот свободен (нажмите для выбора)",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить гонщика",
                        tint = BrandYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReserveSlotCard(
    rider: Rider?,
    raceConfig: com.example.data.model.RaceConfig,
    onRemove: () -> Unit,
    onSlotClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BrandCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .clickable(onClick = onSlotClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF082F49).copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(36.dp)
            ) {
                Text(
                    text = "RES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = BrandCyan
                )
            }

            if (rider != null) {
                val score = DraftMathEngine.calculateRiderScore(rider, raceConfig)
                val roi = DraftMathEngine.calculateRiderROI(rider, raceConfig)

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = rider.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFBAE6FD)
                        )
                        Badge(text = "DNS РЕЗЕРВ", bg = Color(0xFF0C4A6E), fg = BrandCyan)
                    }
                    Text(
                        text = "${rider.team} • Топ-15 этапов • E[Pts]: ${String.format("%.1f", score)}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(getPriceBgColor(rider.price))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = rider.price.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = getPriceColor(rider.price)
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Убрать резерв", tint = BrandRose, modifier = Modifier.size(16.dp))
                }
            } else {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Резерв не выбран (нажмите для добавления 200/400)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить резерв",
                        tint = BrandCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Badge(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .border(0.5.dp, fg, RoundedCornerShape(3.dp))
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Text(
            text = text,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = fg
        )
    }
}

fun getPriceColor(price: Int): Color {
    return when (price) {
        1200 -> Price1200Color
        800 -> Price800Color
        600 -> Price600Color
        400 -> Price400Color
        else -> Price200Color
    }
}

fun getPriceBgColor(price: Int): Color {
    return when (price) {
        1200 -> Color(0xFF422006)
        800 -> Color(0xFF082F49)
        600 -> Color(0xFF2E1065)
        400 -> Color(0xFF064E3B)
        else -> Color(0xFF042F2E)
    }
}
