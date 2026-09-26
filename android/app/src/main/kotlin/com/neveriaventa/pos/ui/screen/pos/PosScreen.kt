package com.neveriaventa.pos.ui.screen.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neveriaventa.pos.data.model.OrderItem
import com.neveriaventa.pos.data.model.Product
import com.neveriaventa.pos.ui.viewmodel.PosViewModel

@Composable
fun PosScreen(viewModel: PosViewModel = hiltViewModel()) {
    val products = viewModel.products.collectAsState(initial = emptyList()).value
    val cart = viewModel.cart.collectAsState(initial = emptyList()).value
    val total = viewModel.total.collectAsState(initial = 0.0).value

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("POS Sistema", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Total: \$%.2f".format(total), style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.7f).fillMaxHeight().padding(16.dp)) {
                Text("Productos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products) { product -> ProductCard(product) { viewModel.addToCart(it) } }
                }
            }

            Divider(modifier = Modifier.fillMaxHeight().padding(vertical = 16.dp))

            Column(modifier = Modifier.weight(0.3f).fillMaxHeight().padding(16.dp)) {
                Text("Carrito", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    items(cart) { item -> CartItemRow(item) { viewModel.removeFromCart(it) } }
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("\$%.2f".format(total), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Button(
                    onClick = { viewModel.checkout() },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    enabled = cart.isNotEmpty()
                ) { Text("Procesar Pago", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onAdd: (Product) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onAdd(product) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(product.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, maxLines = 2)
            Text("\$%.2f".format(product.price), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun CartItemRow(item: OrderItem, onRemove: (OrderItem) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Text("x%d - \$%.2f".format(item.quantity, item.subtotal), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            IconButton(onClick = { onRemove(item) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
