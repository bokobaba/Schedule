package com.love.schedule.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.love.schedule.BuildConfig
import com.love.schedule.core.auth.Auth
import com.love.schedule.core.auth.IAuth
import com.love.schedule.core.data.data_source.EmployeeDatabase
import com.love.schedule.core.data.data_source.ScheduleApiDao
import com.love.schedule.core.data.remote.Auth0Test
import com.love.schedule.core.data.remote.AuthInterceptor
import com.love.schedule.core.data.remote.ScheduleApi
import com.love.schedule.core.data.repository.Auth0TestRepository
import com.love.schedule.core.data.repository.DataStoreRepository
import com.love.schedule.core.data.repository.IDataStoreRepository
import com.love.schedule.core.data.repository.ScheduleApiRepository
import com.love.schedule.core.util.Constants
import com.love.schedule.feature_employees.data.repository.EmployeeRepository
import com.love.schedule.feature_employees.domain.repository.IEmployeeRepository
import com.love.schedule.feature_rules.data.repository.RulesRepository
import com.love.schedule.feature_rules.domain.repository.IRulesRepository
import com.love.schedule.feature_schedule.data.repository.ScheduleRepository
import com.love.schedule.feature_schedule.domain.repository.IScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideScheduleApi(client: OkHttpClient): ScheduleApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ScheduleApi::class.java)
    }

    @Provides
    @Singleton
    internal fun provideClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val client: OkHttpClient.Builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            //set self sign certificate
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            client
                .sslSocketFactory(
                    sslSocketFactory,
                    trustAllCerts[0] as X509TrustManager
                )
                .hostnameVerifier { _, _ -> true }
        }

        return client.addInterceptor(authInterceptor).build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(auth: IAuth): AuthInterceptor {
        return AuthInterceptor(auth)
    }

    @Provides
    @Singleton
    fun provideAuth0Test(): Auth0Test {
        return Retrofit.Builder()
            .baseUrl("https://${BuildConfig.AUTH0_DOMAIN}/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Auth0Test::class.java)
    }

    @Provides
    @Singleton
    fun provideScheduleApiRepository(api: ScheduleApi, db: EmployeeDatabase): ScheduleApiRepository {
        return ScheduleApiRepository(api, db.apiDao)
    }

    @Provides
    @Singleton
    fun provideAuth0TestRepository(api: Auth0Test) = Auth0TestRepository(api)

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

    @Provides
    @Singleton
    fun provideRulesRepository(db: EmployeeDatabase): IRulesRepository {
        return RulesRepository(db.rulesDao)
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(@ApplicationContext context: Context): IDataStoreRepository {
        return DataStoreRepository(context)
    }

    @Provides
    @Singleton
    fun provideAuth(
        dataStoreRepository: IDataStoreRepository,
        testToken: Auth0TestRepository
    ): IAuth {
        return Auth(dataStoreRepository, testToken)
    }
}