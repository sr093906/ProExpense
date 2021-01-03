package com.arduia.expense.ui.about

import com.arduia.core.arch.Mapper
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.ui.about.AboutUpdateUiModel
import javax.inject.Inject

class AboutUpdateUiModelMapper @Inject constructor() :
    Mapper<AboutUpdateDataModel, AboutUpdateUiModel> {
    override fun map(input: AboutUpdateDataModel): AboutUpdateUiModel {
        return AboutUpdateUiModel(
            versionName = input.name,
            versionCode = "(${input.code})",
            changeLogs = input.changeLogs
        )
    }
}