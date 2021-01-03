package com.arduia.expense.data.local

import androidx.paging.DataSource
import androidx.room.*
import com.arduia.expense.model.Result
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao{

    @Insert( onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertExpense(expenseEnt: ExpenseEnt)

    @Insert
    suspend fun insertExpenseAll(expenses: List<ExpenseEnt>)

    @Query ( "SELECT * FROM `expense` ORDER BY modified_date DESC" )
    fun getExpenseSourceAll(): DataSource.Factory<Int, ExpenseEnt>

    @Query ( "SELECT * FROM `expense` ORDER BY modified_date DESC" )
    fun getExpenseAll(): Flow<List<ExpenseEnt>>

    @Query( "SELECT * FROM `expense` ORDER BY modified_date DESC")
    suspend fun getExpenseAllSync(): List<ExpenseEnt>

    @Query("SELECT * FROM `expense` WHERE modified_date >= :startTime AND modified_date <= :endTime ORDER BY modified_date ASC LIMIT :limit OFFSET :offset")
    fun getExpenseRangeAsc(startTime: Long, endTime: Long, offset: Int, limit: Int): Flow<List<ExpenseEnt>>

    @Query("SELECT * FROM `expense` WHERE modified_date >= :startTime AND modified_date <= :endTime ORDER BY modified_date DESC LIMIT :limit OFFSET :offset")
    fun getExpenseRangeDesc(startTime: Long, endTime: Long, offset: Int, limit: Int): Flow<List<ExpenseEnt>>

    @Query("SELECT * FROM `expense` WHERE modified_date >= :startTime AND modified_date <= :endTime ORDER BY modified_date ASC LIMIT :limit OFFSET :offset")
    fun getExpenseRangeAscSource(startTime: Long, endTime: Long, offset: Int, limit: Int): DataSource.Factory<Int, ExpenseEnt>

    @Query("SELECT * FROM `expense` WHERE modified_date >= :startTime AND modified_date <= :endTime ORDER BY modified_date DESC LIMIT :limit OFFSET :offset")
    fun getExpenseRangeDescSource(startTime: Long, endTime: Long, offset: Int, limit: Int): DataSource.Factory<Int, ExpenseEnt>

    @Query("SELECT * FROM `expense` WHERE expense_id =:id")
    fun getItemExpense(id: Int): Flow<ExpenseEnt>

    @Query("SELECT `modified_date` FROM `expense` ORDER BY `modified_date` ASC LIMIT 1")
    suspend fun getMostRecentDateSync(): Long

    @Query("SELECT `modified_date` FROM `expense` ORDER BY `modified_date` DESC LIMIT 1")
    suspend fun getMostLatestDateSync(): Long

    @Query("SELECT `modified_date`, MIN(`modified_date`) AS `minDate`, MAX(`modified_date`) AS `maxDate` FROM `expense` LIMIT 1")
    fun getMaxAndMiniDateRange(): Flow<DateRangeDataModel>

    @Query( "SELECT * FROM `expense` ORDER BY modified_date DESC LIMIT 4")
    fun getRecentExpense(): Flow<List<ExpenseEnt>>

    @Query("SELECT COUNT(*) FROM expense")
    fun getExpenseTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM expense")
    suspend fun getExpenseTotalCountSync(): Int

    @Query("SELECT * FROM 'expense' ORDER BY modified_date DESC LIMIT :limit OFFSET :offset")
    fun getExpenseRange(limit: Int, offset: Int): Flow<List<ExpenseEnt>>

    @Update
    suspend fun updateExpense(expenseEnt: ExpenseEnt)

    @Delete
    suspend fun deleteExpense(expenseEnt: ExpenseEnt)

    @Query("DELETE FROM `expense` WHERE expense_id =:id" )
    suspend fun deleteExpenseRowById(id:Int)

    @Query( "DELETE FROM `expense` WHERE  expense_id in (:idLists)")
    suspend fun deleteExpenseByIDs(idLists: List<Int>)

    @Query("SELECT * FROM 'expense' WHERE modified_date > :startTime ORDER BY modified_date DESC")
    fun getWeekExpense(startTime: Long): Flow<List<ExpenseEnt>>

}
