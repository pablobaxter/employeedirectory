package com.frybits.squarechallenge.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class EmployeeEntity(
    @PrimaryKey val uuid: UUID,
    val full_name: String,
    val phone_number: String = "",
    val email_address: String,
    val biography: String = "",
    val photo_url_small: String = "",
    val photo_url_large: String = "",
    val team: String,
    val employee_type: String,
    val fetch_time: Long
)

fun EmployeeData.toEmployeeEntity(): EmployeeEntity {
    return EmployeeEntity(
        uuid = uuid,
        full_name = full_name,
        phone_number = phone_number,
        email_address = email_address,
        biography = biography,
        photo_url_small = photo_url_small,
        photo_url_large = photo_url_large,
        team = team,
        employee_type = employee_type.name,
        fetch_time = System.currentTimeMillis()
    )
}

fun List<EmployeeData>.toEmployeeEntities(): List<EmployeeEntity> {
    return map { it.toEmployeeEntity() }
}