package com.yuvrajsinghgmx.shopsmart.screens.orders

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yuvrajsinghgmx.shopsmart.datastore.Product
import com.yuvrajsinghgmx.shopsmart.ui.theme.LexendRegular
import com.yuvrajsinghgmx.shopsmart.utils.SharedPrefsHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrders(navController: NavController, selectedItemsJson: String) {
    val context = LocalContext.current
    var orders by remember { mutableStateOf(listOf<Product>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedItemsJson) {
        orders = try {
            Gson().fromJson(selectedItemsJson, object : TypeToken<List<Product>>() {}.type)
        } catch (e: Exception) {
            Log.e("MyOrders", "Error parsing orders", e)
            emptyList()
        }
    }

    val totalAmount = orders.sumOf { it.amount * it.no_of_items }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F5F3))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Custom Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF6F5F3),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF332D25)
                        )
                    }

                    Text(
                        text = "My Orders",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = LexendRegular
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                clearOrders(context)
                                orders = emptyList()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Orders",
                            tint = Color(0xFF332D25)
                        )
                    }
                }
            }

            // Main Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                if (orders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No orders yet. Start shopping!",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = LexendRegular,
                                color = Color.Gray
                            )
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Orders List
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(orders.size) { index ->
                                OrderItem(orders[index])
                            }
                        }

                        // Total Amount Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF98F9B3)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Total:",
                                        style = TextStyle(
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Normal,
                                            fontFamily = LexendRegular,
                                            color = Color.Black
                                        )
                                    )
                                    Text(
                                        "₹${String.format("%.1f", totalAmount)}",
                                        style = TextStyle(
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = LexendRegular,
                                            color = Color.Black
                                        )
                                    )
                                }

                                Button(
                                    onClick = { /* Implement payment logic */ },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF006D3B)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Proceed to Payment",
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = LexendRegular,
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderItem(order: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F0F7)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = order.name,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendRegular,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Quantity: ${order.no_of_items}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = LexendRegular
                    )
                )
            }
            Text(
                text = "₹${String.format("%.1f", order.amount * order.no_of_items)}",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LexendRegular,
                    color = Color(0xFF006D3B)
                )
            )
        }
    }
}

private suspend fun clearOrders(context: android.content.Context) {
    try {
        SharedPrefsHelper.saveOrders(context, emptyList())
        Log.d("MyOrders", "Orders cleared")
        Toast.makeText(context, "All orders cleared", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.e("MyOrders", "Error clearing orders", e)
        Toast.makeText(context, "Error clearing orders", Toast.LENGTH_SHORT).show()
    }
}