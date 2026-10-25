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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.ClinicConfigEntity
import com.example.data.local.ClinicRepository
import com.example.data.local.ProfessionalEntity
import com.example.data.local.UserEntity
import com.example.model.UserRole
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AccentAlternative
import com.example.ui.theme.AccentHighlight
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.StatusDangerBg
import com.example.ui.theme.StatusDangerText
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    repository: ClinicRepository,
    currentUser: UserEntity,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val config by repository.clinicConfig.collectAsStateWithLifecycle(null)
    val users by repository.users.collectAsStateWithLifecycle(emptyList())
    val professionals by repository.professionals.collectAsStateWithLifecycle(emptyList())

    val isAdmin = currentUser.role == "ADMIN"

    var clinicName by remember(config) { mutableStateOf(config?.clinicName ?: "Aura Estética Avançada") }
    var cnpj by remember(config) { mutableStateOf(config?.cnpj ?: "45.892.103/0001-92") }
    var phone by remember(config) { mutableStateOf(config?.phone ?: "(11) 98765-4321") }
    var address by remember(config) { mutableStateOf(config?.address ?: "Av. Higienópolis, 1200 - Conj. 82 - SP") }
    var openingHours by remember(config) { mutableStateOf(config?.openingHours ?: "Segunda a Sábado, das 08h às 20h") }

    var showSavedBanner by remember { mutableStateOf(false) }
    var showAddUserModal by remember { mutableStateOf(false) }
    var showAddProfModal by remember { mutableStateOf(false) }

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
                            text = "Configurações Gerais",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Dados da clínica, segurança, usuários e tema",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = onLogout,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sair", fontSize = 11.sp)
                    }
                }
            }

            // Theme Switcher Card
            item {
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
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (isDarkTheme) ActionPrimary else AccentHighlight.copy(alpha = 0.3f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = if (isDarkTheme) AccentHighlight else ActionPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isDarkTheme) "Tema Escuro de Luxo (Dark Luxury)" else "Tema Claro de Luxo (Off-White)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Paleta personalizada com grafite ardósia e acabamento taupe",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onToggleDarkTheme(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ActionPrimary,
                                checkedTrackColor = AccentHighlight
                            ),
                            modifier = Modifier.testTag("theme_switch")
                        )
                    }
                }
            }

            // Clinic Data (Admin Editable, Employee Readonly)
            item {
                SectionHeader(
                    title = "Dados da Clínica & Estabelecimento",
                    subtitle = if (isAdmin) "Informações corporativas e cabeçalho de termos" else "Visão informativa da clínica"
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = clinicName,
                            onValueChange = { clinicName = it },
                            label = { Text("Nome da Clínica") },
                            enabled = isAdmin,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = cnpj,
                                onValueChange = { cnpj = it },
                                label = { Text("CNPJ") },
                                enabled = isAdmin,
                                modifier = Modifier.weight(1.2f)
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Telefone / WhatsApp") },
                                enabled = isAdmin,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Endereço Completo") },
                            enabled = isAdmin,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = openingHours,
                            onValueChange = { openingHours = it },
                            label = { Text("Horário de Funcionamento") },
                            enabled = isAdmin,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        repository.updateClinicConfig(
                                            ClinicConfigEntity(
                                                id = 1,
                                                clinicName = clinicName,
                                                cnpj = cnpj,
                                                phone = phone,
                                                address = address,
                                                openingHours = openingHours
                                            )
                                        )
                                        showSavedBanner = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("save_clinic_config_button")
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Salvar Dados da Clínica", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (showSavedBanner) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = StatusSuccessBg,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Dados atualizados com sucesso no banco de dados local!",
                                    color = StatusSuccessText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // System Users (Admin Only)
            if (isAdmin) {
                item {
                    SectionHeader(
                        title = "Usuários do Sistema (RBAC)",
                        subtitle = "Controle de papéis: Administrador (Dono) vs Funcionário",
                        actionText = "+ Novo Usuário",
                        onActionClick = { showAddUserModal = true }
                    )
                }

                items(users) { user ->
                    val isUserAdmin = user.role == "ADMIN"
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
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isUserAdmin) ActionPrimary else AccentHighlight.copy(alpha = 0.3f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (isUserAdmin) AccentHighlight else ActionPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = user.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = user.email,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                color = if (isUserAdmin) StatusSuccessBg else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isUserAdmin) "ADM" else "FUNCIONÁRIO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUserAdmin) StatusSuccessText else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Professionals Team
            item {
                SectionHeader(
                    title = "Equipe de Profissionais & Especialistas",
                    subtitle = "Escala de horários, consultórios e taxas contratuais de comissão",
                    actionText = if (isAdmin) "+ Novo Profissional" else null,
                    onActionClick = { showAddProfModal = true }
                )
            }

            items(professionals) { prof ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prof.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                color = AccentHighlight.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Comissão: ${(prof.commissionRate * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ActionPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = prof.specialty,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Horário: ${prof.availableHours} • ${prof.activeRooms}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = AccentAlternative
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // Modal for Adding User (Admin Only)
        if (showAddUserModal && isAdmin) {
            ModalBottomSheet(
                onDismissRequest = { showAddUserModal = false }
            ) {
                var newName by remember { mutableStateOf("") }
                var newEmail by remember { mutableStateOf("") }
                var newPin by remember { mutableStateOf("1234") }
                var newPhone by remember { mutableStateOf("") }
                var newRole by remember { mutableStateOf("EMPLOYEE") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Cadastrar Novo Usuário do Sistema",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nome Completo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("E-mail de Login") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newPin,
                            onValueChange = { newPin = it },
                            label = { Text("Senha / PIN") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newPhone,
                            onValueChange = { newPhone = it },
                            label = { Text("Telefone") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Papel de Acesso (RBAC):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = newRole == "EMPLOYEE",
                            onClick = { newRole = "EMPLOYEE" },
                            label = { Text("Funcionário / Esteta", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = newRole == "ADMIN",
                            onClick = { newRole = "ADMIN" },
                            label = { Text("Administrador (Dono)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (newName.isNotBlank() && newEmail.isNotBlank()) {
                                scope.launch {
                                    repository.insertUser(
                                        UserEntity(
                                            name = newName,
                                            email = newEmail.trim().lowercase(),
                                            passwordHash = repository.hashPassword(newPin),
                                            role = newRole,
                                            phone = newPhone
                                        )
                                    )
                                    showAddUserModal = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Criar Usuário Autorizado", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Modal for Adding Professional
        if (showAddProfModal && isAdmin) {
            ModalBottomSheet(
                onDismissRequest = { showAddProfModal = false }
            ) {
                var profName by remember { mutableStateOf("") }
                var specialty by remember { mutableStateOf("Farmacêutica Esteta") }
                var profPhone by remember { mutableStateOf("(11) 98888-7777") }
                var commissionText by remember { mutableStateOf("30") }
                var availableHours by remember { mutableStateOf("09:00 - 18:00") }
                var activeRooms by remember { mutableStateOf("Sala 02 - Harmonização") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Novo Profissional da Equipe",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = profName,
                        onValueChange = { profName = it },
                        label = { Text("Nome do Profissional") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = specialty,
                        onValueChange = { specialty = it },
                        label = { Text("Especialidade Clínica") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = commissionText,
                            onValueChange = { commissionText = it },
                            label = { Text("Comissão (%)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = profPhone,
                            onValueChange = { profPhone = it },
                            label = { Text("Telefone") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = availableHours,
                        onValueChange = { availableHours = it },
                        label = { Text("Horário de Atendimento") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val rate = (commissionText.toDoubleOrNull() ?: 30.0) / 100.0
                            if (profName.isNotBlank()) {
                                scope.launch {
                                    repository.insertProfessional(
                                        ProfessionalEntity(
                                            name = profName,
                                            specialty = specialty,
                                            phone = profPhone,
                                            commissionRate = rate,
                                            availableHours = availableHours,
                                            activeRooms = activeRooms
                                        )
                                    )
                                    showAddProfModal = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Salvar Profissional", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
