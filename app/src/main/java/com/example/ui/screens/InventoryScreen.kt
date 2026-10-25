package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ClinicRepository
import com.example.data.local.InventoryItemEntity
import com.example.data.local.UserEntity
import com.example.model.ItemCategory
import com.example.ui.theme.AccentAlternative
import com.example.ui.theme.AccentHighlight
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.StatusDangerBg
import com.example.ui.theme.StatusDangerText
import com.example.ui.theme.StatusWarningBg
import com.example.ui.theme.StatusWarningText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    repository: ClinicRepository,
    currentUser: UserEntity,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val inventory by repository.inventory.collectAsStateWithLifecycle(emptyList())

    var selectedCategoryTab by remember { mutableStateOf("TODAS") }
    var showAddItemModal by remember { mutableStateOf(false) }

    val categories = listOf(
        "TODAS" to "Todos os Itens",
        ItemCategory.TOXINAS.name to "Toxinas",
        ItemCategory.PREENCHEDORES.name to "Preenchedores",
        ItemCategory.BIOESTIMULADORES.name to "Bioestimuladores",
        ItemCategory.DESCARTAVEIS.name to "Descartáveis",
        ItemCategory.ANESTESICOS.name to "Anestésicos"
    )

    val filteredItems = if (selectedCategoryTab == "TODAS") {
        inventory
    } else {
        inventory.filter { it.category == selectedCategoryTab }
    }

    val lowStockCount = inventory.count { it.currentQuantity <= it.minimumQuantity }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Controle de Estoque",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Lotes ANVISA, rastreamento e alertas de validade",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddItemModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("add_inventory_item_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Novo Insumo", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Low Stock Warning Banner if any
            if (lowStockCount > 0) {
                Surface(
                    color = StatusDangerBg,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusDangerText.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationImportant,
                            contentDescription = null,
                            tint = StatusDangerText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Atenção: $lowStockCount itens com estoque igual ou abaixo do mínimo de segurança!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusDangerText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Category Filter
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { (catKey, catLabel) ->
                    val isSelected = selectedCategoryTab == catKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryTab = catKey },
                        label = { Text(catLabel, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ActionPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Inventory List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems) { item ->
                    val isLowStock = item.currentQuantity <= item.minimumQuantity

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (isLowStock) StatusDangerText.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Fabricante: ${item.brand}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Lote: ${item.lotNumber} • Validade: ${item.expirationDate}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ActionPrimary,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                if (isLowStock) {
                                    Surface(
                                        color = StatusDangerBg,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "ESTOQUE BAIXO",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusDangerText,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quantity Adjuster Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mínimo: ${item.minimumQuantity} ${item.unit}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (item.currentQuantity > 0) {
                                                scope.launch {
                                                    repository.updateInventoryItem(item.copy(currentQuantity = item.currentQuantity - 1))
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RemoveCircleOutline,
                                            contentDescription = "Diminuir Estoque",
                                            tint = ActionPrimary
                                        )
                                    }

                                    Surface(
                                        color = if (isLowStock) StatusDangerBg else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    ) {
                                        Text(
                                            text = "${item.currentQuantity} ${item.unit}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isLowStock) StatusDangerText else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                repository.updateInventoryItem(item.copy(currentQuantity = item.currentQuantity + 1))
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddCircleOutline,
                                            contentDescription = "Aumentar Estoque",
                                            tint = ActionPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // Modal for Adding Inventory Item
        if (showAddItemModal) {
            ModalBottomSheet(
                onDismissRequest = { showAddItemModal = false }
            ) {
                var name by remember { mutableStateOf("") }
                var brand by remember { mutableStateOf("") }
                var lotNumber by remember { mutableStateOf("") }
                var expirationDate by remember { mutableStateOf("2027-12-31") }
                var currentQtyText by remember { mutableStateOf("5") }
                var minimumQtyText by remember { mutableStateOf("3") }
                var unit by remember { mutableStateOf("Frasco") }
                var category by remember { mutableStateOf(ItemCategory.TOXINAS.name) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Cadastrar Novo Insumo no Estoque",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do Produto (ex: Botox 100 UI, Juvederm...)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Fabricante / Laboratório") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lotNumber,
                            onValueChange = { lotNumber = it },
                            label = { Text("Lote ANVISA") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = expirationDate,
                            onValueChange = { expirationDate = it },
                            label = { Text("Data de Validade") },
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unidade (Frasco, Caixa...)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = currentQtyText,
                            onValueChange = { currentQtyText = it },
                            label = { Text("Qtd Inicial") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minimumQtyText,
                            onValueChange = { minimumQtyText = it },
                            label = { Text("Estoque Mínimo") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                scope.launch {
                                    repository.insertInventoryItem(
                                        InventoryItemEntity(
                                            name = name,
                                            brand = brand,
                                            lotNumber = lotNumber,
                                            expirationDate = expirationDate,
                                            currentQuantity = currentQtyText.toIntOrNull() ?: 1,
                                            minimumQuantity = minimumQtyText.toIntOrNull() ?: 1,
                                            unit = unit,
                                            category = category
                                        )
                                    )
                                    showAddItemModal = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Salvar Insumo no Estoque", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
