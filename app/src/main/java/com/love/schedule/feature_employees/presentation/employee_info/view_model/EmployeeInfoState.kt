package com.love.schedule.feature_employees.presentation.employee_info.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.love.schedule.Data
import com.love.schedule.feature_employees.domain.model.Employee
import javax.inject.Inject

interface IEmployeeInfoState {
    val employeeName: MutableState<String>
    val employeeId: MutableState<String>

    val editEmployeeName: (name: String) -> Unit
    val editEmployeeId: (id: String) -> Unit
    var saveInfo: () -> Unit
}

class EmployeeInfoState @Inject constructor() : IEmployeeInfoState {
    private var _employeeName = mutableStateOf("")
    private var _employeeId = mutableStateOf<String>("")

    override val employeeName: MutableState<String>
        get() = _employeeName

    override val employeeId: MutableState<String>
        get() = _employeeId

        override val editEmployeeName = { name: String ->
        _employeeName.value = name
    }

    override val editEmployeeId = { id: String ->
        _employeeId.value = id
    }

    override lateinit var saveInfo: () -> Unit

    companion object {
        val previewState = object : IEmployeeInfoState {
            override val employeeName: MutableState<String>
                get() = mutableStateOf(Data.employees[0].name)
            override val employeeId: MutableState<String>
                get() = mutableStateOf(Data.employees[0].employeeId)
            override val editEmployeeName: (name: String) -> Unit = {}
            override val editEmployeeId: (id: String) -> Unit = {}
            override var saveInfo: () -> Unit = {}
        }
    }
}