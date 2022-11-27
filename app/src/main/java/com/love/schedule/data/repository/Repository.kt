package com.love.schedule.data.repository

import android.app.Application
import com.love.schedule.R
import com.love.schedule.data.domain.repository.IRepository
import com.love.schedule.data.remote.TestApi
import javax.inject.Inject

class Repository @Inject constructor(
    private val api: TestApi,
    private val appContext: Application
) : IRepository {

    init {
        val appName = appContext.getString(R.string.app_name)
        println("hello from the repository: the app name is $appName")
    }

    override suspend fun doNetworkCall() {

    }

}