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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ClinicRepository
import com.example.data.local.TreatmentPackageEntity
import com.example.data.local.UserEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentAlternative
import com.example.ui.theme.AccentHighlight
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessText
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackagesScreen(
    repository: ClinicRepository,
    currentUser: UserEntity,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val packages by repository.packages.collectAsStateWithLifecycle(emptyList())
    val clients by repository.clients.collectAsStateWithLifecycle(emptyList())

    val ptBr = Locale("pt", "BR")
    val currencyFormatter = NumberFormat.getCurrencyInstance(ptBr)

    var showNewPackageModal by remember { mutableStateOf(false) }

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
                        text = "Pacotes de Tratamento",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Gestão de sessões contratadas, realizadas e restantes",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showNewPackageModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("create_package_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Criar Pacote", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (packages.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Nenhum pacote de tratamento ativo no momento.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(packages) { pkg ->
                        val remaining = (pkg.totalSessions - pkg.completedSessions).coerceAtLeast(0)
                        val progress = if (pkg.totalSessions > 0) pkg.completedSessions.toFloat() / pkg.totalSessions.toFloat() else 0f
                        val isFinished = remaining == 0

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
                                            text = pkg.packageName,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Paciente: ${pkg.clientName}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = ActionPrimary,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    StatusBadge(status = if (isFinished) "CONCLUIDO" else pkg.status)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Progress Indicator
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${pkg.completedSessions} de ${pkg.totalSessions} sessões realizadas",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$remaining restantes",
                                        fontSize = 12.sp,
                                        color = if (remaining > 0) ActionPrimary else StatusSuccessText,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (isFinished) StatusSuccessText else ActionPrimary,
                                    trackColor = BorderLight
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Valor: ${currencyFormatter.format(pkg.price)}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Validade: ${pkg.expiryDate}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (!isFinished) {
                                        Button(
                                            onClick = {
                                                val newCompleted = pkg.completedSessions + 1
                                                val newStatus = if (newCompleted >= pkg.totalSessions) "CONCLUIDO" else "ATIVO"
                                                scope.launch {
                                                    repository.updatePackage(
                                                        pkg.copy(
                                                            completedSessions = newCompleted,
                                                            status = newStatus
                                                        )
                                                    )
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Baixa de 1 Sessão", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // Modal for Creating Package
        if (showNewPackageModal) {
            ModalBottomSheet(
                onDismissRequest = { showNewPackageModal = false }
            ) {
                var selectedClientId by remember { mutableStateOf(clients.firstOrNull()?.id ?: 1) }
                var selectedClientName by remember { mutableStateOf(clients.firstOrNull()?.name ?: "") }
                var packageName by remember { mutableStateOf("Protocolo Glow & Firm (4 Sessões)") }
                var totalSessionsText by remember { mutableStateOf("4") }
                var priceText by remember { mutableStateOf("1980") }
                var expiryDate by remember { mutableStateOf("2027-05-30") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Novo Pacote de Sessões",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Selecione o Cliente:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        items(clients) { c ->
                            FilterChip(
                                selected = selectedClientId == c.id,
                                onClick = {
                                    selectedClientId = c.id
                                    selectedClientName = c.name
                                },
                                label = { Text(c.name.split(" ").take(2).joinToString(" "), fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = packageName,
                        onValueChange = { packageName = it },
                        label = { Text("Nome do Pacote / Protocolo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = totalSessionsText,
                            onValueChange = { totalSessionsText = it },
                            label = { Text("Total de Sessões") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Valor Total (R$)") },
                            modifier = Modifier.weight(1.3f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Data de Validade (AAAA-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val total = totalSessionsText.toIntOrNull() ?: 3
                            val price = priceText.toDoubleOrNull() ?: 1000.0
                            scope.launch {
                                repository.insertPackage(
                                    TreatmentPackageEntity(
                                        clientId = selectedClientId,
                                        clientName = selectedClientName,
                                        packageName = packageName,
                                        totalSessions = total,
                                        completedSessions = 0,
                                        price = price,
                                        expiryDate = expiryDate,
                                        status = "ATIVO"
                                    )
                                )
                                showNewPackageModal = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Criar e Ativar Pacote", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
