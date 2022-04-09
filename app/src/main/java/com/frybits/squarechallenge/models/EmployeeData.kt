package com.frybits.squarechallenge.models

import com.frybits.squarechallenge.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Simple employee data model to mirror employee network response
 */
@Serializable
data class EmployeeDataList(val employees: List<EmployeeData>)

@Serializable
data class EmployeeData(
    @Serializable(with = UUIDSerializer::class) val uuid: UUID,
    val full_name: String,
    val phone_number: String = "",
    val email_address: String,
    val biography: String = "",
    val photo_url_small: String = "",
    val photo_url_large: String = "",
    val team: String,
    val employee_type: EmployeeType
)

enum class EmployeeType {
    FULL_TIME, PART_TIME, CONTRACTOR
}
