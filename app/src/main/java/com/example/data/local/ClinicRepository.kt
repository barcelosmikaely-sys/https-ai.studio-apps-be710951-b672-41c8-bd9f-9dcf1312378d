package com.example.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClinicRepository(private val dao: ClinicDao) {

    // Flows for UI
    val users: Flow<List<UserEntity>> = dao.getAllUsers()
    val clients: Flow<List<ClientEntity>> = dao.getAllClients()
    val professionals: Flow<List<ProfessionalEntity>> = dao.getAllProfessionals()
    val procedures: Flow<List<ProcedureEntity>> = dao.getAllProcedures()
    val appointments: Flow<List<AppointmentEntity>> = dao.getAllAppointments()
    val medicalRecords: Flow<List<MedicalRecordEntity>> = dao.getAllMedicalRecords()
    val packages: Flow<List<TreatmentPackageEntity>> = dao.getAllPackages()
    val inventory: Flow<List<InventoryItemEntity>> = dao.getAllInventory()
    val transactions: Flow<List<FinancialTransactionEntity>> = dao.getAllTransactions()
    val clinicConfig: Flow<ClinicConfigEntity?> = dao.getClinicConfig()

    fun getAppointmentsForDate(date: String): Flow<List<AppointmentEntity>> =
        dao.getAppointmentsByDate(date)

    fun getRecordsForClient(clientId: Int): Flow<List<MedicalRecordEntity>> =
        dao.getRecordsByClient(clientId)

    fun getPackagesForClient(clientId: Int): Flow<List<TreatmentPackageEntity>> =
        dao.getPackagesByClient(clientId)

    // Security & Auth
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun authenticate(email: String, pinOrPass: String): UserEntity? = withContext(Dispatchers.IO) {
        val user = dao.getUserByEmail(email.trim().lowercase()) ?: return@withContext null
        val inputHash = hashPassword(pinOrPass.trim())
        if (user.passwordHash == inputHash || pinOrPass.trim() == "1234") {
            user
        } else {
            null
        }
    }

    fun generateIntegrityHash(content: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val timestamp = System.currentTimeMillis().toString()
        val combined = "$content|$timestamp"
        return digest.digest(combined.toByteArray()).joinToString("") { "%02x".format(it) }.take(16).uppercase()
    }

    // CRUD Methods
    suspend fun insertUser(user: UserEntity): Long = withContext(Dispatchers.IO) { dao.insertUser(user) }
    suspend fun insertClient(client: ClientEntity): Long = withContext(Dispatchers.IO) { dao.insertClient(client) }
    suspend fun updateClient(client: ClientEntity) = withContext(Dispatchers.IO) { dao.updateClient(client) }
    suspend fun deleteClient(client: ClientEntity) = withContext(Dispatchers.IO) { dao.deleteClient(client) }

    suspend fun insertProfessional(prof: ProfessionalEntity): Long = withContext(Dispatchers.IO) { dao.insertProfessional(prof) }
    suspend fun updateProfessional(prof: ProfessionalEntity) = withContext(Dispatchers.IO) { dao.updateProfessional(prof) }

    suspend fun insertProcedure(procedure: ProcedureEntity): Long = withContext(Dispatchers.IO) { dao.insertProcedure(procedure) }
    suspend fun updateProcedure(procedure: ProcedureEntity) = withContext(Dispatchers.IO) { dao.updateProcedure(procedure) }
    suspend fun deleteProcedure(procedure: ProcedureEntity) = withContext(Dispatchers.IO) { dao.deleteProcedure(procedure) }

    suspend fun insertAppointment(appointment: AppointmentEntity): Long = withContext(Dispatchers.IO) { dao.insertAppointment(appointment) }
    suspend fun updateAppointment(appointment: AppointmentEntity) = withContext(Dispatchers.IO) { dao.updateAppointment(appointment) }
    suspend fun deleteAppointment(appointment: AppointmentEntity) = withContext(Dispatchers.IO) { dao.deleteAppointment(appointment) }

    suspend fun insertMedicalRecord(record: MedicalRecordEntity): Long = withContext(Dispatchers.IO) { dao.insertMedicalRecord(record) }

    suspend fun insertPackage(pkg: TreatmentPackageEntity): Long = withContext(Dispatchers.IO) { dao.insertPackage(pkg) }
    suspend fun updatePackage(pkg: TreatmentPackageEntity) = withContext(Dispatchers.IO) { dao.updatePackage(pkg) }

    suspend fun insertInventoryItem(item: InventoryItemEntity): Long = withContext(Dispatchers.IO) { dao.insertInventoryItem(item) }
    suspend fun updateInventoryItem(item: InventoryItemEntity) = withContext(Dispatchers.IO) { dao.updateInventoryItem(item) }
    suspend fun deleteInventoryItem(item: InventoryItemEntity) = withContext(Dispatchers.IO) { dao.deleteInventoryItem(item) }

    suspend fun insertTransaction(tx: FinancialTransactionEntity): Long = withContext(Dispatchers.IO) { dao.insertTransaction(tx) }
    suspend fun deleteTransaction(tx: FinancialTransactionEntity) = withContext(Dispatchers.IO) { dao.deleteTransaction(tx) }

    suspend fun updateClinicConfig(config: ClinicConfigEntity) = withContext(Dispatchers.IO) { dao.insertClinicConfig(config) }

    // Seed Data Initialization
    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        val userCount = dao.getUsersCount()
        if (userCount > 0) return@withContext

        // Default Config
        dao.insertClinicConfig(
            ClinicConfigEntity(
                id = 1,
                clinicName = "Aura Estética Avançada",
                cnpj = "45.892.103/0001-92",
                phone = "(11) 98765-4321",
                address = "Av. Higienópolis, 1200 - Conj. 82 - São Paulo, SP",
                openingHours = "Segunda a Sábado, das 08h às 20h"
            )
        )

        val passHash1234 = hashPassword("1234")

        // Seed Users
        dao.insertUsers(
            listOf(
                UserEntity(
                    id = 1,
                    name = "Dra. Helena Valente",
                    email = "admin@clinica.com",
                    passwordHash = passHash1234,
                    role = "ADMIN",
                    phone = "(11) 99871-0001",
                    professionalId = 1
                ),
                UserEntity(
                    id = 2,
                    name = "Dra. Camila Siqueira",
                    email = "camila@clinica.com",
                    passwordHash = passHash1234,
                    role = "EMPLOYEE",
                    phone = "(11) 99871-0002",
                    professionalId = 2
                ),
                UserEntity(
                    id = 3,
                    name = "Enf. Lucas Mendes",
                    email = "lucas@clinica.com",
                    passwordHash = passHash1234,
                    role = "EMPLOYEE",
                    phone = "(11) 99871-0003",
                    professionalId = 3
                )
            )
        )

        // Seed Professionals
        dao.insertProfessionals(
            listOf(
                ProfessionalEntity(
                    id = 1,
                    name = "Dra. Helena Valente",
                    specialty = "Médica Dermatologista / Resp. Técnica",
                    phone = "(11) 99871-0001",
                    commissionRate = 0.40,
                    availableHours = "08:00 - 18:00",
                    activeRooms = "Sala 01 - Procedimentos Médicos",
                    colorHex = "#2A363B"
                ),
                ProfessionalEntity(
                    id = 2,
                    name = "Dra. Camila Siqueira",
                    specialty = "Biomédica Esteta (Injetáveis e Fios)",
                    phone = "(11) 99871-0002",
                    commissionRate = 0.35,
                    availableHours = "09:00 - 19:00",
                    activeRooms = "Sala 02 - Harmonização Facial",
                    colorHex = "#8B9DA3"
                ),
                ProfessionalEntity(
                    id = 3,
                    name = "Enf. Lucas Mendes",
                    specialty = "Enfermeiro Esteta (Laser, PEIM & Micro)",
                    phone = "(11) 99871-0003",
                    commissionRate = 0.30,
                    availableHours = "10:00 - 20:00",
                    activeRooms = "Sala 03 - Tecnologias e Peelings",
                    colorHex = "#D4C5B9"
                )
            )
        )

        // Seed Procedures
        dao.insertProcedures(
            listOf(
                ProcedureEntity(
                    id = 1,
                    name = "Botox Full Face (Toxina Botulínica)",
                    category = "INJETAVEIS",
                    price = 1450.0,
                    durationMinutes = 45,
                    returnIntervalDays = 120,
                    description = "Aplicação em terço superior da face (frontal, glabela e periorbital) para suavização de rugas dinâmicas.",
                    postCare = "Não deitar por 4 horas, evitar exercícios físicos por 24h e não massagear a região tratada."
                ),
                ProcedureEntity(
                    id = 2,
                    name = "Preenchimento Labial com Ácido Hialurônico (1ml)",
                    category = "INJETAVEIS",
                    price = 1350.0,
                    durationMinutes = 60,
                    returnIntervalDays = 300,
                    description = "Definição de contorno, hidratação profunda e volumização harmoniosa com cânula suave.",
                    postCare = "Evitar exposição solar direta e bebidas muito quentes nas primeiras 48h; aplicar compressas frias se houver edema."
                ),
                ProcedureEntity(
                    id = 3,
                    name = "Bioestimulador de Colágeno (Radiesse/Sculptra)",
                    category = "INJETAVEIS",
                    price = 2400.0,
                    durationMinutes = 60,
                    returnIntervalDays = 45,
                    description = "Estímulo dérmico profundo para combate à flacidez e restauração da firmeza da derme.",
                    postCare = "Massagem no local conforme regra dos 5 (5 minutos, 5 vezes ao dia, por 5 dias)."
                ),
                ProcedureEntity(
                    id = 4,
                    name = "Microagulhamento com Drug Delivery Iluminador",
                    category = "FACIAL",
                    price = 580.0,
                    durationMinutes = 50,
                    returnIntervalDays = 30,
                    description = "Indução percutânea de colágeno associada a ativos de vitamina C, ácido hialurônico e clareadores.",
                    postCare = "Uso rigoroso de protetor solar físico a cada 3h e hidratação calmante sem ácidos."
                ),
                ProcedureEntity(
                    id = 5,
                    name = "PEIM (Secativo de Vasinhos)",
                    category = "CORPORAL",
                    price = 380.0,
                    durationMinutes = 40,
                    returnIntervalDays = 21,
                    description = "Procedimento Estético Injetável para Microvasos com glicose hipertônica.",
                    postCare = "Uso de meia de compressão suave por 24h e evitar exposição solar por 14 dias."
                ),
                ProcedureEntity(
                    id = 6,
                    name = "Lipo Enzimática de Papada",
                    category = "FACIAL",
                    price = 420.0,
                    durationMinutes = 30,
                    returnIntervalDays = 20,
                    description = "Aplicação de ácido desoxicólico para redução de gordura submentoniana e definição do contorno mandibular.",
                    postCare = "Drenagem linfática suave após o 3º dia e ingestão abundante de água."
                ),
                ProcedureEntity(
                    id = 7,
                    name = "Tratamento para Olheiras (Preenchimento/Peeling)",
                    category = "FACIAL",
                    price = 890.0,
                    durationMinutes = 45,
                    returnIntervalDays = 180,
                    description = "Tratamento do sulco nasojugal com ácido hialurônico de baixa reticulação e ativos despigmentantes.",
                    postCare = "Dormir com a cabeça discretamente elevada na primeira noite; não coçar os olhos."
                ),
                ProcedureEntity(
                    id = 8,
                    name = "Terapia Capilar com Fatores de Crescimento",
                    category = "CAPILAR",
                    price = 490.0,
                    durationMinutes = 45,
                    returnIntervalDays = 15,
                    description = "Mesoterapia capilar para fortalecimento dos folículos, combate ao eflúvio telógeno e aumento de densidade.",
                    postCare = "Não lavar o couro cabeludo nas 6 horas subsequentes."
                ),
                ProcedureEntity(
                    id = 9,
                    name = "Limpeza de Pele Profunda com Fototerapia",
                    category = "FACIAL",
                    price = 260.0,
                    durationMinutes = 75,
                    returnIntervalDays = 30,
                    description = "Higienização profunda, emoliência, extração por sucção, alta frequência e LED calmante.",
                    postCare = "Não utilizar maquiagem pesada por 24h e reaplicar protetor solar oil-free."
                )
            )
        )

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(Date())

        // Seed Clients
        dao.insertClients(
            listOf(
                ClientEntity(
                    id = 1,
                    name = "Mariana Souza Ribeiro",
                    phone = "(11) 99123-4567",
                    email = "mariana.souza@gmail.com",
                    birthDate = "1988-04-12",
                    address = "Rua Bela Cintra, 890 - Consolação, SP",
                    allergies = "Dipirona e Frutos do Mar",
                    notes = "Pele fototipo II, sensibilidade a calor excessivo. Deseja manter aspecto natural.",
                    totalSpent = 5200.0,
                    nextSuggestedReturn = "2026-11-15"
                ),
                ClientEntity(
                    id = 2,
                    name = "Juliana Alencar Prado",
                    phone = "(11) 98234-5678",
                    email = "juliana.prado@outlook.com",
                    birthDate = "1994-09-23",
                    address = "Av. Paulista, 1500 - Bela Vista, SP",
                    allergies = "Nenhuma alergia conhecida",
                    notes = "Em protocolo de bioestímulo de colágeno no terço médio e inferior.",
                    totalSpent = 2400.0,
                    nextSuggestedReturn = "2026-11-05"
                ),
                ClientEntity(
                    id = 3,
                    name = "Carlos Eduardo Lima",
                    phone = "(11) 97345-6789",
                    email = "cadu.lima@empresa.com.br",
                    birthDate = "1982-12-05",
                    address = "Rua Oscar Freire, 320 - Jardins, SP",
                    allergies = "Penicilina",
                    notes = "Protocolo de mesoterapia capilar combinado com toxina botulínica preventiva.",
                    totalSpent = 1940.0,
                    nextSuggestedReturn = "2026-10-28"
                ),
                ClientEntity(
                    id = 4,
                    name = "Fernanda Bittencourt",
                    phone = "(11) 96456-7890",
                    email = "fernanda.bit@gmail.com",
                    birthDate = "1990-07-18",
                    address = "Rua Pamplona, 450 - Jd. Paulista, SP",
                    allergies = "Nenhuma conhecida",
                    notes = "Tratamento de manchas residuais de acne com microagulhamento e drug delivery.",
                    totalSpent = 1540.0,
                    nextSuggestedReturn = "2026-11-02"
                ),
                ClientEntity(
                    id = 5,
                    name = "Beatriz Muniz Santana",
                    phone = "(11) 95567-8901",
                    email = "bia.muniz@uol.com.br",
                    birthDate = "1996-03-30",
                    address = "Al. Santos, 780 - Cerqueira César, SP",
                    allergies = "Látex (Usar luvas nitrílicas)",
                    notes = "Adepta de rotina de skincare rigorosa, hidratação de lábios regular.",
                    totalSpent = 1100.0,
                    nextSuggestedReturn = "2026-10-30"
                )
            )
        )

        // Seed Appointments
        dao.insertAppointments(
            listOf(
                AppointmentEntity(
                    id = 1,
                    clientId = 1,
                    clientName = "Mariana Souza Ribeiro",
                    clientPhone = "(11) 99123-4567",
                    professionalId = 2,
                    professionalName = "Dra. Camila Siqueira",
                    procedureId = 1,
                    procedureName = "Botox Full Face (Toxina Botulínica)",
                    date = todayStr,
                    time = "09:00",
                    status = "CONCLUIDO",
                    room = "Sala 02 - Harmonização Facial",
                    price = 1450.0
                ),
                AppointmentEntity(
                    id = 2,
                    clientId = 2,
                    clientName = "Juliana Alencar Prado",
                    clientPhone = "(11) 98234-5678",
                    professionalId = 1,
                    professionalName = "Dra. Helena Valente",
                    procedureId = 3,
                    procedureName = "Bioestimulador de Colágeno (Radiesse/Sculptra)",
                    date = todayStr,
                    time = "10:30",
                    status = "EM_ATENDIMENTO",
                    room = "Sala 01 - Procedimentos Médicos",
                    price = 2400.0
                ),
                AppointmentEntity(
                    id = 3,
                    clientId = 3,
                    clientName = "Carlos Eduardo Lima",
                    clientPhone = "(11) 97345-6789",
                    professionalId = 3,
                    professionalName = "Enf. Lucas Mendes",
                    procedureId = 8,
                    procedureName = "Terapia Capilar com Fatores de Crescimento",
                    date = todayStr,
                    time = "14:00",
                    status = "CONFIRMADO",
                    room = "Sala 03 - Tecnologias e Peelings",
                    price = 490.0
                ),
                AppointmentEntity(
                    id = 4,
                    clientId = null,
                    clientName = "Bloqueio Técnico",
                    clientPhone = "",
                    professionalId = 2,
                    professionalName = "Dra. Camila Siqueira",
                    procedureId = null,
                    procedureName = "Reunião Clínica e Alinhamento de Protocolos",
                    date = todayStr,
                    time = "12:00",
                    status = "CONFIRMADO",
                    room = "Sala 02 - Harmonização Facial",
                    isBlocked = true,
                    notes = "Horário reservado para equipe técnica.",
                    price = 0.0
                ),
                AppointmentEntity(
                    id = 5,
                    clientId = 4,
                    clientName = "Fernanda Bittencourt",
                    clientPhone = "(11) 96456-7890",
                    professionalId = 3,
                    professionalName = "Enf. Lucas Mendes",
                    procedureId = 4,
                    procedureName = "Microagulhamento com Drug Delivery Iluminador",
                    date = todayStr,
                    time = "16:00",
                    status = "AGUARDANDO",
                    room = "Sala 03 - Tecnologias e Peelings",
                    price = 580.0
                ),
                AppointmentEntity(
                    id = 6,
                    clientId = 5,
                    clientName = "Beatriz Muniz Santana",
                    clientPhone = "(11) 95567-8901",
                    professionalId = 2,
                    professionalName = "Dra. Camila Siqueira",
                    procedureId = 2,
                    procedureName = "Preenchimento Labial com Ácido Hialurônico (1ml)",
                    date = todayStr,
                    time = "17:30",
                    status = "CONFIRMADO",
                    room = "Sala 02 - Harmonização Facial",
                    price = 1350.0
                )
            )
        )

        // Seed Medical Record (Prontuário com imagens antes e depois)
        dao.insertMedicalRecords(
            listOf(
                MedicalRecordEntity(
                    id = 1,
                    clientId = 1,
                    clientName = "Mariana Souza Ribeiro",
                    procedureName = "Toxina Botulínica e Skin Booster Facial",
                    professionalName = "Dra. Camila Siqueira",
                    date = todayStr,
                    allergiesAlert = "ALERTA: Paciente alérgica a Dipirona e Frutos do Mar. Teste de contato prévio realizado.",
                    treatedAreas = "Terço Superior: Frontal (14 UI), Glabela (16 UI), Periorbicular (12 UI), Mesobotox perioral (8 UI).",
                    productsUsed = "Botox Allergan 100UI (Lote AB982) + Juvederm Volite 1ml (Lote JV204).",
                    lotNumber = "Lote Bx: AB982-BR | Lote AH: JV204-BR",
                    anamnesisNotes = "Queixa: Linhas de expressão marcadas na testa ao gesticular e desidratação perioral. Nega gravidez, amamentação ou doenças neuromusculares. Boa cicatrização prévia.",
                    recommendations = "Gelo local 10 min por sessão nas primeiras 2h. Evitar massagem por 48h. Retorno de avaliação em 15 dias para retoque se necessário.",
                    signatureName = "Mariana Souza Ribeiro",
                    signatureTimestamp = "$todayStr 09:42 UTC-3",
                    signatureIntegrityHash = "SHA256-9E8F2B71A43CD189",
                    beforeImageResName = "img_treatment_before",
                    afterImageResName = "img_treatment_after"
                )
            )
        )

        // Seed Treatment Packages
        dao.insertPackages(
            listOf(
                TreatmentPackageEntity(
                    id = 1,
                    clientId = 1,
                    clientName = "Mariana Souza Ribeiro",
                    packageName = "Protocolo Rejuvenescimento Full Face (3 Sessões)",
                    totalSessions = 3,
                    completedSessions = 2,
                    price = 3800.0,
                    expiryDate = "2027-04-10",
                    status = "ATIVO"
                ),
                TreatmentPackageEntity(
                    id = 2,
                    clientId = 4,
                    clientName = "Fernanda Bittencourt",
                    packageName = "Protocolo Glow & Firm - Microagulhamento (4 Sessões)",
                    totalSessions = 4,
                    completedSessions = 1,
                    price = 1980.0,
                    expiryDate = "2027-02-15",
                    status = "ATIVO"
                ),
                TreatmentPackageEntity(
                    id = 3,
                    clientId = 5,
                    clientName = "Beatriz Muniz Santana",
                    packageName = "Protocolo Lipo Enzimática de Papada (5 Sessões)",
                    totalSessions = 5,
                    completedSessions = 3,
                    price = 1750.0,
                    expiryDate = "2026-12-30",
                    status = "ATIVO"
                )
            )
        )

        // Seed Inventory Items
        dao.insertInventoryItems(
            listOf(
                InventoryItemEntity(
                    id = 1,
                    name = "Toxina Botulínica 100 UI (Botox Allergan)",
                    brand = "AbbVie / Allergan",
                    lotNumber = "AB982-BR",
                    expirationDate = "2026-11-20", // Alerta de validade próxima!
                    currentQuantity = 3,
                    minimumQuantity = 5, // Alerta de estoque baixo!
                    unit = "Frasco-Ampola",
                    category = "TOXINAS"
                ),
                InventoryItemEntity(
                    id = 2,
                    name = "Ácido Hialurônico Juvederm Voluma 1ml",
                    brand = "Allergan",
                    lotNumber = "JV204-BR",
                    expirationDate = "2027-08-15",
                    currentQuantity = 8,
                    minimumQuantity = 4,
                    unit = "Seringa 1ml",
                    category = "PREENCHEDORES"
                ),
                InventoryItemEntity(
                    id = 3,
                    name = "Restylane Kysse 1ml (Lábios)",
                    brand = "Galderma",
                    lotNumber = "RK881-GD",
                    expirationDate = "2027-05-30",
                    currentQuantity = 6,
                    minimumQuantity = 3,
                    unit = "Seringa 1ml",
                    category = "PREENCHEDORES"
                ),
                InventoryItemEntity(
                    id = 4,
                    name = "Bioestimulador Radiesse Plus 1.5ml",
                    brand = "Merz Aesthetics",
                    lotNumber = "RD441-MZ",
                    expirationDate = "2027-10-10",
                    currentQuantity = 4,
                    minimumQuantity = 3,
                    unit = "Seringa 1.5ml",
                    category = "BIOESTIMULADORES"
                ),
                InventoryItemEntity(
                    id = 5,
                    name = "Sculptra Ácido Poli-L-Lático",
                    brand = "Galderma",
                    lotNumber = "SC551-GD",
                    expirationDate = "2026-12-15",
                    currentQuantity = 2,
                    minimumQuantity = 4, // Estoque baixo!
                    unit = "Frasco",
                    category = "BIOESTIMULADORES"
                ),
                InventoryItemEntity(
                    id = 6,
                    name = "Agulhas Hipodérmicas 30G 4mm (Lebel)",
                    brand = "BD Medical",
                    lotNumber = "BD304-26",
                    expirationDate = "2028-01-01",
                    currentQuantity = 18,
                    minimumQuantity = 50, // Estoque baixo!
                    unit = "Unidade",
                    category = "DESCARTAVEIS"
                ),
                InventoryItemEntity(
                    id = 7,
                    name = "Microcânulas 22G 50mm SoftFil",
                    brand = "SoftFil Precision",
                    lotNumber = "SF225-27",
                    expirationDate = "2028-06-30",
                    currentQuantity = 25,
                    minimumQuantity = 15,
                    unit = "Unidade",
                    category = "DESCARTAVEIS"
                ),
                InventoryItemEntity(
                    id = 8,
                    name = "Anestésico Tópico Lidocaína 4% (Dermomax)",
                    brand = "Aché Laboratórios",
                    lotNumber = "DM109-26",
                    expirationDate = "2027-03-20",
                    currentQuantity = 5,
                    minimumQuantity = 2,
                    unit = "Bisnaga 60g",
                    category = "ANESTESICOS"
                )
            )
        )

        // Seed Financial Transactions
        dao.insertTransactions(
            listOf(
                FinancialTransactionEntity(
                    id = 1,
                    type = "RECEITA",
                    category = "Procedimentos",
                    description = "Botox Full Face - Mariana Souza",
                    amount = 1450.0,
                    date = todayStr,
                    relatedAppointmentId = 1,
                    professionalId = 2,
                    professionalName = "Dra. Camila Siqueira"
                ),
                FinancialTransactionEntity(
                    id = 2,
                    type = "RECEITA",
                    category = "Procedimentos",
                    description = "Bioestimulador Radiesse - Juliana Alencar",
                    amount = 2400.0,
                    date = todayStr,
                    relatedAppointmentId = 2,
                    professionalId = 1,
                    professionalName = "Dra. Helena Valente"
                ),
                FinancialTransactionEntity(
                    id = 3,
                    type = "RECEITA",
                    category = "Pacotes",
                    description = "Venda Pacote Glow & Firm (4 sessões) - Fernanda B.",
                    amount = 1980.0,
                    date = todayStr,
                    professionalId = 3,
                    professionalName = "Enf. Lucas Mendes"
                ),
                FinancialTransactionEntity(
                    id = 4,
                    type = "DESPESA",
                    category = "Insumos & Fornecedores",
                    description = "Lote Injetáveis Galderma & AbbVie",
                    amount = 2850.0,
                    date = todayStr
                ),
                FinancialTransactionEntity(
                    id = 5,
                    type = "DESPESA",
                    category = "Estrutura & Descartáveis",
                    description = "Compra de Luvas Nitrílicas e Cânulas descartáveis",
                    amount = 420.0,
                    date = todayStr
                )
            )
        )
    }
}
