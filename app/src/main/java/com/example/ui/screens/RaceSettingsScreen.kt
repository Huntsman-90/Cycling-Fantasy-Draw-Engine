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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RaceConfig
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RaceSettingsScreen(
    viewModel: DraftViewModel,
    uiState: DraftUiState,
    modifier: Modifier = Modifier
) {
    val race = uiState.currentRace

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrandDark)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section: Race Presets
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
                    Text(
                        text = "🏁 ШАБЛОНЫ ГОНОК (PRESETS)",
                        color = BrandYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        RaceConfig.PRESETS.forEach { preset ->
                            val isSelected = preset.id == race.id
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BrandCardElevated else BrandDark)
                                    .border(
                                        1.dp,
                                        if (isSelected) BrandYellow else BrandBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setRacePreset(preset) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = preset.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) BrandYellow else TextPrimary
                                        )
                                        Text(
                                            text = preset.category,
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Text(
                                        text = "${preset.budget} кр.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (preset.budget >= 6000) BrandYellow else BrandCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Terrain Profile Sliders
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
                    Text(
                        text = "⛰️ ПРОФИЛЬ ТРАССЫ И РЕЛЬЕФ",
                        color = BrandCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Mountain Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Горы / Высокогорье (FTP):", fontSize = 11.sp, color = TextSecondary)
                            Text("${race.mountain}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandYellow, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = race.mountain.toFloat(),
                            onValueChange = { viewModel.updateRaceParams(mountain = it.toInt()) },
                            valueRange = 0f..100f,
                            steps = 19,
                            colors = SliderDefaults.colors(
                                thumbColor = BrandYellow,
                                activeTrackColor = BrandYellow
                            )
                        )
                    }

                    // Cobbles Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Паве / Брусчатка / Стеррато:", fontSize = 11.sp, color = TextSecondary)
                            Text("${race.cobbles}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandCyan, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = race.cobbles.toFloat(),
                            onValueChange = { viewModel.updateRaceParams(cobbles = it.toInt()) },
                            valueRange = 0f..100f,
                            steps = 19,
                            colors = SliderDefaults.colors(
                                thumbColor = BrandCyan,
                                activeTrackColor = BrandCyan
                            )
                        )
                    }

                    // Sprint plains
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Спринтерские равнины:", fontSize = 11.sp, color = TextSecondary)
                            Text("${race.sprint}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandEmerald, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = race.sprint.toFloat(),
                            onValueChange = { viewModel.updateRaceParams(sprint = it.toInt()) },
                            valueRange = 0f..100f,
                            steps = 19,
                            colors = SliderDefaults.colors(
                                thumbColor = BrandEmerald,
                                activeTrackColor = BrandEmerald
                            )
                        )
                    }

                    // ITT distance
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Разделки ITT / TTT (дистанция):", fontSize = 11.sp, color = TextSecondary)
                            Text("${race.itt} км", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandRose, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = race.itt.toFloat(),
                            onValueChange = { viewModel.updateRaceParams(itt = it.toInt()) },
                            valueRange = 0f..70f,
                            steps = 13,
                            colors = SliderDefaults.colors(
                                thumbColor = BrandRose,
                                activeTrackColor = BrandRose
                            )
                        )
                    }
                }
            }
        }

        // Section: Weather Module
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
                    Text(
                        text = "🌦️ МЕТЕОРОЛОГИЧЕСКИЙ МОДУЛЬ (K_weather)",
                        color = BrandEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Wind
                    Column {
                        Text("Боковой ветер:", fontSize = 11.sp, color = TextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "calm" to "Штиль",
                                "moderate" to "Умеренный",
                                "echelon" to "Эшелоны ⚠️"
                            ).forEach { (w, label) ->
                                val selected = race.wind == w
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.updateRaceParams(wind = w) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandEmerald,
                                        selectedLabelColor = BrandDark,
                                        containerColor = BrandDark,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (selected) BrandEmerald else BrandBorder,
                                        selectedBorderColor = BrandEmerald,
                                        enabled = true,
                                        selected = selected
                                    )
                                )
                            }
                        }
                    }

                    // Rain
                    Column {
                        Text("Осадки на трассе:", fontSize = 11.sp, color = TextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "dry" to "Сухо",
                                "wet" to "Мокрая",
                                "storm" to "Шторм 🌧️"
                            ).forEach { (r, label) ->
                                val selected = race.rain == r
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.updateRaceParams(rain = r) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandCyan,
                                        selectedLabelColor = BrandDark,
                                        containerColor = BrandDark,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (selected) BrandCyan else BrandBorder,
                                        selectedBorderColor = BrandCyan,
                                        enabled = true,
                                        selected = selected
                                    )
                                )
                            }
                        }
                    }

                    // Temperature
                    Column {
                        Text("Температурный режим:", fontSize = 11.sp, color = TextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "cold" to "Холод (≤8°)",
                                "optimal" to "Комфорт",
                                "heat" to "Жара (≥32°) ☀️"
                            ).forEach { (t, label) ->
                                val selected = race.temp == t
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.updateRaceParams(temp = t) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandYellow,
                                        selectedLabelColor = BrandDark,
                                        containerColor = BrandDark,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (selected) BrandYellow else BrandBorder,
                                        selectedBorderColor = BrandYellow,
                                        enabled = true,
                                        selected = selected
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Rules Cheat Sheet
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandCard.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "📖 СВОД ПРАВИЛ CF-EDE (Strict 1200 Cap)",
                        color = BrandYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text("• Цены строго дискретные: 200, 400, 600, 800, 1000, 1200. Максимальная цена — 1200.", fontSize = 11.sp, color = TextSecondary)
                    Text("• Лимит бюджета: Гранд-туры — 6000 | Монументы и недельные — 5000 кредитов.", fontSize = 11.sp, color = TextSecondary)
                    Text("• Состав: 9 стартовых слотов (1° to 9°) + 1 резервный слот за 200 или 400 кредитов.", fontSize = 11.sp, color = TextSecondary)
                    Text("• Тай-брейк: 1° (x1.0), 2° (x0.9), 3° (x0.8) ... 9° (x0.2). Ранжирование критически влияет на сумму очков.", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
        }
    }
}
