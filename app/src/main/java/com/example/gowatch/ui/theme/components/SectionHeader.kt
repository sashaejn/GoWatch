package com.example.gowatch.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gowatch.R
import androidx.compose.ui.tooling.preview.Preview
import com.example.gowatch.ui.theme.GoWatchTheme

@Composable
fun SectionHeader(
    title: String,
    itemCount: Int? = null,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (itemCount != null) "$title ($itemCount)" else title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        if (onSeeAllClick != null) {
            Text(
                text = "See all",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }
    }
}

@Preview(name = "Section Header", showBackground = true)
@Composable
fun SectionHeaderPreview() {
    GoWatchTheme {
        Column {
            SectionHeader(
                title = "Trending Now",
                onSeeAllClick = null
            )
            SectionHeader(
                title = "My Watchlist",
                itemCount = 5,
                onSeeAllClick = {}
            )
        }
    }
}