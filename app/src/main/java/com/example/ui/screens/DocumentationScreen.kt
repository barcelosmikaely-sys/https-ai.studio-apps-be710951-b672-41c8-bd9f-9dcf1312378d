package com.example.ui.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.local.ClinicRepository
import com.example.data.local.UserEntity
import com.example.model.DocumentType
import com.example.ui.components.SectionHeader
import com.example.ui.components.SignaturePad
import com.example.ui.components.openWhatsAppMessage
import com.example.ui.theme.AccentAlternative
import com.example.ui.theme.AccentHighlight
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentationScreen(
    repository: ClinicRepository,
    currentUser: UserEntity,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clients by repository.clients.collectAsStateWithLifecycle(emptyList())

    var selectedDocType by remember { mutableStateOf(DocumentType.TCLE_BOTOX) }
    var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }

    val activeClient = selectedClient ?: clients.firstOrNull()
    val clientName = activeClient?.name ?: "Paciente"

    val documentText = when (selectedDocType) {
        DocumentType.TCLE_BOTOX -> """
            TERMO DE CONSENTIMENTO LIVRE E ESCLARECIDO - TOXINA BOTULÍNICA
            
            Eu, $clientName, declaro que fui devidamente esclarecido(a) pelos profissionais da Aura Estética sobre a aplicação de Toxina Botulínica Tipo A.
            
            1. OBJETIVO: Atenuação temporária de rugas hipercinéticas faciais.
            2. RISCOS E REAÇÕES ESPERADAS: Equimoses (manchas roxas), leve cefaleia transitória, discreto edema local ou assimetria corrigível em retorno.
            3. CUIDADOS PÓS: Não deitar por 4h, não massagear o rosto e não praticar exercícios intensos nas primeiras 24h.
            4. DECLARAÇÃO: Declaro não ser portador(a) de miastenia gravis, esclerose lateral amiotrófica, nem estar gestante ou lactante.
        """.trimIndent()

        DocumentType.TCLE_PREENCHIMENTO -> """
            TERMO DE CONSENTIMENTO LIVRE E ESCLARECIDO - PREENCHIMENTO COM ÁCIDO HIALURÔNICO
            
            Eu, $clientName, autorizo a realização de preenchimento dérmico com ácido hialurônico de grau estético e registro ANVISA.
            
            1. NATUREZA DO MATERIAL: Biomaterial reabsorvível e biocompatível com duração estimada de 8 a 18 meses.
            2. INTERCORRÊNCIAS PREVISTAS: Edema leve a moderado nos primeiros 3 dias, pequenas equimoses e sensibilidade tátil transitória.
            3. RASTREABILIDADE: O lote e validade da seringa utilizada foram registrados no meu prontuário eletrônico.
        """.trimIndent()

        DocumentType.TCLE_BIOESTIMULADOR -> """
            TERMO DE CONSENTIMENTO LIVRE E ESCLARECIDO - BIOESTIMULADOR DE COLÁGENO
            
            Eu, $clientName, recebi todas as orientações sobre a indução tecidual com bioestimulador (Radiesse / Sculptra).
            
            1. MECANISMO DE AÇÃO: Estímulo progressivo dos fibroblastos autólogos com resultados observados a partir de 30 a 90 dias.
            2. CONDUTA PÓS-PROCEDIMENTO: Realizar massagens suaves conforme a regra dos 5 (5 minutos, 5x ao dia, por 5 dias).
        """.trimIndent()

        DocumentType.ORIENTACOES_POS -> """
            MANUAL DE CUIDADOS PÓS-PROCEDIMENTO ESTÉTICO - AURA ESTÉTICA
            
            Prezado(a) $clientName:
            • Higienize a face delicadamente com sabonete neutro;
            • Não aplique cosméticos à base de ácidos nos primeiros 5 dias;
            • Utilize protetor solar com FPS 50+ a cada 3 horas mesmo em ambientes fechados;
            • Em caso de dúvidas ou intercorrências, nossa equipe médica está disponível pelo WhatsApp da clínica.
        """.trimIndent()

        DocumentType.CONTRATO_TRATAMENTO -> """
            CONTRATO DE PRESTAÇÃO DE SERVIÇOS ESTÉTICOS
            
            CONTRATADA: Aura Estética Avançada (CNPJ: 45.892.103/0001-92)
            CONTRATANTE: $clientName
            
            Cláusula 1ª: A Contratada se compromete a prestar os serviços estéticos acordados segundo a melhor técnica científica e normas sanitárias vigentes.
            Cláusula 2ª: A obrigação assumida na estética avançada é de meio, dependendo da resposta biológica, imunológica e estilo de vida individual do Contratante.
        """.trimIndent()
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
                    text = "Documentação & Termos Digitais",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "TCLE, orientações pós e contratos com assinatura e envio WhatsApp",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Document Type Selector
            item {
                Text(
                    text = "SELECIONE O DOCUMENTO:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    items(DocumentType.entries) { doc ->
                        val isSelected = selectedDocType == doc
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDocType = doc },
                            label = { Text(doc.title.split("-").first().trim(), fontSize = 11.sp) },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ActionPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Client Selector
            item {
                Text(
                    text = "PACIENTE VINCULADO:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    items(clients) { cl ->
                        val isSelected = activeClient?.id == cl.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedClient = cl },
                            label = { Text(cl.name.split(" ").take(2).joinToString(" "), fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Document Preview Card
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = ActionPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedDocType.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = documentText,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    activeClient?.let { cl ->
                                        val formattedMsg = "Olá ${cl.name.split(" ").first()}, segue seu termo digital da Aura Estética:\n\n$documentText\n\nDocumento emitido eletronicamente."
                                        openWhatsAppMessage(context, cl.phone, formattedMsg)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("send_term_whatsapp_button")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Enviar via WhatsApp", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Digital Signature Pad on the Document
            item {
                SectionHeader(
                    title = "Assinatura do Paciente no Termo",
                    subtitle = "Validação digital em conformidade com as diretrizes do CFM e Anvisa"
                )

                SignaturePad(
                    clientName = clientName,
                    onSignatureConfirmed = { _, _ -> }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
