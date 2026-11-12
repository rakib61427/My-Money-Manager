package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.WarningOrange
import com.example.util.CategoryUtils
import com.example.util.CurrencyHelper
import com.example.util.Localization

data class BarChartItem(
    val label: String,
    val value: Double,
    val color: Color
)

@Composable
fun BarComparisonChart(
    items: List<BarChartItem>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val maxValue = (items.maxOfOrNull { it.value } ?: 1.0).coerceAtLeast(1.0)
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(items) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(700))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val bottomY = canvasHeight - 30.dp.toPx()
                val availableHeight = bottomY - 20.dp.toPx()

                // Baseline
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    start = Offset(0f, bottomY),
                    end = Offset(canvasWidth, bottomY),
                    strokeWidth = 1.dp.toPx()
                )

                if (items.isEmpty()) return@Canvas

                val slotWidth = canvasWidth / items.size
                val barWidth = 32.dp.toPx().coerceAtMost(slotWidth * 0.6f)

                items.forEachIndexed { index, item ->
                    val fraction = ((item.value / maxValue).toFloat() * animProgress.value).coerceIn(0f, 1f)
                    val barHeight = availableHeight * fraction
                    val barLeft = index * slotWidth + (slotWidth - barWidth) / 2
                    val barTop = bottomY - barHeight

                    // Bar
                    drawRoundRect(
                        color = item.color,
                        topLeft = Offset(barLeft, barTop),
                        size = Size(barWidth, barHeight.coerceAtLeast(4.dp.toPx())),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                }
            }

            // Labels below bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items.forEach { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                        Text(
                            text = CurrencyHelper.format(item.value, currencySymbol),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = item.color,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

data class CategoryShare(
    val category: String,
    val amount: Double,
    val color: Color
)

@Composable
fun DonutCategoryChart(
    categories: List<CategoryShare>,
    currencySymbol: String,
    language: String,
    modifier: Modifier = Modifier
) {
    val totalAmount = categories.sumOf { it.amount }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(categories) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(800))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            if (categories.isEmpty() || totalAmount == 0.0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = Localization.get("no_transactions", language),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(170.dp)
                    ) {
                        val strokeWidth = 26.dp.toPx()
                        var startAngle = -90f

                        categories.forEach { item ->
                            val sweepAngle = ((item.amount / totalAmount).toFloat() * 360f * animProgress.value)
                            drawArc(
                                color = item.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += (item.amount / totalAmount).toFloat() * 360f
                        }
                    }

                    // Center text
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = Localization.get("total", language),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = CurrencyHelper.format(totalAmount, currencySymbol),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown list
                categories.take(6).forEach { item ->
                    val percentage = if (totalAmount > 0) ((item.amount / totalAmount) * 100).toInt() else 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(item.color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = CategoryUtils.getDisplayName(item.category, language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$percentage%",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = CurrencyHelper.format(item.amount, currencySymbol),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
