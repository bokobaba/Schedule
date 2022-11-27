package com.love.schedule.feature_employees.presentation.employee_info.view_model

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.love.schedule.Data
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import javax.inject.Inject

interface IRequestsState {
    val requests: SnapshotStateList<EmployeeRequest>

    fun editDescription(id: Int, value: String)
    fun editStart(id: Int, value: String)
    fun editEnd(id: Int, value: String)
    fun addRequest()
    fun setRequests(list: List<EmployeeRequest>)
}

class RequestsState @Inject constructor() : IRequestsState {
    //    private val _requests = mutableStateMapOf<Int, EmployeeRequest>()
    private val _requests = mutableStateListOf<EmployeeRequest>()

    override val requests: SnapshotStateList<EmployeeRequest>
        get() = _requests

    override fun editDescription(id: Int, value: String) {
        if (id < _requests.size)
            _requests[id] = _requests[id].copy(description = value)
    }

    override fun editStart(id: Int, value: String) {
        if (id < _requests.size)
            _requests[id] = _requests[id].copy(start = value)
    }

    override fun editEnd(id: Int, value: String) {
        if (id < _requests.size)
            _requests[id] = _requests[id].copy(end = value)
    }

    override fun addRequest() {
        _requests.add(
            EmployeeRequest(
                employeeId = "",
                description = "",
                start = "",
                end = "",
            )
        )
    }

    override fun setRequests(list: List<EmployeeRequest>) {
        _requests.clear()
        _requests.addAll(list)
    }

    companion object {
        val previewRequestsState = object : IRequestsState {
            override val requests: SnapshotStateList<EmployeeRequest>
                get() = Data.requests.toMutableStateList()

            override fun editDescription(id: Int, value: String) {}
            override fun editStart(id: Int, value: String) {}
            override fun editEnd(id: Int, value: String) {}
            override fun addRequest() {}
            override fun setRequests(list: List<EmployeeRequest>) {}
        }
    }
}