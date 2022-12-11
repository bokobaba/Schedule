package com.love.schedule.core.data.remote.responses

data class GetEmployeeInfoDto(
    val availability: List<GetAvailabilityDto>,
    val employeeId: Int,
    val name: String,
    val requests: List<GetRequestDto>
)