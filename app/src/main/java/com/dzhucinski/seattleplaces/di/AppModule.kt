package com.dzhucinski.seattleplaces.di

import androidx.room.Room
import com.dzhucinski.seattleplaces.detail.DetailViewModel
import com.dzhucinski.seattleplaces.repository.PlacesRepository
import com.dzhucinski.seattleplaces.repository.PlacesRepositoryImpl
import com.dzhucinski.seattleplaces.network.FoursquareService
import com.dzhucinski.seattleplaces.repository.FavoritesRepository
import com.dzhucinski.seattleplaces.repository.FavoritesRepositoryImpl
import com.dzhucinski.seattleplaces.search.SearchViewModel
import com.dzhucinski.seattleplaces.storage.DB_NAME
import com.dzhucinski.seattleplaces.storage.PlacesDatabase
import com.dzhucinski.seattleplaces.util.ResourceProvider
import com.dzhucinski.seattleplaces.util.ResourceProviderImpl
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created by Denis Zhuchinski on 4/10/19.
 *
 * Koin module to create and provide dependencies in the App
 *
 */

val appModule = module {

    single<OkHttpClient> {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(30, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        builder.build()
    }

    single<FoursquareService> {
        Retrofit.Builder()
            .baseUrl(FoursquareService.API_CLIENT_URL)
            .client(get())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(FoursquareService::class.java)
    }

    single<Executor> { Executors.newSingleThreadExecutor() }

    single { Room.databaseBuilder(androidApplication(), PlacesDatabase::class.java, DB_NAME).build() }

    single<ResourceProvider> { ResourceProviderImpl(androidApplication()) }
}

val repositoryModule = module {

    single<PlacesRepository> { PlacesRepositoryImpl(get()) }

    single<FavoritesRepository> { FavoritesRepositoryImpl(get(), get()) }
}

val viewModelModule = module {
    viewModel { SearchViewModel(get(), get(), get()) }

    viewModel { (id: String) -> DetailViewModel(id, get(), get(), get()) }
}