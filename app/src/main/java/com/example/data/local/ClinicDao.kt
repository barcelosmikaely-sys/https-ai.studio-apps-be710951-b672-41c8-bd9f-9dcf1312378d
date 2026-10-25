package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicDao {
    // USERS
    @Query("SELECT * FROM users WHERE active = 1")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    // CLIENTS
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    suspend fun getClientById(id: Int): ClientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>)

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Delete
    suspend fun deleteClient(client: ClientEntity)

    // PROFESSIONALS
    @Query("SELECT * FROM professionals ORDER BY name ASC")
    fun getAllProfessionals(): Flow<List<ProfessionalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfessional(prof: ProfessionalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfessionals(professionals: List<ProfessionalEntity>)

    @Update
    suspend fun updateProfessional(prof: ProfessionalEntity)

    // PROCEDURES
    @Query("SELECT * FROM procedures ORDER BY category ASC, name ASC")
    fun getAllProcedures(): Flow<List<ProcedureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcedure(procedure: ProcedureEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcedures(procedures: List<ProcedureEntity>)

    @Update
    suspend fun updateProcedure(procedure: ProcedureEntity)

    @Delete
    suspend fun deleteProcedure(procedure: ProcedureEntity)

    // APPOINTMENTS
    @Query("SELECT * FROM appointments ORDER BY date ASC, time ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE date = :date ORDER BY time ASC")
    fun getAppointmentsByDate(date: String): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)

    // MEDICAL RECORDS
    @Query("SELECT * FROM medical_records ORDER BY id DESC")
    fun getAllMedicalRecords(): Flow<List<MedicalRecordEntity>>

    @Query("SELECT * FROM medical_records WHERE clientId = :clientId ORDER BY id DESC")
    fun getRecordsByClient(clientId: Int): Flow<List<MedicalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalRecord(record: MedicalRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalRecords(records: List<MedicalRecordEntity>)

    // TREATMENT PACKAGES
    @Query("SELECT * FROM treatment_packages ORDER BY id DESC")
    fun getAllPackages(): Flow<List<TreatmentPackageEntity>>

    @Query("SELECT * FROM treatment_packages WHERE clientId = :clientId")
    fun getPackagesByClient(clientId: Int): Flow<List<TreatmentPackageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(pkg: TreatmentPackageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackages(packages: List<TreatmentPackageEntity>)

    @Update
    suspend fun updatePackage(pkg: TreatmentPackageEntity)

    // INVENTORY
    @Query("SELECT * FROM inventory_items ORDER BY expirationDate ASC")
    fun getAllInventory(): Flow<List<InventoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItems(items: List<InventoryItemEntity>)

    @Update
    suspend fun updateInventoryItem(item: InventoryItemEntity)

    @Delete
    suspend fun deleteInventoryItem(item: InventoryItemEntity)

    // FINANCIAL TRANSACTIONS
    @Query("SELECT * FROM financial_transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<FinancialTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: FinancialTransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(txs: List<FinancialTransactionEntity>)

    @Delete
    suspend fun deleteTransaction(tx: FinancialTransactionEntity)

    // CLINIC CONFIG
    @Query("SELECT * FROM clinic_config WHERE id = 1 LIMIT 1")
    fun getClinicConfig(): Flow<ClinicConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClinicConfig(config: ClinicConfigEntity)
}
