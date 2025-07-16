package com.yuvrajsinghgmx.shopsmart.screens.preferences

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yuvrajsinghgmx.shopsmart.R
import java.text.SimpleDateFormat
import java.util.*

data class SearchItem(
    val query: String,
    val timestamp: Long,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchHistoryScreen(navController: NavController) {
    val lightBackgroundColor = Color(0xFFF6F5F3)
    var showClearDialog by remember { mutableStateOf(false) }

    var searchHistory by remember {
        mutableStateOf(
            listOf(
                SearchItem("Green Running Shoes", System.currentTimeMillis() - 3600000, "Sports"),
                SearchItem("Wireless Headphones", System.currentTimeMillis() - 7200000, "Electronics"),
                SearchItem("Cotton T-Shirts", System.currentTimeMillis() - 86400000, "Fashion"),
                SearchItem("Smart Watch", System.currentTimeMillis() - 172800000, "Electronics"),
                SearchItem("Yoga Mat", System.currentTimeMillis() - 259200000, "Sports"),
                SearchItem("Winter Jacket", System.currentTimeMillis() - 345600000, "Fashion"),
                SearchItem("Gaming Mouse", System.currentTimeMillis() - 432000000, "Electronics")
            )
        )
    }

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val categories = searchHistory.map { it.category }.distinct()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightBackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Custom Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = lightBackgroundColor,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    Text(
                        text = "Search History",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF332D25),
                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                    )

                    if (searchHistory.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.delete_sweep_24px),
                                contentDescription = "Clear History"
                            )
                        }
                    }
                }
            }

            // Main Content
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Category Filter
                if (categories.isNotEmpty()) {
                    ScrollableTabRow(
                        selectedTabIndex = categories.indexOf(selectedCategory) + 1,
                        modifier = Modifier.padding(vertical = 8.dp),
                        edgePadding = 16.dp,
                        containerColor = lightBackgroundColor,
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            text = { Text("All") }
                        )
                        categories.forEach { category ->
                            Tab(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                text = { Text(category) }
                            )
                        }
                    }
                }

                if (searchHistory.isEmpty()) {
                    EmptyHistoryMessage()
                } else {
                    val filteredHistory = if (selectedCategory != null) {
                        searchHistory.filter { it.category == selectedCategory }
                    } else {
                        searchHistory
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 80.dp // Space for bottom navigation
                        )
                    ) {
                        items(
                            items = filteredHistory,
                            key = { "${it.query}${it.timestamp}" }
                        ) { searchItem ->
                            SearchHistoryItem(
                                searchItem = searchItem,
                                onDelete = {
                                    searchHistory = searchHistory.filter { it != searchItem }
                                },
                                modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                            )
                        }
                    }
                }
            }
        }

        // Clear Dialog
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("Clear Search History") },
                text = { Text("Are you sure you want to clear all search history?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            searchHistory = emptyList()
                            showClearDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Clear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchHistoryItem(
    searchItem: SearchItem,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = searchItem.query,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { },
                        label = { Text(searchItem.category) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        leadingIcon = {
                            Icon(
                                painter = when (searchItem.category) {
                                    "Electronics" -> painterResource(id = R.drawable.devices_24px)
                                    "Sports" -> painterResource(id = R.drawable.fitness_center_24px)
                                    "Fashion" -> painterResource(id = R.drawable.checkroom_24px)
                                    else -> painterResource(id = R.drawable.category_24px)
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                    Text(
                        text = dateFormat.format(Date(searchItem.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(id = R.drawable.delete_24px),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.history_24px),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Search History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your search history will appear here",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}