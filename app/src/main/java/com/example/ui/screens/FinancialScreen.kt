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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import com.example.data.local.FinancialTransactionEntity
import com.example.data.local.UserEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.theme.AccentAlternative
import com.example.ui.theme.AccentHighlight
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.StatusDangerBg
import com.example.ui.theme.StatusDangerText
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessText
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialScreen(
    repository: ClinicRepository,
    currentUser: UserEntity,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val transactions by repository.transactions.collectAsStateWithLifecycle(emptyList())
    val professionals by repository.professionals.collectAsStateWithLifecycle(emptyList())
    val appointments by repository.appointments.collectAsStateWithLifecycle(emptyList())

    val isAdmin = currentUser.role == "ADMIN"
    val ptBr = Locale("pt", "BR")
    val currencyFormatter = NumberFormat.getCurrencyInstance(ptBr)

    var showNewTransactionModal by remember { mutableStateOf(false) }

    // Computations
    val totalRevenue = transactions.filter { it.type == "RECEITA" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "DESPESA" }.sumOf { it.amount }
    val netBalance = totalRevenue - totalExpense

    // Individual commissions calculation from completed appointments
    val completedAppointments = appointments.filter { it.status == "CONCLUIDO" && !it.isBlocked }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isAdmin) "Gestão Financeira & Comissões" else "Minhas Comissões & Produção",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isAdmin) "Fluxo de caixa, calculadora de repasses e despesas" else "Extrato individual de atendimentos e comissões a receber",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isAdmin) {
                        Button(
                            onClick = { showNewTransactionModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("add_transaction_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Lançar", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Financial Balance Overview (Admin Only)
            if (isAdmin) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard(
                                title = "RECEITA TOTAL",
                                value = currencyFormatter.format(totalRevenue),
                                subtitle = "Entradas registradas",
                                icon = Icons.Default.ArrowUpward,
                                iconTint = StatusSuccessText,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "DESPESAS / INSUMOS",
                                value = currencyFormatter.format(totalExpense),
                                subtitle = "Saídas e custos",
                                icon = Icons.Default.ArrowDownward,
                                iconTint = StatusDangerText,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = ActionPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
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
                                        text = "SALDO LÍQUIDO OPERACIONAL",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentHighlight
                                    )
                                    Text(
                                        text = currencyFormatter.format(netBalance),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Surface(
                                    color = AccentHighlight.copy(alpha = 0.25f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AttachMoney,
                                            contentDescription = null,
                                            tint = AccentHighlight,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Commission Calculator Section
            item {
                SectionHeader(
                    title = if (isAdmin) "Calculadora de Comissões por Profissional" else "Minha Produção & Repasse",
                    subtitle = "Cálculo automatizado sobre os procedimentos concluídos"
                )
            }

            val profsToShow = if (isAdmin) {
                professionals
            } else {
                professionals.filter { it.name.contains(currentUser.name, ignoreCase = true) || it.id == currentUser.professionalId }
            }

            items(profsToShow) { prof ->
                val profAppointments = completedAppointments.filter { it.professionalId == prof.id || it.professionalName.contains(prof.name, ignoreCase = true) }
                val totalProduced = profAppointments.sumOf { it.price }
                val commissionEarned = totalProduced * prof.commissionRate
                val ratePercent = (prof.commissionRate * 100).toInt()

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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = prof.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${prof.specialty} • Taxa: $ratePercent%",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                color = StatusSuccessBg,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Comissão a Repassar",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = StatusSuccessText
                                    )
                                    Text(
                                        text = currencyFormatter.format(commissionEarned),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusSuccessText
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Produção Total Faturada:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = currencyFormatter.format(totalProduced),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Atendimentos Concluídos:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${profAppointments.size} procedimentos",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Transaction History Section (Admin Only)
            if (isAdmin) {
                item {
                    SectionHeader(
                        title = "Extrato Recente do Fluxo de Caixa",
                        subtitle = "${transactions.size} lançamentos registrados"
                    )
                }

                items(transactions) { tx ->
                    val isIncome = tx.type == "RECEITA"
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isIncome) StatusSuccessBg else StatusDangerBg,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                            contentDescription = null,
                                            tint = if (isIncome) StatusSuccessText else StatusDangerText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = tx.description,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${tx.category} • ${tx.date}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = (if (isIncome) "+ " else "- ") + currencyFormatter.format(tx.amount),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isIncome) StatusSuccessText else StatusDangerText
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // New Transaction Modal (Admin Only)
        if (showNewTransactionModal && isAdmin) {
            ModalBottomSheet(
                onDismissRequest = { showNewTransactionModal = false }
            ) {
                var type by remember { mutableStateOf("RECEITA") }
                var description by remember { mutableStateOf("") }
                var category by remember { mutableStateOf("Procedimentos") }
                var amountText by remember { mutableStateOf("1500") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Novo Lançamento Financeiro",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = type == "RECEITA",
                            onClick = { type = "RECEITA"; category = "Procedimentos" },
                            label = { Text("Receita (+)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = type == "DESPESA",
                            onClick = { type = "DESPESA"; category = "Insumos & Fornecedores" },
                            label = { Text("Despesa (-)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição do Lançamento") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = { Text("Valor (R$)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Categoria") },
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull() ?: 0.0
                            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            if (description.isNotBlank() && amount > 0) {
                                scope.launch {
                                    repository.insertTransaction(
                                        FinancialTransactionEntity(
                                            type = type,
                                            category = category,
                                            description = description,
                                            amount = amount,
                                            date = today
                                        )
                                    )
                                    showNewTransactionModal = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Confirmar Lançamento", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
