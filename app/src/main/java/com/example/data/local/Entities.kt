package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: String, // ADMIN or EMPLOYEE
    val phone: String,
    val professionalId: Int? = null,
    val active: Boolean = true
)

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String,
    val birthDate: String,
    val address: String,
    val allergies: String, // ex: "Dipirona, Látex" ou "Nenhuma"
    val notes: String,
    val totalSpent: Double = 0.0,
    val nextSuggestedReturn: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "professionals")
data class ProfessionalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val specialty: String,
    val phone: String,
    val commissionRate: Double = 0.30, // ex: 30%
    val availableHours: String = "08:00 - 19:00",
    val activeRooms: String = "Sala 01, Sala 02",
    val colorHex: String = "#2A363B"
)

@Entity(tableName = "procedures")
data class ProcedureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // INJETAVEIS, FACIAL, CORPORAL, CAPILAR, LASER_TECNOLOGIA
    val price: Double,
    val durationMinutes: Int,
    val returnIntervalDays: Int, // ex: 120 para Botox, 30 para Enzimas
    val description: String,
    val postCare: String
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientId: Int?,
    val clientName: String,
    val clientPhone: String,
    val professionalId: Int,
    val professionalName: String,
    val procedureId: Int?,
    val procedureName: String,
    val date: String, // YYYY-MM-DD
    val time: String, // HH:mm
    val status: String, // CONFIRMADO, AGUARDANDO, EM_ATENDIMENTO, CONCLUIDO, CANCELADO
    val room: String,
    val isBlocked: Boolean = false,
    val notes: String = "",
    val price: Double = 0.0
)

@Entity(tableName = "medical_records")
data class MedicalRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientId: Int,
    val clientName: String,
    val procedureName: String,
    val professionalName: String,
    val date: String,
    val allergiesAlert: String,
    val treatedAreas: String,
    val productsUsed: String,
    val lotNumber: String,
    val anamnesisNotes: String,
    val recommendations: String,
    val signatureName: String = "",
    val signatureTimestamp: String = "",
    val signatureIntegrityHash: String = "",
    val beforeImageResName: String = "img_treatment_before",
    val afterImageResName: String = "img_treatment_after"
)

@Entity(tableName = "treatment_packages")
data class TreatmentPackageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientId: Int,
    val clientName: String,
    val packageName: String,
    val totalSessions: Int,
    val completedSessions: Int,
    val price: Double,
    val expiryDate: String,
    val status: String = "ATIVO" // ATIVO, CONCLUIDO, EXPIRADO
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val brand: String,
    val lotNumber: String,
    val expirationDate: String,
    val currentQuantity: Int,
    val minimumQuantity: Int,
    val unit: String, // Frasco, Ampola, Caixa, Unidade
    val category: String
)

@Entity(tableName = "financial_transactions")
data class FinancialTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // RECEITA ou DESPESA
    val category: String,
    val description: String,
    val amount: Double,
    val date: String,
    val relatedAppointmentId: Int? = null,
    val professionalId: Int? = null,
    val professionalName: String? = null,
    val isPaid: Boolean = true
)

@Entity(tableName = "clinic_config")
data class ClinicConfigEntity(
    @PrimaryKey val id: Int = 1,
    val clinicName: String = "Aura Estética Avançada",
    val cnpj: String = "45.892.103/0001-92",
    val phone: String = "(11) 98765-4321",
    val address: String = "Av. Higienópolis, 1200 - Conj. 82 - São Paulo, SP",
    val openingHours: String = "Segunda a Sábado, das 08h às 20h",
    val autoLockTimeoutMinutes: Int = 15,
    val quickPin: String = "1234"
)
