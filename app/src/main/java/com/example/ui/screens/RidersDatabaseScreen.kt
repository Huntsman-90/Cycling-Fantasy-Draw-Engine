package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.RidersDatabase
import com.example.ui.DraftUiState
import com.example.ui.DraftViewModel
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCardElevated
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RidersDatabaseScreen(
    viewModel: DraftViewModel,
    uiState: DraftUiState,
    modifier: Modifier = Modifier
) {
    val roles = listOf(
        "all" to "Все роли",
        "GC" to "GC",
        "Sprinter" to "Спринт",
        "Puncheur" to "Панчер",
        "Climber" to "Горняк",
        "ITT" to "Разделка",
        "Domestique" to "Грегари"
    )

    val prices = listOf("all", "1200", "1000", "800", "600", "400", "200")

    val filteredRiders = RidersDatabase.ALL_RIDERS.filter { rider ->
        val matchesQuery = uiState.searchQuery.isBlank() ||
                rider.name.contains(uiState.searchQuery, ignoreCase = true) ||
                rider.team.contains(uiState.searchQuery, ignoreCase = true)

        val matchesRole = uiState.filterRole == "all" || rider.role.equals(uiState.filterRole, ignoreCase = true)

        val matchesPrice = uiState.filterPrice == "all" || rider.price.toString() == uiState.filterPrice

        matchesQuery && matchesRole && matchesPrice
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandDark)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Поиск гонщика или команды...", fontSize = 12.sp, color = TextMuted) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Поиск", tint = BrandYellow, modifier = Modifier.size(18.dp))
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BrandCard,
                unfocusedContainerColor = BrandCard,
                focusedBorderColor = BrandYellow,
                unfocusedBorderColor = BrandBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("rider_search_field")
        )

        Spacer(Modifier.height(8.dp))

        // Role Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            roles.forEach { (key, label) ->
                val selected = uiState.filterRole == key
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.setFilterRole(key) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandYellow,
                        selectedLabelColor = BrandDark,
                        containerColor = BrandCard,
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

        Spacer(Modifier.height(4.dp))

        // Price Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            prices.forEach { price ->
                val selected = uiState.filterPrice == price
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.setFilterPrice(price) },
                    label = {
                        Text(
                            if (price == "all") "Все цены" else "$price кр.",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandCyan,
                        selectedLabelColor = BrandDark,
                        containerColor = BrandCard,
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

        Spacer(Modifier.height(8.dp))

        // Riders List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredRiders, key = { it.id }) { rider ->
                val isSelected = uiState.isRiderSelected(rider.id)
                val score = DraftMathEngine.calculateRiderScore(rider, uiState.currentRace)
                val roi = DraftMathEngine.calculateRiderROI(rider, uiState.currentRace)

                RiderDbCard(
                    rider = rider,
                    score = score,
                    roi = roi,
                    isSelected = isSelected,
                    onAddStarter = { viewModel.addStarter(rider) },
                    onAddReserve = { viewModel.setReserve(rider) }
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RiderDbCard(
    rider: Rider,
    score: Double,
    roi: Double,
    isSelected: Boolean,
    onAddStarter: () -> Unit,
    onAddReserve: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                if (isSelected) BrandYellow.copy(alpha = 0.6f) else BrandBorder,
                RoundedCornerShape(10.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BrandCardElevated else BrandCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Left info
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
                        Badge(text = "MUST-HAVE", bg = Color(0xFF422006), fg = BrandYellow)
                    }
                    if (rider.isArbitrage) {
                        Badge(text = "💎 VALUE", bg = Color(0xFF064E3B), fg = BrandEmerald)
                    }
                    if (rider.u25) {
                        Badge(text = "U25", bg = Color(0xFF0C4A6E), fg = BrandCyan)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = rider.team, fontSize = 10.sp, color = TextSecondary)
                    Text(text = "•", fontSize = 10.sp, color = TextMuted)
                    Text(text = rider.role, fontSize = 10.sp, color = BrandCyan)
                    Text(text = "•", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = "Форма: ${rider.rShort.toInt()}",
                        fontSize = 10.sp,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
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
                    Text(
                        text = "Риск: ${(rider.crashRisk * 100).toInt()}%",
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                }
            }

            // Price & Actions
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(getPriceBgColor(rider.price))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = rider.price.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = getPriceColor(rider.price)
                    )
                }

                if (isSelected) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = BrandYellow, modifier = Modifier.size(12.dp))
                        Text("В составе", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandYellow)
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = onAddStarter,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandYellow,
                                contentColor = BrandDark
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("+ Основа", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        if (rider.price <= 400) {
                            OutlinedButton(
                                onClick = onAddReserve,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = BrandCyan
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("+ Рез", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
