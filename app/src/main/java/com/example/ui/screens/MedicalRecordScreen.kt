package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.ClientEntity
import com.example.data.local.ClinicRepository
import com.example.data.local.MedicalRecordEntity
import com.example.data.local.UserEntity
import com.example.ui.components.AllergyAlertBanner
import com.example.ui.components.BeforeAfterSlider
import com.example.ui.components.SectionHeader
import com.example.ui.components.SignaturePad
import com.example.ui.components.openWhatsAppMessage
import com.example.ui.theme.AccentAlternative
import com.example.ui.theme.AccentHighlight
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MedicalRecordScreen(
    repository: ClinicRepository,
    currentUser: UserEntity,
    initialClientId: Int? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clients by repository.clients.collectAsStateWithLifecycle(emptyList())
    val medicalRecords by repository.medicalRecords.collectAsStateWithLifecycle(emptyList())

    var selectedClientId by remember {
        mutableStateOf(initialClientId ?: clients.firstOrNull()?.id ?: 1)
    }

    val currentClient = clients.find { it.id == selectedClientId } ?: clients.firstOrNull()
    val clientRecords = medicalRecords.filter { it.clientId == currentClient?.id }

    var showNewEvolutionModal by remember { mutableStateOf(false) }

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
                        text = "Prontuário & Anamnese",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Registros clínicos, fotos antes/depois e consentimento",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showNewEvolutionModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nova Evolução", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Client Selector Chips
            Text(
                text = "PACIENTE SELECIONADO:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                items(clients) { c ->
                    val isSelected = c.id == currentClient?.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) ActionPrimary else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) ActionPrimary else BorderLight
                        ),
                        modifier = Modifier.clickable { selectedClientId = c.id }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isSelected) AccentHighlight else ActionPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = c.name.split(" ").take(2).joinToString(" "),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Records Scrollable Content
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Allergy Safety Alert (Critical for medical aesthetic safety)
                currentClient?.let { client ->
                    item {
                        AllergyAlertBanner(allergies = client.allergies)
                    }

                    // Client Summary Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = client.name,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${client.phone} • Nasc: ${client.birthDate}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Surface(
                                        color = AccentHighlight.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "Gasto Total: R$ ${client.totalSpent.toInt()}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ActionPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Observações da Anamnese: ${client.notes}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Interactive Before/After Slider Section
                item {
                    SectionHeader(
                        title = "Evolução Fotográfica (Antes & Depois)",
                        subtitle = "Arraste o divisor interativo para comparar a resposta clínica"
                    )

                    BeforeAfterSlider(
                        beforeImageRes = R.drawable.img_treatment_before,
                        afterImageRes = R.drawable.img_treatment_after,
                        height = 320.dp
                    )
                }

                // Medical Evolutions & Records
                item {
                    SectionHeader(
                        title = "Histórico de Atendimentos & Aplicações",
                        subtitle = "${clientRecords.size} registros documentados"
                    )
                }

                if (clientRecords.isEmpty()) {
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Nenhuma evolução cadastrada para este cliente ainda.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                } else {
                    items(clientRecords) { record ->
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
                                            text = record.procedureName,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Data: ${record.date} • Resp: ${record.professionalName}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Surface(
                                        color = StatusSuccessBg,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = StatusSuccessText,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Assinado",
                                                fontSize = 10.sp,
                                                color = StatusSuccessText,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Treated areas badges
                                Text(
                                    text = "Regiões Tratadas:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = record.treatedAreas,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                                )

                                // Products and Lot numbers (Traceability)
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Rastreabilidade de Insumos (ANVISA):",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ActionPrimary
                                        )
                                        Text(
                                            text = record.productsUsed,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = record.lotNumber,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Recomendações Pós-Procedimento:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = record.recommendations,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Digital Signature & WhatsApp Action
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Autenticação: ${record.signatureIntegrityHash}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusSuccessText
                                        )
                                        Text(
                                            text = "Assinado em ${record.signatureTimestamp}",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            currentClient?.let { cl ->
                                                val msg = "Olá ${cl.name.split(" ").first()}! Seguem suas orientações pós-procedimento de ${record.procedureName}:\n\n${record.recommendations}\n\nQualquer dúvida, entre em contato conosco da Aura Estética."
                                                openWhatsAppMessage(context, cl.phone, msg)
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("WhatsApp", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Interactive Consent Signature Pad Section
                item {
                    SectionHeader(
                        title = "Termo de Consentimento & Assinatura",
                        subtitle = "Assine digitalmente no dispositivo para anexar ao prontuário"
                    )

                    SignaturePad(
                        clientName = currentClient?.name ?: "Paciente",
                        onSignatureConfirmed = { hash, timestamp ->
                            currentClient?.let { cl ->
                                scope.launch {
                                    repository.insertMedicalRecord(
                                        MedicalRecordEntity(
                                            clientId = cl.id,
                                            clientName = cl.name,
                                            procedureName = "Assinatura de Termo e Consentimento",
                                            professionalName = currentUser.name,
                                            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                            allergiesAlert = cl.allergies,
                                            treatedAreas = "Consentimento livre e esclarecido para tratamentos estéticos.",
                                            productsUsed = "Conforme planejamento individual.",
                                            lotNumber = "N/A - Termo Documental",
                                            anamnesisNotes = "Paciente ciente e de acordo com os procedimentos e riscos.",
                                            recommendations = "Seguir orientações da equipe da Aura Estética.",
                                            signatureName = cl.name,
                                            signatureTimestamp = timestamp,
                                            signatureIntegrityHash = hash
                                        )
                                    )
                                }
                            }
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // New Evolution Modal
        if (showNewEvolutionModal && currentClient != null) {
            ModalBottomSheet(
                onDismissRequest = { showNewEvolutionModal = false }
            ) {
                var procedureName by remember { mutableStateOf("Toxina Botulínica e Hidratação Labial") }
                var treatedAreas by remember { mutableStateOf("Frontal, Glabela, Periorbicular") }
                var productsUsed by remember { mutableStateOf("Botox Allergan 100UI (50 UI)") }
                var lotNumber by remember { mutableStateOf("Lote: AB982-BR") }
                var anamnesisNotes by remember { mutableStateOf("Paciente retorna para retoque. Sem intercorrências prévias.") }
                var recommendations by remember { mutableStateOf("Gelo local, não massagear nas primeiras 48h.") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Nova Evolução Clínica: ${currentClient.name}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = procedureName,
                        onValueChange = { procedureName = it },
                        label = { Text("Procedimento Realizado") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = treatedAreas,
                        onValueChange = { treatedAreas = it },
                        label = { Text("Regiões Anatômicas Tratadas") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = productsUsed,
                            onValueChange = { productsUsed = it },
                            label = { Text("Produtos & Doses") },
                            modifier = Modifier.weight(1.3f)
                        )
                        OutlinedTextField(
                            value = lotNumber,
                            onValueChange = { lotNumber = it },
                            label = { Text("Lote ANVISA") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = recommendations,
                        onValueChange = { recommendations = it },
                        label = { Text("Orientações Pós-Procedimento") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            val hash = repository.generateIntegrityHash("${currentClient.id}|$procedureName")
                            scope.launch {
                                repository.insertMedicalRecord(
                                    MedicalRecordEntity(
                                        clientId = currentClient.id,
                                        clientName = currentClient.name,
                                        procedureName = procedureName,
                                        professionalName = currentUser.name,
                                        date = today,
                                        allergiesAlert = currentClient.allergies,
                                        treatedAreas = treatedAreas,
                                        productsUsed = productsUsed,
                                        lotNumber = lotNumber,
                                        anamnesisNotes = anamnesisNotes,
                                        recommendations = recommendations,
                                        signatureName = currentClient.name,
                                        signatureTimestamp = "$today 10:30 UTC-3",
                                        signatureIntegrityHash = "SHA256-$hash"
                                    )
                                )
                                showNewEvolutionModal = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Salvar Registro Clínico no Prontuário", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
