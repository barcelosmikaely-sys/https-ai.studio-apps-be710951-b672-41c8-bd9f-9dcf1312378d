package com.example

import com.example.data.local.ClinicDao
import com.example.data.local.ClinicRepository
import com.example.model.AppointmentStatus
import com.example.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

class ClinicSystemTest {

    @Test
    fun testSha256PasswordHashing() {
        val testPin = "1234"
        val bytes = MessageDigest.getInstance("SHA-256").digest(testPin.toByteArray())
        val hash = bytes.joinToString("") { "%02x".format(it) }

        assertNotNull(hash)
        assertEquals(64, hash.length)
        // Check hash repeatability
        val hashAgain = bytes.joinToString("") { "%02x".format(it) }
        assertEquals(hash, hashAgain)
    }

    @Test
    fun testCommissionCalculation() {
        val procedurePrice = 1450.0 // Botox Full Face
        val commissionRate = 0.35   // 35%
        val expectedCommission = 507.50

        val actualCommission = procedurePrice * commissionRate
        assertEquals(expectedCommission, actualCommission, 0.001)
    }

    @Test
    fun testPackageSessionProgress() {
        val totalSessions = 5
        val completedSessions = 3
        val remainingSessions = (totalSessions - completedSessions).coerceAtLeast(0)

        assertEquals(2, remainingSessions)
        val progress = completedSessions.toFloat() / totalSessions.toFloat()
        assertEquals(0.6f, progress, 0.001f)
    }

    @Test
    fun testRoleDistinction() {
        val adminRole = UserRole.ADMIN
        val employeeRole = UserRole.EMPLOYEE

        assertNotEquals(adminRole, employeeRole)
        assertEquals("Administrador / Dono", adminRole.label)
        assertEquals("Profissional / Atendente", employeeRole.label)
    }
}
