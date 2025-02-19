package com.example.fordtask2.di

import android.content.Context
import androidx.room.Room
import com.example.fordtask2.BuildConfig
import com.example.fordtask2.data.local.dao.AppDatabase
import com.example.fordtask2.data.local.dao.RepoDao
import com.example.fordtask2.data.local.datastore.PreferencesDataStore
import com.example.fordtask2.data.remote.api.GithubApiService
import com.example.fordtask2.domain.repository.GithubRepository
import com.example.fordtask2.domain.repository.GithubRepositoryImpl
import com.example.fordtask2.util.mapper.NetworkHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGithubApi(okHttpClient: OkHttpClient): GithubApiService {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())  // Kotlin data class'ları için adaptör ekliyoruz
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(GithubApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer ${BuildConfig.GITHUB_API_TOKEN}")
                    .header("Accept", "application/vnd.github.v3+json")
                    .method(original.method, original.body)

                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkHelper(@ApplicationContext context: Context): NetworkHelper {
        return NetworkHelper(context)
    }

    @Provides
    fun provideRepository(
        api: GithubApiService,
        repoDao: RepoDao,
        networkHelper: NetworkHelper
    ): GithubRepository {
        return GithubRepositoryImpl(api, repoDao, networkHelper)
    }

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideRepoDao(database: AppDatabase): RepoDao {
        return database.repoDao()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object DataStoreModule {
        @Provides
        @Singleton
        fun providePreferencesDataStore(@ApplicationContext context: Context): PreferencesDataStore {
            return PreferencesDataStore(context)
        }
    }
}