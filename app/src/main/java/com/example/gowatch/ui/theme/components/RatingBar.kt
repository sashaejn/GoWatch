package com.example.gowatch.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// RatingBar Anda harus didefinisikan seperti ini:
@Composable
fun RatingBar(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    stars: Int = 5,
    starSize: Int = 36
) {
    Row(modifier = modifier) {
        for (i in 1..stars) {
            IconButton(
                onClick = {
                    onRatingChange(i.toFloat())
                },
                modifier = Modifier.size(starSize.dp)
            ) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star
                    else Icons.Outlined.Star,
                    contentDescription = "Rate $i stars",
                    tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray
                )
            }
        }
    }
}
