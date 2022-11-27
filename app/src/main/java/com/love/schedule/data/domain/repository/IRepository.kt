package com.love.schedule.data.domain.repository

interface IRepository {
    suspend fun doNetworkCall()
}