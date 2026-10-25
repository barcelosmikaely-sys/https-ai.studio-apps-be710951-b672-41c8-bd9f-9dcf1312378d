package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Room
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppointmentEntity
import com.example.data.local.ClinicRepository
import com.example.data.local.UserEntity
import com.example.model.AppointmentStatus
import com.example.ui.components.StatusBadge
import com.example.ui.components.openWhatsAppMessage
import com.example.ui.theme.AccentAlternative
import com.example.ui.theme.AccentHighlight
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.StatusDangerBg
import com.example.ui.theme.StatusDangerText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    repository: ClinicRepository,
    currentUser: UserEntity,
    onNavigateToRecord: (clientId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appointments by repository.appointments.collectAsStateWithLifecycle(emptyList())
    val clients by repository.clients.collectAsStateWithLifecycle(emptyList())
    val procedures by repository.procedures.collectAsStateWithLifecycle(emptyList())
    val professionals by repository.professionals.collectAsStateWithLifecycle(emptyList())

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    var selectedStatusFilter by remember { mutableStateOf<String?>("TODOS") }
    var selectedProfFilter by remember { mutableStateOf<String?>("TODOS") }
    var selectedRoomFilter by remember { mutableStateOf<String?>("TODAS") }

    var showNewAppointmentModal by remember { mutableStateOf(false) }
    var showBlockSlotModal by remember { mutableStateOf(false) }

    // Filter appointments
    val filteredAppointments = appointments.filter { item ->
        val matchStatus = if (selectedStatusFilter == "TODOS") true else item.status == selectedStatusFilter
        val matchProf = if (selectedProfFilter == "TODOS") true else item.professionalName.contains(selectedProfFilter ?: "", ignoreCase = true)
        val matchRoom = if (selectedRoomFilter == "TODAS") true else item.room.contains(selectedRoomFilter ?: "", ignoreCase = true)
        matchStatus && matchProf && matchRoom
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

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Agenda Inteligente",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Grade de atendimentos, salas e bloqueios",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showBlockSlotModal = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bloquear", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { showNewAppointmentModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("new_appointment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agendar", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val statuses = listOf("TODOS", "CONFIRMADO", "AGUARDANDO", "EM_ATENDIMENTO", "CONCLUIDO", "CANCELADO")
                items(statuses) { st ->
                    val isSelected = selectedStatusFilter == st
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatusFilter = st },
                        label = {
                            Text(
                                text = if (st == "TODOS") "Todos Status" else AppointmentStatus.entries.find { it.name == st }?.label ?: st,
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ActionPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Professional Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedProfFilter == "TODOS",
                        onClick = { selectedProfFilter = "TODOS" },
                        label = { Text("Todos Profissionais", fontSize = 11.sp) }
                    )
                }
                items(professionals) { prof ->
                    val firstName = prof.name.split(" ").take(2).joinToString(" ")
                    FilterChip(
                        selected = selectedProfFilter == prof.name,
                        onClick = { selectedProfFilter = prof.name },
                        label = { Text(firstName, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Appointments List
            if (filteredAppointments.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nenhum horário encontrado com os filtros selecionados.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredAppointments) { appt ->
                        if (appt.isBlocked) {
                            // Blocked slot card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "Bloqueio de Horário",
                                        tint = StatusDangerText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${appt.time} • HORÁRIO BLOQUEADO (${appt.room})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = StatusDangerText
                                        )
                                        Text(
                                            text = appt.procedureName + if (appt.notes.isNotBlank()) " - ${appt.notes}" else "",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            scope.launch { repository.deleteAppointment(appt) }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Desbloquear Horário",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            AppointmentCardItem(
                                appointment = appt,
                                onStatusChange = { newStatus ->
                                    scope.launch {
                                        repository.updateAppointment(appt.copy(status = newStatus.name))
                                    }
                                },
                                onOpenRecord = {
                                    appt.clientId?.let { onNavigateToRecord(it) }
                                },
                                onOpenWhatsApp = {
                                    val msg = "Olá ${appt.clientName.split(" ").first()}, informamos seu agendamento de ${appt.procedureName} às ${appt.time} na Aura Estética."
                                    openWhatsAppMessage(context, appt.clientPhone, msg)
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // Modal for New Appointment
        if (showNewAppointmentModal) {
            ModalBottomSheet(
                onDismissRequest = { showNewAppointmentModal = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                var selectedClientName by remember { mutableStateOf(clients.firstOrNull()?.name ?: "") }
                var selectedPhone by remember { mutableStateOf(clients.firstOrNull()?.phone ?: "") }
                var selectedClientId by remember { mutableStateOf(clients.firstOrNull()?.id) }
                var selectedProc by remember { mutableStateOf(procedures.firstOrNull()?.name ?: "") }
                var selectedPrice by remember { mutableStateOf(procedures.firstOrNull()?.price ?: 0.0) }
                var selectedProf by remember { mutableStateOf(professionals.firstOrNull()?.name ?: "") }
                var selectedProfId by remember { mutableStateOf(professionals.firstOrNull()?.id ?: 1) }
                var selectedTime by remember { mutableStateOf("11:00") }
                var selectedRoom by remember { mutableStateOf("Sala 02 - Harmonização Facial") }
                var appointmentNotes by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Novo Agendamento Clínico",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Client Selection
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
                                    selectedPhone = c.phone
                                },
                                label = { Text(c.name.split(" ").take(2).joinToString(" "), fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Procedure Selection
                    Text("Procedimento:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        items(procedures) { p ->
                            FilterChip(
                                selected = selectedProc == p.name,
                                onClick = {
                                    selectedProc = p.name
                                    selectedPrice = p.price
                                },
                                label = { Text(p.name, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Professional Selection
                    Text("Profissional Responsável:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        items(professionals) { prof ->
                            FilterChip(
                                selected = selectedProfId == prof.id,
                                onClick = {
                                    selectedProfId = prof.id
                                    selectedProf = prof.name
                                },
                                label = { Text(prof.name.split(" ").first(), fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedTime,
                            onValueChange = { selectedTime = it },
                            label = { Text("Horário (HH:mm)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = selectedRoom,
                            onValueChange = { selectedRoom = it },
                            label = { Text("Sala / Consultório") },
                            modifier = Modifier.weight(1.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                repository.insertAppointment(
                                    AppointmentEntity(
                                        clientId = selectedClientId,
                                        clientName = selectedClientName,
                                        clientPhone = selectedPhone,
                                        professionalId = selectedProfId,
                                        professionalName = selectedProf,
                                        procedureId = null,
                                        procedureName = selectedProc,
                                        date = todayStr,
                                        time = selectedTime,
                                        status = AppointmentStatus.CONFIRMADO.name,
                                        room = selectedRoom,
                                        price = selectedPrice
                                    )
                                )
                                showNewAppointmentModal = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_create_appointment_button")
                    ) {
                        Text("Confirmar e Salvar Agendamento", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Modal for Slot Block
        if (showBlockSlotModal) {
            ModalBottomSheet(
                onDismissRequest = { showBlockSlotModal = false }
            ) {
                var blockReason by remember { mutableStateOf("Reunião Técnica / Alinhamento de Protocolo") }
                var blockTime by remember { mutableStateOf("12:00") }
                var blockRoom by remember { mutableStateOf("Sala 01 - Procedimentos Médicos") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Bloquear Horário na Grade",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusDangerText
                    )
                    Text(
                        text = "Impeça novos agendamentos neste intervalo para reuniões ou manutenção.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = blockReason,
                        onValueChange = { blockReason = it },
                        label = { Text("Motivo do Bloqueio") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = blockTime,
                            onValueChange = { blockTime = it },
                            label = { Text("Horário (HH:mm)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = blockRoom,
                            onValueChange = { blockRoom = it },
                            label = { Text("Sala") },
                            modifier = Modifier.weight(1.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                repository.insertAppointment(
                                    AppointmentEntity(
                                        clientId = null,
                                        clientName = "Horário Bloqueado",
                                        clientPhone = "",
                                        professionalId = 1,
                                        professionalName = "Equipe Técnica",
                                        procedureId = null,
                                        procedureName = blockReason,
                                        date = todayStr,
                                        time = blockTime,
                                        status = AppointmentStatus.CONFIRMADO.name,
                                        room = blockRoom,
                                        isBlocked = true
                                    )
                                )
                                showBlockSlotModal = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Salvar Bloqueio de Horário", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
