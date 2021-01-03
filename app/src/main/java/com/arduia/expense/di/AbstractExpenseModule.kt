package com.arduia.expense.di

import android.content.Context
import com.arduia.expense.ui.common.category.ExpenseCategoryProvider
import com.arduia.expense.ui.common.category.ExpenseCategoryProviderImpl
import com.arduia.expense.ui.common.language.LanguageProvider
import com.arduia.expense.ui.common.language.LanguageProviderImpl
import com.arduia.expense.ui.home.ExpenseDayNameProvider
import com.arduia.expense.ui.home.ExpenseRateCalculator
import com.arduia.expense.ui.home.ExpenseRateCalculatorFactory
import com.arduia.graph.DayNameProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class AbstractExpenseModule {

    @Binds
    @ActivityScoped
    abstract fun provideExpenseCategory(impl: ExpenseCategoryProviderImpl): ExpenseCategoryProvider

    @Binds
    @ActivityScoped
    abstract fun provideLanguage(impl: LanguageProviderImpl): LanguageProvider

    @Binds
    @ActivityScoped
    abstract fun provideExpenseCalculator(impl: ExpenseRateCalculatorFactory): ExpenseRateCalculator.Factory

    @Binds
    @ActivityScoped
    abstract fun provideDataNames(impl: ExpenseDayNameProvider): DayNameProvider

}
