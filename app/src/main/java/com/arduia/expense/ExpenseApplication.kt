package com.arduia.expense

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import com.arduia.core.lang.updateResource
import com.arduia.expense.data.SettingRepositoryFactoryImpl
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.SettingsRepositoryImpl
import com.arduia.expense.data.local.PreferenceFlowStorageDaoImpl
import com.arduia.expense.data.local.PreferenceStorageDao
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.getDataOrError
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ExpenseApplication : Application(), androidx.work.Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun onCreate() {
        super.onCreate()
        setupLogging()
    }

    private fun setupLogging() {
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(Timber.DebugTree())
            false -> Unit
        }
    }

    override fun getWorkManagerConfiguration(): androidx.work.Configuration =
        androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun attachBaseContext(base: Context?) {
        val localedContext = getLocaleContext(base)
        super.attachBaseContext(localedContext)
    }

    private fun getLocaleContext(base: Context?): Context? =
        runBlocking {
            if (base == null) return@runBlocking base
            val selectedLanguage =
                SettingRepositoryFactoryImpl.create(base).getSelectedLanguageSync().getDataOrError()
            return@runBlocking base.updateResource(selectedLanguage)
        }
}
