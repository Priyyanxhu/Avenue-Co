package com.example.multivendor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.multivendor.data.RatingBreakdown
import com.example.multivendor.data.Review
import com.example.multivendor.theme.BrandOrange
import com.example.multivendor.theme.StarAmber
import com.example.multivendor.theme.SuccessGreen

@Composable
fun RatingBreakdownCard(
    breakdown: RatingBreakdown,
    onWriteReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Ratings & Customer Reviews ⭐",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = onWriteReviewClick,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.RateReview, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Rate Product")
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Big Rating Badge (Flipkart/Amazon style)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Surface(
                        color = SuccessGreen,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "%.1f".format(breakdown.averageRating),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${breakdown.totalReviews} Ratings",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "94% Recommended",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Spacer(Modifier.width(16.dp))

                // 5 Star Breakdown Progress Bars
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(5, 4, 3, 2, 1).forEach { star ->
                        val count = breakdown.starCounts[star] ?: 0
                        val percentage = if (breakdown.totalReviews > 0) count.toFloat() / breakdown.totalReviews else 0f
                        val barColor = when (star) {
                            5 -> SuccessGreen
                            4 -> Color(0xFF65A30D)
                            3 -> StarAmber
                            2 -> BrandOrange
                            else -> MaterialTheme.colorScheme.error
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "$star★",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.width(24.dp)
                            )
                            LinearProgressIndicator(
                                progress = { percentage },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = barColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                count.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerReviewCard(
    review: Review,
    onHelpfulClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hasUpvoted by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            // User Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            review.userName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(review.userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        if (review.isVerifiedBuyer) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    "Certified Buyer",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                Text(review.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(10.dp))

            // Rating Pill + Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = when (review.rating) {
                        5, 4 -> SuccessGreen
                        3 -> StarAmber
                        else -> MaterialTheme.colorScheme.error
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${review.rating}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(Modifier.width(2.dp))
                        Icon(Icons.Filled.Star, null, tint = Color.White, modifier = Modifier.size(11.dp))
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(review.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(8.dp))
            Text(review.comment, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(6.dp))

            // Helpful button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (review.helpfulCount > 0) "${review.helpfulCount} people found this helpful" else "Be the first to vote",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = {
                        if (!hasUpvoted) {
                            hasUpvoted = true
                            onHelpfulClick()
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        if (hasUpvoted) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Helpful",
                        modifier = Modifier.size(15.dp),
                        tint = if (hasUpvoted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (hasUpvoted) "Helpful!" else "Helpful",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (hasUpvoted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun WriteReviewDialog(
    productName: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, title: String, comment: String, reviewerName: String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var title by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var reviewerName by remember { mutableStateOf("Priya Sharma") }
    var error by remember { mutableStateOf<String?>(null) }

    val ratingLabels = mapOf(
        1 to "1★ Terrible 😞",
        2 to "2★ Disappointing 😕",
        3 to "3★ Average 😐",
        4 to "4★ Very Good 😊",
        5 to "5★ Outstanding / Loved it! 🤩"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Rate & Review Product ⭐", fontWeight = FontWeight.Bold)
                Text(productName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, maxLines = 1)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Star Selector
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { rating = star }) {
                                Icon(
                                    imageVector = if (star <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = "$star stars",
                                    tint = StarAmber,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    Text(
                        ratingLabels[rating] ?: "",
                        fontWeight = FontWeight.Bold,
                        color = if (rating >= 4) SuccessGreen else if (rating == 3) StarAmber else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; error = null },
                    label = { Text("Review Headline *") },
                    placeholder = { Text("e.g. Awesome quality & super fast delivery!") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it; error = null },
                    label = { Text("Detailed Review *") },
                    placeholder = { Text("Share what you liked or disliked about the product...") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = reviewerName,
                    onValueChange = { reviewerName = it },
                    label = { Text("Your Name (Public)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        title.isBlank() -> error = "Please provide a review title."
                        comment.isBlank() -> error = "Please write a brief comment."
                        reviewerName.isBlank() -> error = "Please enter your name."
                        else -> onSubmit(rating, title.trim(), comment.trim(), reviewerName.trim())
                    }
                }
            ) {
                Text("Submit Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
