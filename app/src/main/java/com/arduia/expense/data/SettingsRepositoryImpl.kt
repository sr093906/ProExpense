package com.arduia.expense.data

import android.content.Context
import com.arduia.expense.data.exception.RepositoryException
import com.arduia.expense.data.ext.getResultSuccessOrError
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.data.local.PreferenceFlowStorageDaoImpl
import com.arduia.expense.data.local.PreferenceStorageDao
import com.arduia.expense.model.ErrorResult
import com.arduia.expense.model.FlowResult
import com.arduia.expense.model.Result
import com.arduia.expense.model.SuccessResult
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(private val dao: PreferenceStorageDao) :
    SettingsRepository {

    override fun getSelectedLanguage(): FlowResult<String> =
        dao.getSelectedLanguage()
            .map { SuccessResult(it) }
            .catch { e -> ErrorResult(RepositoryException(e)) }

    override suspend fun setSelectedLanguage(id: String) {
        dao.setSelectedLanguage(id)
    }

    override fun getFirstUser(): FlowResult<Boolean> =
        dao.getFirstUser()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }


    override suspend fun setFirstUser(isFirstUser: Boolean) {
        dao.setFirstUser(isFirstUser)
    }

    override fun getSelectedCurrencyNumber(): FlowResult<String> =
        dao.getSelectedCurrencyNumber()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }


    override suspend fun setSelectedCurrencyNumber(num: String) {
        dao.setSelectedCurrencyNumber(num)
    }

    override suspend fun getSelectedLanguageSync(): Result<String> {
        return getResultSuccessOrError { dao.getSelectedLanguageSync() }
    }

    override suspend fun getFirstUserSync(): Result<Boolean> {
        return getResultSuccessOrError { dao.getFirstUserSync() }
    }

    override suspend fun getSelectedCurrencyNumberSync(): Result<String> {
        return getResultSuccessOrError { dao.getSelectedCurrencyNumberSync() }
    }

    override suspend fun setSelectedThemeMode(mode: Int) {
        dao.setSelectedThemeMode(mode)
    }

    override suspend fun getSelectedThemeModeSync(): Result<Int> {
        return getResultSuccessOrError { dao.getSelectedThemeModeSync() }
    }

    override fun getUpdateStatus(): FlowResult<Int> {
        return dao.getUpdateStatus()
            .map { SuccessResult(it) }
            .catch { ErrorResult(RepositoryException(it)) }
    }

    override suspend fun setUpdateStatus(status: Int) {
        dao.setUpdateStatus(status)
    }

    override suspend fun getAboutUpdateSync(): Result<AboutUpdateDataModel> {
        return getResultSuccessOrError { dao.getAboutUpdateSync() }
    }

    override suspend fun setAboutUpdate(info: AboutUpdateDataModel) {
        dao.setAboutUpdate(info)
    }
}

object SettingRepositoryFactoryImpl  : SettingsRepository.Factory {
    override fun create(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(PreferenceFlowStorageDaoImpl(context))
    }
}