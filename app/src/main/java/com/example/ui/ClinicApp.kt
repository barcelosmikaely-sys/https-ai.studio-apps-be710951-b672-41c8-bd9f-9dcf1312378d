package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ClinicRepository
import com.example.data.local.UserEntity
import com.example.ui.screens.AgendaScreen
import com.example.ui.screens.ClientsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DocumentationScreen
import com.example.ui.screens.FinancialScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MedicalRecordScreen
import com.example.ui.screens.PackagesScreen
import com.example.ui.screens.ProceduresScreen
import com.example.ui.screens.ReturnsCrmScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AccentAlternative
import com.example.ui.theme.AccentHighlight
import com.example.ui.theme.ActionPrimary
import com.example.ui.theme.AuraEsteticaTheme
import com.example.ui.theme.BorderLight
import com.example.ui.theme.StatusDangerBg
import com.example.ui.theme.StatusDangerText
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusSuccessText
import kotlinx.coroutines.launch

enum class ClinicNavScreen(val title: String, val icon: ImageVector, val adminOnly: Boolean = false) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    AGENDA("Agenda", Icons.Default.CalendarMonth),
    PRONTUARIO("Prontuário", Icons.Default.MedicalServices),
    PROCEDIMENTOS("Procedimentos", Icons.Default.Spa),
    PACOTES("Pacotes", Icons.Default.Assignment),
    CLIENTES("Clientes", Icons.Default.People),
    RETORNOS("Retorno & CRM", Icons.Default.Schedule),
    DOCUMENTOS("Documentação", Icons.Default.Description),
    ESTOQUE("Estoque", Icons.Default.Inventory2),
    FINANCEIRO("Financeiro", Icons.Default.AttachMoney),
    CONFIG("Configurações", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicApp(repository: ClinicRepository) {
    var currentUser by remember { mutableStateOf<UserEntity?>(null) }
    var currentScreen by remember { mutableStateOf(ClinicNavScreen.DASHBOARD) }
    var isDarkTheme by remember { mutableStateOf(false) }
    var isQuickLocked by remember { mutableStateOf(false) }
    var recordTargetClientId by remember { mutableStateOf<Int?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AuraEsteticaTheme(darkTheme = isDarkTheme) {
        if (currentUser == null) {
            LoginScreen(
                repository = repository,
                onLoginSuccess = { user ->
                    currentUser = user
                    currentScreen = ClinicNavScreen.DASHBOARD
                }
            )
        } else if (isQuickLocked) {
            // Quick PIN Screen Lock for Privacy Compliance
            QuickPinLockScreen(
                currentUser = currentUser!!,
                onUnlock = { isQuickLocked = false }
            )
        } else {
            // Handle Back Button
            BackHandler(enabled = currentScreen != ClinicNavScreen.DASHBOARD) {
                currentScreen = ClinicNavScreen.DASHBOARD
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.width(300.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(16.dp)
                        ) {
                            // Drawer Header with Logo
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(ActionPrimary)
                                        .border(1.dp, AccentHighlight, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_aura_logo),
                                        contentDescription = "Aura Logo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "AURA ESTÉTICA",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        letterSpacing = 1.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = currentUser?.name ?: "",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Navigation Items
                            ClinicNavScreen.entries.forEach { screen ->
                                val isSelected = currentScreen == screen
                                val isRestricted = screen.adminOnly && currentUser?.role != "ADMIN"

                                if (!isRestricted) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) ActionPrimary else Color.Transparent,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .clickable {
                                                currentScreen = screen
                                                scope.launch { drawerState.close() }
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = screen.icon,
                                                contentDescription = screen.title,
                                                tint = if (isSelected) AccentHighlight else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = screen.title,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Quick lock and logout buttons at drawer bottom
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable { isQuickLocked = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = ActionPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Bloquear", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StatusDangerBg,
                                    modifier = Modifier.clickable { currentUser = null }
                                ) {
                                    Text(
                                        text = "Encerrar Sessão",
                                        color = StatusDangerText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentScreen.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("drawer_menu_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu de Navegação",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            actions = {
                                // Role Badge
                                Surface(
                                    color = if (currentUser?.role == "ADMIN") AccentHighlight.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = if (currentUser?.role == "ADMIN") "ADM" else "ESTETA",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ActionPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                // Quick Screen Lock Icon (LGPD)
                                IconButton(
                                    onClick = { isQuickLocked = true },
                                    modifier = Modifier.testTag("quick_lock_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Bloqueio Rápido de Segurança",
                                        tint = ActionPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        // Quick Primary Tab Bar for Mobile Flow
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp
                        ) {
                            val bottomScreens = listOf(
                                ClinicNavScreen.DASHBOARD,
                                ClinicNavScreen.AGENDA,
                                ClinicNavScreen.PRONTUARIO,
                                ClinicNavScreen.CLIENTES,
                                ClinicNavScreen.FINANCEIRO
                            )

                            bottomScreens.forEach { screen ->
                                val isSelected = currentScreen == screen
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentScreen = screen },
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = AccentHighlight,
                                        selectedTextColor = ActionPrimary,
                                        indicatorColor = ActionPrimary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            ClinicNavScreen.DASHBOARD -> DashboardScreen(
                                repository = repository,
                                currentUser = currentUser!!,
                                onNavigateToAgenda = { currentScreen = ClinicNavScreen.AGENDA },
                                onNavigateToRecord = { clientId ->
                                    recordTargetClientId = clientId
                                    currentScreen = ClinicNavScreen.PRONTUARIO
                                },
                                onNavigateToReturns = { currentScreen = ClinicNavScreen.RETORNOS },
                                onNavigateToInventory = { currentScreen = ClinicNavScreen.ESTOQUE }
                            )

                            ClinicNavScreen.AGENDA -> AgendaScreen(
                                repository = repository,
                                currentUser = currentUser!!,
                                onNavigateToRecord = { clientId ->
                                    recordTargetClientId = clientId
                                    currentScreen = ClinicNavScreen.PRONTUARIO
                                }
                            )

                            ClinicNavScreen.PRONTUARIO -> MedicalRecordScreen(
                                repository = repository,
                                currentUser = currentUser!!,
                                initialClientId = recordTargetClientId
                            )

                            ClinicNavScreen.PROCEDIMENTOS -> ProceduresScreen(
                                repository = repository,
                                currentUser = currentUser!!
                            )

                            ClinicNavScreen.PACOTES -> PackagesScreen(
                                repository = repository,
                                currentUser = currentUser!!
                            )

                            ClinicNavScreen.CLIENTES -> ClientsScreen(
                                repository = repository,
                                currentUser = currentUser!!,
                                onNavigateToRecord = { clientId ->
                                    recordTargetClientId = clientId
                                    currentScreen = ClinicNavScreen.PRONTUARIO
                                }
                            )

                            ClinicNavScreen.RETORNOS -> ReturnsCrmScreen(
                                repository = repository,
                                currentUser = currentUser!!
                            )

                            ClinicNavScreen.DOCUMENTOS -> DocumentationScreen(
                                repository = repository,
                                currentUser = currentUser!!
                            )

                            ClinicNavScreen.ESTOQUE -> InventoryScreen(
                                repository = repository,
                                currentUser = currentUser!!
                            )

                            ClinicNavScreen.FINANCEIRO -> FinancialScreen(
                                repository = repository,
                                currentUser = currentUser!!
                            )

                            ClinicNavScreen.CONFIG -> SettingsScreen(
                                repository = repository,
                                currentUser = currentUser!!,
                                isDarkTheme = isDarkTheme,
                                onToggleDarkTheme = { isDarkTheme = it },
                                onLogout = { currentUser = null }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickPinLockScreen(
    currentUser: UserEntity,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ActionPrimary),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .width(320.dp)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = ActionPrimary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = AccentHighlight,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Terminal Bloqueado",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Digite seu PIN para retomar a sessão de ${currentUser.name.split(" ").first()}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )

                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        pin = it
                        pinError = false
                        if (it == "1234") {
                            onUnlock()
                        }
                    },
                    label = { Text("PIN de Acesso (1234)") },
                    singleLine = true,
                    isError = pinError,
                    modifier = Modifier.fillMaxWidth()
                )

                if (pinError) {
                    Text(
                        text = "PIN incorreto. Tente novamente.",
                        color = StatusDangerText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (pin == "1234") {
                            onUnlock()
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Desbloquear Terminal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
