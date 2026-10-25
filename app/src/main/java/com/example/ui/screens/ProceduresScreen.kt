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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Spa
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
import com.example.data.local.ProcedureEntity
import com.example.data.local.UserEntity
import com.example.model.ProcedureCategory
import com.example.ui.theme.AccentAlternative
import com.example.ui.theme.AccentHighlight
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.BorderLight
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProceduresScreen(
    repository: ClinicRepository,
    currentUser: UserEntity,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val procedures by repository.procedures.collectAsStateWithLifecycle(emptyList())
    val isAdmin = currentUser.role == "ADMIN"

    val ptBr = Locale("pt", "BR")
    val currencyFormatter = NumberFormat.getCurrencyInstance(ptBr)

    var selectedCategoryTab by remember { mutableStateOf("TODAS") }
    var showAddProcedureModal by remember { mutableStateOf(false) }

    val categories = listOf(
        "TODAS" to "Todos Procedimentos",
        ProcedureCategory.INJETAVEIS.name to "Injetáveis",
        ProcedureCategory.FACIAL.name to "Facial",
        ProcedureCategory.CORPORAL.name to "Corporal",
        ProcedureCategory.CAPILAR.name to "Capilar",
        ProcedureCategory.LASER_TECNOLOGIA.name to "Laser & Tec"
    )

    val filteredList = if (selectedCategoryTab == "TODAS") {
        procedures
    } else {
        procedures.filter { it.category == selectedCategoryTab }
    }

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
                        text = "Procedimentos & Protocolos",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Catálogo técnico com valores, duração e retornos",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isAdmin) {
                    Button(
                        onClick = { showAddProcedureModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("add_procedure_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Novo", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Tabs
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

            // List of Procedures
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList) { proc ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = proc.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        color = AccentHighlight.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            text = ProcedureCategory.entries.find { it.name == proc.category }?.label ?: proc.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ActionPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = currencyFormatter.format(proc.price),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ActionPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = proc.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Technical Info Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = AccentAlternative,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${proc.durationMinutes} minutos",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = AccentAlternative,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Retorno em ${proc.returnIntervalDays} dias",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ActionPrimary
                                    )
                                }
                            }

                            if (proc.postCare.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Cuidados: ${proc.postCare}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }

                            if (isAdmin) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = {
                                            scope.launch { repository.deleteProcedure(proc) }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Excluir Procedimento",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(16.dp)
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

        // Add Procedure Modal
        if (showAddProcedureModal) {
            ModalBottomSheet(
                onDismissRequest = { showAddProcedureModal = false }
            ) {
                var name by remember { mutableStateOf("") }
                var category by remember { mutableStateOf(ProcedureCategory.INJETAVEIS.name) }
                var priceText by remember { mutableStateOf("1200") }
                var durationText by remember { mutableStateOf("45") }
                var returnDaysText by remember { mutableStateOf("90") }
                var description by remember { mutableStateOf("") }
                var postCare by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Cadastrar Novo Procedimento",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do Procedimento") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Categoria Técnica:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        items(ProcedureCategory.entries) { cat ->
                            FilterChip(
                                selected = category == cat.name,
                                onClick = { category = cat.name },
                                label = { Text(cat.label, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Preço (R$)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = durationText,
                            onValueChange = { durationText = it },
                            label = { Text("Duração (min)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = returnDaysText,
                            onValueChange = { returnDaysText = it },
                            label = { Text("Retorno (dias)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição Técnica") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = postCare,
                        onValueChange = { postCare = it },
                        label = { Text("Recomendações Pós-Procedimento") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                scope.launch {
                                    repository.insertProcedure(
                                        ProcedureEntity(
                                            name = name,
                                            category = category,
                                            price = priceText.toDoubleOrNull() ?: 500.0,
                                            durationMinutes = durationText.toIntOrNull() ?: 45,
                                            returnIntervalDays = returnDaysText.toIntOrNull() ?: 60,
                                            description = description,
                                            postCare = postCare
                                        )
                                    )
                                    showAddProcedureModal = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Salvar Procedimento", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
