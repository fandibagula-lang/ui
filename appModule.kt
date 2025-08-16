package org.babetech.borastock.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sunildhiman90.kmauth.google.KMAuthGoogle
import com.sunildhiman90.kmauth.google.GoogleAuthManager
import org.babetech.borastock.data.models.datasource.datastore.provideDataStore
import org.babetech.borastock.data.models.datasource.repository.BoraStockRepository
import org.babetech.borastock.data.models.datasource.repository.BoraStockRepositoryImpl
import org.babetech.borastock.domain.usecase.GetCurrentUserUseCase
import org.babetech.borastock.domain.usecase.SetCurrentUserUseCase
import org.babetech.borastock.ui.screens.auth.viewmodel.LoginViewModel
import org.babetech.borastock.ui.screens.setup.viewmodel.CompanySetupViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    // DataStore multiplateforme
    single<DataStore<Preferences>> {
        provideDataStore()
    }

    // Repository
    single<BoraStockRepository> {
        BoraStockRepositoryImpl(datastore = get(), datastoreUser = get())
    }

    // UseCases
    single { GetCurrentUserUseCase(repository = get()) }
    single { SetCurrentUserUseCase(repository = get()) }

    factory { GetCurrentUserUseCase(get()) }

    // Auth Manager Google
    single<GoogleAuthManager> {
        KMAuthGoogle.googleAuthManager
    }

    // ViewModels
    viewModelOf(::LoginViewModel)
    viewModelOf(::CompanySetupViewModel)
}
