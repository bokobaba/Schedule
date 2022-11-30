package com.love.schedule.di

import android.app.Application
import androidx.room.Room
import com.love.schedule.core.data.data_source.EmployeeDatabase
import com.love.schedule.feature_employees.data.repository.EmployeeRepository
import com.love.schedule.feature_employees.domain.repository.IEmployeeRepository
import com.love.schedule.feature_schedule.data.repository.ScheduleRepository
import com.love.schedule.feature_schedule.domain.repository.IScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun provideTestApi(): TestApi {
//        return Retrofit.Builder()
//            .baseUrl("https://test.com")
//            .build()
//            .create(TestApi::class.java)
//    }

    @Provides
    @Singleton
    fun provideEmployeeDatabase(app: Application): EmployeeDatabase {
        return Room.databaseBuilder(
            app,
            EmployeeDatabase::class.java,
            EmployeeDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideEmployeeRepository(db: EmployeeDatabase): IEmployeeRepository {
        return EmployeeRepository(db.employeeDao)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(db: EmployeeDatabase): IScheduleRepository {
        return ScheduleRepository(db.scheduleDao)
    }
}