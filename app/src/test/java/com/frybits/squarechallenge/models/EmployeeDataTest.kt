package com.frybits.squarechallenge.models

import com.frybits.squarechallenge.repo.networking.DEFAULT_JSON_FORMAT
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class EmployeeDataTest {

    @Test
    fun serializeEmployeeDataTest() {
        val uuid = UUID.randomUUID()
        val employeeData = EmployeeData(
            uuid = uuid,
            full_name = "testName",
            phone_number = "",
            email_address = "test@test.com",
            biography = "",
            photo_url_small = "",
            photo_url_large = "",
            team = "test",
            employee_type = EmployeeType.FULL_TIME
        )

        val expectedJson = """
            {
                "uuid":"$uuid",
                "full_name":"testName",
                "email_address":"test@test.com",
                "team":"test",
                "employee_type":"FULL_TIME"
            }
        """.trimIndent().replace("\n", "").replace(" ", "")

        val jsonString = DEFAULT_JSON_FORMAT.encodeToString(employeeData)

        assertEquals(expectedJson, jsonString)
    }

    @Test
    fun deserializeEmployeeDataTest() {
        val uuid = UUID.randomUUID()
        val actualEmployeeData = EmployeeData(
            uuid = uuid,
            full_name = "testName",
            phone_number = "",
            email_address = "test@test.com",
            biography = "",
            photo_url_small = "",
            photo_url_large = "",
            team = "test",
            employee_type = EmployeeType.FULL_TIME
        )

        val testJson = """
            {
                "uuid":"$uuid",
                "full_name":"testName",
                "phone_number": "",
                "email_address":"test@test.com",
                "biography": "",
                "photo_url_small": "",
                "photo_url_large": "",
                "team":"test",
                "employee_type":"FULL_TIME"
            }
        """.trimIndent()

        val testData = DEFAULT_JSON_FORMAT.decodeFromString<EmployeeData>(testJson)

        assertEquals(actualEmployeeData, testData)
    }

    @Test
    fun deserializeWithMinimalDataTest() {
        val uuid = UUID.randomUUID()
        val testJson = """
            {
                "uuid":"$uuid",
                "full_name":"testName",
                "email_address":"test@test.com",
                "team":"test",
                "email_address": null
                "employee_type":"FULL_TIME"
            }
        """.trimIndent()

        val testData = DEFAULT_JSON_FORMAT.decodeFromString<EmployeeData>(testJson)

        println(testData)
    }
}
