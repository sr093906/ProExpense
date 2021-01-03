package com.arduia.expense.di

import android.content.Context
import android.view.LayoutInflater
import com.arduia.expense.ui.backup.BackupListAdapter
import com.arduia.expense.ui.expenselogs.ExpenseLogAdapter
import com.arduia.expense.ui.home.RecentListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(FragmentComponent::class)
object AdapterModule{

    @Provides
    fun provideLayoutInflater(@ActivityContext context: Context)
        : LayoutInflater = LayoutInflater.from(context)

}
