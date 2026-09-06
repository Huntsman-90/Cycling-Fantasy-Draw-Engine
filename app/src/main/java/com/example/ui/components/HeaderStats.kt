package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DraftUiState
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandRose
import com.example.ui.theme.BrandYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HeaderStats(
    uiState: DraftUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BrandDark)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // App badge & title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BrandYellow)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "CF-EDE v2.4",
                        color = BrandDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = "Strict 1200 Cap & Arbitrage",
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }

        Text(
            text = "🚴 Cycling Fantasy Draft Engine",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )

        Text(
            text = "Математический оптимизатор: 9 основных слотов + 1 резерв (200/400)",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        // 4 Quick Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MetricCard(
                title = "ЛИМИТ",
                value = uiState.currentRace.budget.toString(),
                valueColor = BrandYellow,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "ПОТРАЧЕНО",
                value = uiState.totalSpent.toString(),
                valueColor = if (uiState.isOverBudget) BrandRose else BrandEmerald,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "СВОБОДНО",
                value = uiState.budgetRemaining.toString(),
                valueColor = if (uiState.budgetRemaining < 0) BrandRose else BrandCyan,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "E[PTS]",
                value = String.format("%.1f", uiState.totalWeightedPoints),
                valueColor = BrandYellow,
                modifier = Modifier.weight(1.1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BrandCard)
            .border(1.dp, BrandBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = valueColor
            )
        }
    }
}
