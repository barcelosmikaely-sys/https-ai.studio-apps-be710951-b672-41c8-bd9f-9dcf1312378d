package com.example.ui.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.local.ClinicRepository
import com.example.data.local.UserEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.openWhatsAppMessage
import com.example.ui.theme.AccentAlternative
import com.example.ui.theme.AccentHighlight
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessText
import com.example.ui.theme.StatusWarningBg
import com.example.ui.theme.StatusWarningText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnsCrmScreen(
    repository: ClinicRepository,
    currentUser: UserEntity,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clients by repository.clients.collectAsStateWithLifecycle(emptyList())

    val templates = listOf(
        "CONFIRMACAO" to "Confirmação de Agendamento (D-1)",
        "CHECKIN_D1" to "Check-in Pós-Procedimento (D+1)",
        "CHECKIN_D7" to "Avaliação Pós-Procedimento (D+7)",
        "RETORNO_BOTOX" to "Lembrete Manutenção Botox (4 meses)",
        "RETORNO_BIO" to "Retorno Bioestimulador (45 dias)"
    )

    var selectedTemplateKey by remember { mutableStateOf("RETORNO_BOTOX") }
    var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }
    var customMessageText by remember { mutableStateOf("") }

    // Update message text when client or template changes
    val activeClient = selectedClient ?: clients.firstOrNull()
    val clientFirstName = activeClient?.name?.split(" ")?.firstOrNull() ?: "Paciente"

    val generatedMessage = when (selectedTemplateKey) {
        "CONFIRMACAO" -> "Olá, $clientFirstName! Tudo bem? Aqui é da Aura Estética. Lembramos do seu agendamento confirmado para amanhã na clínica. Pedimos que chegue 10 minutos antes. Podemos confirmar sua presença?"
        "CHECKIN_D1" -> "Olá, $clientFirstName! Passando para saber como você amanheceu após a aplicação do seu procedimento de ontem. Seguiu as recomendações de pós? Qualquer dúvida ou desconforto, estamos à disposição!"
        "CHECKIN_D7" -> "Olá, $clientFirstName! Completamos 7 dias do seu procedimento estético. Como está a evolução e cicatrização da região? Se desejar agendar a avaliação de retoque, é só nos avisar!"
        "RETORNO_BOTOX" -> "Olá, $clientFirstName! Já se passaram 4 meses desde a sua última aplicação de Toxina Botulínica na Aura Estética. Para manter o efeito de relaxamento muscular e prevenção contínua de rugas, sugerimos agendar sua renovação neste mês. Vamos reservar seu horário?"
        "RETORNO_BIO" -> "Olá, $clientFirstName! Seu protocolo de Bioestimulador de Colágeno prevê a próxima etapa após 45 dias para estímulo máximo de firmeza dérmica. Podemos verificar os melhores horários com a sua profissional?"
        else -> ""
    }

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
                Text(
                    text = "Retornos & CRM Automático",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Acompanhamento inteligente de pós-atendimento e manutenção",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Return Tracking List (Patients due for return)
            item {
                SectionHeader(
                    title = "Fila de Acompanhamento & Retornos Sugeridos",
                    subtitle = "Baseado no intervalo técnico dos procedimentos realizados"
                )
            }

            items(clients) { client ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
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
                                color = StatusWarningBg,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = StatusWarningText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = client.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Data Sugerida: ${client.nextSuggestedReturn}",
                                    fontSize = 12.sp,
                                    color = ActionPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Tel: ${client.phone}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = {
                                selectedClient = client
                                val msg = "Olá ${client.name.split(" ").first()}, notamos que sua data recomendada de retorno para avaliação estética é em ${client.nextSuggestedReturn}. Gostaria de agendar seu horário na Aura Estética?"
                                openWhatsAppMessage(context, client.phone, msg)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chamar", fontSize = 11.sp)
                        }
                    }
                }
            }

            // WhatsApp CRM Simulator & Auto Messenger
            item {
                SectionHeader(
                    title = "Simulador de Mensagens & Marketing WhatsApp",
                    subtitle = "Gere mensagens personalizadas e envie com 1 toque"
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Selecione o Modelo de Mensagem:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(templates) { (tKey, tLabel) ->
                                val isSelected = selectedTemplateKey == tKey
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedTemplateKey = tKey },
                                    label = { Text(tLabel, fontSize = 11.sp) },
                                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ActionPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // WhatsApp Chat Preview Bubble
                        Surface(
                            color = Color(0xFFEFEAE2), // WhatsApp chat background color
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Message,
                                        contentDescription = null,
                                        tint = Color(0xFF075E54),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Pré-visualização para ${activeClient?.name ?: "Paciente"}:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF075E54)
                                    )
                                }

                                // Message Bubble
                                Surface(
                                    color = Color(0xFFD9FDD3), // Outgoing message bubble color
                                    shape = RoundedCornerShape(10.dp),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = generatedMessage,
                                            fontSize = 13.sp,
                                            color = Color(0xFF111B21),
                                            lineHeight = 18.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "10:30 • Entregue",
                                            fontSize = 9.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                activeClient?.let { cl ->
                                    openWhatsAppMessage(context, cl.phone, generatedMessage)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("send_whatsapp_crm_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Disparar Mensagem via WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
