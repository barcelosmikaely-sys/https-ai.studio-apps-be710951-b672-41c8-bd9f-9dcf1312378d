package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.openWhatsAppMessage
import com.example.ui.theme.AccentAlternative
import com.example.ui.theme.AccentHighlight
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.StatusDangerBg
import com.example.ui.theme.StatusDangerText
import com.example.ui.theme.StatusInfoBg
import com.example.ui.theme.StatusInfoText
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessText
import com.example.ui.theme.StatusWarningBg
import com.example.ui.theme.StatusWarningText
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    repository: ClinicRepository,
    currentUser: UserEntity,
    onNavigateToAgenda: () -> Unit,
    onNavigateToRecord: (clientId: Int) -> Unit,
    onNavigateToReturns: () -> Unit,
    onNavigateToInventory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isAdmin = currentUser.role == "ADMIN"

    val appointments by repository.appointments.collectAsStateWithLifecycle(emptyList())
    val clients by repository.clients.collectAsStateWithLifecycle(emptyList())
    val inventory by repository.inventory.collectAsStateWithLifecycle(emptyList())
    val transactions by repository.transactions.collectAsStateWithLifecycle(emptyList())

    val ptBr = Locale("pt", "BR")
    val currencyFormatter = NumberFormat.getCurrencyInstance(ptBr)

    // Metrics calculations
    val todayAppointments = appointments.filter { !it.isBlocked }
    val completedToday = todayAppointments.filter { it.status == AppointmentStatus.CONCLUIDO.name }
    val inProgress = todayAppointments.filter { it.status == AppointmentStatus.EM_ATENDIMENTO.name }

    val todayRevenue = completedToday.sumOf { it.price }
    val monthRevenue = transactions.filter { it.type == "RECEITA" }.sumOf { it.amount }
    val myAppointments = if (isAdmin) todayAppointments else todayAppointments.filter { it.professionalName.contains(currentUser.name, ignoreCase = true) || it.professionalId == currentUser.professionalId }
    val myCommission = completedToday
        .filter { it.professionalId == currentUser.professionalId || it.professionalName.contains(currentUser.name, ignoreCase = true) }
        .sumOf { it.price * 0.35 }

    // Inventory alerts count
    val lowStockCount = inventory.count { it.currentQuantity <= it.minimumQuantity }

    var selectedChartBarIndex by remember { mutableIntStateOf(5) } // Friday by default
    val daysOfWeek = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
    val dailyRevenueData = listOf(3400f, 4800f, 3950f, 6200f, 7850f, 8900f, 2100f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Olá, ${currentUser.name.split(" ").first()}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isAdmin) "Visão Geral Executiva • Gestão & Faturamento" else "Painel Operacional • Meus Atendimentos",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isAdmin) AccentHighlight.copy(alpha = 0.3f) else StatusInfoBg,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAdmin) ActionPrimary.copy(alpha = 0.3f) else StatusInfoText.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = if (isAdmin) "DIRETORIA (ADM)" else "PROFISSIONAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAdmin) ActionPrimary else StatusInfoText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Quick Alert Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (lowStockCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = StatusDangerBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusDangerText.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToInventory() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = StatusDangerText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$lowStockCount itens em estoque baixo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StatusDangerText
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = StatusWarningBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusWarningText.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToReturns() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = StatusWarningText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "5 retornos previstos",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StatusWarningText
                        )
                    }
                }
            }
        }

        // KPI Cards Grid
        item {
            if (isAdmin) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "RECEITA HOJE",
                            value = currencyFormatter.format(todayRevenue),
                            subtitle = "${completedToday.size} concluídos hoje",
                            icon = Icons.Default.AttachMoney,
                            iconTint = StatusSuccessText,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "FATURAMENTO MÊS",
                            value = currencyFormatter.format(monthRevenue),
                            subtitle = "+18% vs mês anterior",
                            icon = Icons.Default.TrendingUp,
                            iconTint = ActionPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "AGENDAMENTOS HOJE",
                            value = "${todayAppointments.size}",
                            subtitle = "${inProgress.size} em atendimento",
                            icon = Icons.Default.CalendarMonth,
                            iconTint = AccentAlternative,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "CLIENTES ATIVOS",
                            value = "${clients.size}",
                            subtitle = "LTV Médio R$ 2.450",
                            icon = Icons.Default.Face,
                            iconTint = ActionPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // Employee view: commission and personal workload
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "MEUS PACIENTES HOJE",
                        value = "${myAppointments.size}",
                        subtitle = "${completedToday.size} já atendidos",
                        icon = Icons.Default.MedicalServices,
                        iconTint = ActionPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "MINHA COMISSÃO HOJE",
                        value = currencyFormatter.format(myCommission),
                        subtitle = "35% sobre injetáveis",
                        icon = Icons.Default.AttachMoney,
                        iconTint = StatusSuccessText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Interactive Revenue Chart (Admin Only)
        if (isAdmin) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Faturamento da Semana",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Toque em uma barra para detalhes",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${daysOfWeek[selectedChartBarIndex]}: ${currencyFormatter.format(dailyRevenueData[selectedChartBarIndex])}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ActionPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom Interactive Bar Chart
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        ) {
                            val maxVal = (dailyRevenueData.maxOrNull() ?: 10000f) * 1.15f
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                dailyRevenueData.forEachIndexed { index, value ->
                                    val isSelected = index == selectedChartBarIndex
                                    val barHeightRatio = (value / maxVal).coerceIn(0.1f, 1f)

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedChartBarIndex = index }
                                            .padding(horizontal = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height((100 * barHeightRatio).dp)
                                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                                .background(
                                                    if (isSelected) ActionPrimary else AccentHighlight.copy(
                                                        alpha = 0.45f
                                                    )
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = daysOfWeek[index],
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) ActionPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Appointments List Section
        item {
            SectionHeader(
                title = "Agenda do Dia",
                subtitle = "${myAppointments.size} atendimentos programados",
                actionText = "Ver Grade Completa",
                onActionClick = onNavigateToAgenda
            )
        }

        if (myAppointments.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Nenhum agendamento programado para hoje.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        } else {
            items(myAppointments) { appointment ->
                AppointmentCardItem(
                    appointment = appointment,
                    onStatusChange = { newStatus ->
                        scope.launch {
                            repository.updateAppointment(appointment.copy(status = newStatus.name))
                        }
                    },
                    onOpenRecord = {
                        appointment.clientId?.let { clientId ->
                            onNavigateToRecord(clientId)
                        }
                    },
                    onOpenWhatsApp = {
                        val message = "Olá ${appointment.clientName.split(" ").first()}, tudo bem? Confirmamos seu procedimento de ${appointment.procedureName} hoje às ${appointment.time} na Aura Estética. Estamos à sua disposição!"
                        openWhatsAppMessage(context, appointment.clientPhone, message)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AppointmentCardItem(
    appointment: AppointmentEntity,
    onStatusChange: (AppointmentStatus) -> Unit,
    onOpenRecord: () -> Unit,
    onOpenWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ActionPrimary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = appointment.time,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = appointment.room,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = appointment.clientName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = appointment.procedureName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = AccentAlternative,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = "Profissional: ${appointment.professionalName}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (appointment.status) {
                    AppointmentStatus.CONFIRMADO.name, AppointmentStatus.AGUARDANDO.name -> {
                        Button(
                            onClick = { onStatusChange(AppointmentStatus.EM_ATENDIMENTO) },
                            colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Atender", fontSize = 12.sp)
                        }
                    }
                    AppointmentStatus.EM_ATENDIMENTO.name -> {
                        Button(
                            onClick = { onStatusChange(AppointmentStatus.CONCLUIDO) },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccessText),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Concluir", fontSize = 12.sp)
                        }
                    }
                    else -> {}
                }

                OutlinedButton(
                    onClick = onOpenRecord,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text("Prontuário", fontSize = 12.sp)
                }

                if (appointment.clientPhone.isNotBlank()) {
                    OutlinedButton(
                        onClick = onOpenWhatsApp,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("WhatsApp", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
