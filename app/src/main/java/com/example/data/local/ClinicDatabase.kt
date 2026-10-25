package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        ClientEntity::class,
        ProfessionalEntity::class,
        ProcedureEntity::class,
        AppointmentEntity::class,
        MedicalRecordEntity::class,
        TreatmentPackageEntity::class,
        InventoryItemEntity::class,
        FinancialTransactionEntity::class,
        ClinicConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ClinicDatabase : RoomDatabase() {
    abstract fun clinicDao(): ClinicDao

    companion object {
        @Volatile
        private var INSTANCE: ClinicDatabase? = null

        fun getInstance(context: Context): ClinicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClinicDatabase::class.java,
                    "aura_estetica_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
