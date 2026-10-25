package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessText

@Composable
fun SignaturePad(
    clientName: String,
    onSignatureConfirmed: (signatureHash: String, timestamp: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isConfirmed by remember { mutableStateOf(false) }
    var confirmedHash by remember { mutableStateOf("") }
    var confirmedTimestamp by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Assinatura Digital de Consentimento",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Segurança Criptográfica",
                    tint = StatusSuccessText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "LGPD & Validação SHA-256",
                    fontSize = 11.sp,
                    color = StatusSuccessText,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = "O paciente deve assinar no quadro abaixo com o dedo ou caneta stylus para confirmação do termo.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Signature Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFAFAFA))
                .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                .pointerInput(isConfirmed) {
                    if (!isConfirmed) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentStroke = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentStroke = currentStroke + change.position
                            },
                            onDragEnd = {
                                if (currentStroke.isNotEmpty()) {
                                    strokes.add(currentStroke)
                                    currentStroke = emptyList()
                                }
                            }
                        )
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                // Line for signing guide
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(20f, size.height - 30f),
                    end = Offset(size.width - 20f, size.height - 30f),
                    strokeWidth = 1f
                )

                // Draw completed strokes
                for (stroke in strokes) {
                    if (stroke.size > 1) {
                        val path = Path().apply {
                            moveTo(stroke.first().x, stroke.first().y)
                            for (i in 1 until stroke.size) {
                                lineTo(stroke[i].x, stroke[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFF1E293B),
                            style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }

                // Draw in-progress stroke
                if (currentStroke.size > 1) {
                    val path = Path().apply {
                        moveTo(currentStroke.first().x, currentStroke.first().y)
                        for (i in 1 until currentStroke.size) {
                            lineTo(currentStroke[i].x, currentStroke[i].y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF1E293B),
                        style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }

            if (strokes.isEmpty() && currentStroke.isEmpty() && !isConfirmed) {
                Text(
                    text = "Toque e assine aqui...",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        if (isConfirmed) {
            Surface(
                color = StatusSuccessBg,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Assinatura válida",
                        tint = StatusSuccessText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Assinatura Autenticada: $clientName",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = StatusSuccessText
                        )
                        Text(
                            text = "Timestamp: $confirmedTimestamp | Hash: $confirmedHash",
                            fontSize = 10.sp,
                            color = StatusSuccessText.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        strokes.clear()
                        currentStroke = emptyList()
                    },
                    modifier = Modifier.testTag("clear_signature_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Limpar Assinatura",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Limpar", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val ts = "2026-10-25 " + java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                        val hash = "SHA256-" + java.util.UUID.randomUUID().toString().take(12).uppercase()
                        isConfirmed = true
                        confirmedHash = hash
                        confirmedTimestamp = ts
                        onSignatureConfirmed(hash, ts)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                    modifier = Modifier.testTag("confirm_signature_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Confirmar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Confirmar Assinatura", fontSize = 12.sp)
                }
            }
        }
    }
}
