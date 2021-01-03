package com.arduia.expense.ui.expenselogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.ItemTouchHelper
import com.arduia.core.extension.px
import com.arduia.core.view.asInvisible
import com.arduia.core.view.asVisible
import com.arduia.expense.R
import com.arduia.expense.databinding.FragExpenseLogsBinding
import com.arduia.expense.di.TopDropNavOption
import com.arduia.expense.domain.filter.ExpenseLogFilterInfo
import com.arduia.expense.ui.NavBaseFragment
import com.arduia.expense.ui.common.delete.DeleteConfirmFragment
import com.arduia.expense.ui.common.uimodel.DeleteInfoUiModel
import com.arduia.expense.ui.common.expense.ExpenseDetailDialog
import com.arduia.expense.ui.common.expense.ExpenseDetailUiModel
import com.arduia.expense.ui.common.helper.MarginItemDecoration
import com.arduia.expense.ui.common.filter.ExpenseFilterDialogFragment
import com.arduia.expense.ui.expenselogs.swipe.SwipeItemCallback
import com.arduia.mvvm.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseFragment : NavBaseFragment() {

    private var _binding: FragExpenseLogsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ExpenseViewModel>()

    private var filterDialog: ExpenseFilterDialogFragment? = null

    private var adapter: ExpenseLogAdapter? = null

    private val itemNumberFormat = DecimalFormat()

    @Inject
    @TopDropNavOption
    lateinit var entryNavOption: NavOptions

    private var deleteConfirmDialog: DeleteConfirmFragment? = null
    private var detailDialog: ExpenseDetailDialog? = null

    private val filterInfoObserver: Observer<String> = Observer {
        binding.tbExpense.subtitle = it
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragExpenseLogsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        clean()
        super.onDestroyView()
    }

    private fun clean() {
        deleteConfirmDialog?.setOnConfirmListener(null)
        deleteConfirmDialog = null
        filterDialog?.setOnFilterApplyListener(null)
        filterDialog = null
//        lifecycle.removeObserver(viewModel)
        binding.tbExpense.setOnMenuItemClickListener(null)
        binding.rvExpense.adapter = null
        adapter = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onRestoreState()
        setupToolbar()
        setupExpenseLogRecyclerview()
        setupViewModel()
        PageKeyedDataSource.LoadInitialParams<Int>(1, false)
    }

    private fun setupToolbar() {
        binding.tbExpense.setOnMenuItemClickListener listener@{
            when (it.itemId) {
                R.id.filter -> viewModel.onFilterPrepare()
                R.id.delete -> viewModel.onDeletePrepared()
            }
            return@listener true
        }
    }

    private fun showDeleteConfirmDialog(countTotal: Int) {
        deleteConfirmDialog?.dismiss()
        deleteConfirmDialog = DeleteConfirmFragment()
        deleteConfirmDialog?.setOnConfirmListener {
            viewModel.onMultiDeleteConfirmed()
        }
        deleteConfirmDialog?.show(childFragmentManager, DeleteInfoUiModel(countTotal, null))
    }

    private fun showSingleDeleteConfirmDialog() {
        deleteConfirmDialog?.dismiss()
        deleteConfirmDialog = DeleteConfirmFragment()
        deleteConfirmDialog?.setOnConfirmListener {
            viewModel.onSingleItemDeleteConfirmed()
        }
        deleteConfirmDialog?.show(childFragmentManager, DeleteInfoUiModel(1, null))
    }

    private fun showFilterDialog(filterEnt: ExpenseLogFilterInfo) {
        //Remove Old Dialog if exit
        with(filterDialog) {
            this?.setOnFilterApplyListener(null)
            this?.dismiss()
        }

        //Create New Dialog
        filterDialog = ExpenseFilterDialogFragment().apply {
            setOnFilterApplyListener(viewModel::setFilter)
        }

        filterDialog?.show(
            childFragmentManager,
            filterEnt
        )
    }

    private fun setupExpenseLogRecyclerview() {
        adapter = ExpenseLogAdapter(layoutInflater).apply {
            setOnStateChangeListener { holder, _ ->
                viewModel.storeState(holder)
            }
            setOnDeleteListener {
                viewModel.onSingleDeletePrepared(it.expenseLog.id)
            }
            setOnClickListener(viewModel::onShowItemDetail)
        }
        val rvTouchHelper = ItemTouchHelper(SwipeItemCallback())
        rvTouchHelper.attachToRecyclerView(binding.rvExpense)
        binding.rvExpense.addItemDecoration(
            MarginItemDecoration(
                spaceSide = 0,
                spaceHeight = requireContext().px(0.5f).toInt()
            )
        )
        binding.rvExpense.adapter = adapter
    }

    private fun setupViewModel() {

        viewModel.expenseList.observe(viewLifecycleOwner) {
            adapter?.submitList(it)

        }

        viewModel.onRestoreSwipeState.observe(viewLifecycleOwner, EventObserver {
            adapter?.restoreState(it)
            adapter?.notifyDataSetChanged()
        })

        viewModel.expenseLogMode.observe(viewLifecycleOwner) {
            when (it) {
                ExpenseMode.NORMAL -> changeUiDefault()
                ExpenseMode.SELECTION -> changeUiSelection()
                else -> Unit
            }
        }

        viewModel.onMultiDeleteConfirm.observe(viewLifecycleOwner, EventObserver {
            showDeleteConfirmDialog(it)
        })

        viewModel.onSingleDeleteConfirm.observe(viewLifecycleOwner, EventObserver {
            showSingleDeleteConfirmDialog()
        })

        viewModel.onFilterShow.observe(viewLifecycleOwner, EventObserver {
            showFilterDialog(it)
        })

        viewModel.onDetailShow.observe(viewLifecycleOwner, EventObserver {
            showItemDetail(it)
        })

        viewModel.isEmptyExpenseCount.observe(viewLifecycleOwner) {
            if (it) {
                disableMenuAction()
            } else {
               enableMenuActions()
            }
        }

        viewModel.selectedCount.observe(viewLifecycleOwner) observer@{
            if (it == 0) return@observer
            binding.tbExpense.title = "${itemNumberFormat.format(it)} ${
                if (it <= 1) getString(R.string.single_item_suffix) else getString(R.string.multi_item_suffix)
            }"
        }

        viewModel.isCurrentListEmpty.observe(viewLifecycleOwner) { isEmptyLogs ->
            if (isEmptyLogs) {
                showNoExpenseInfo()

                setEmptyStringOnToolbarSubtitle()
                unregisterFilterInfoObserver()
            } else {
                hideNoExpenseInfo()
                registerFilterInfoObserver()
            }
        }
    }

    private fun disableMenuAction() {
        binding.tbExpense.menu.forEach {
            it.isEnabled = false
        }
    }

    private fun enableMenuActions() {
        binding.tbExpense.menu.forEach {
            it.isEnabled = true
        }
    }

    private fun registerFilterInfoObserver() {
        viewModel.filterInfo.observe(viewLifecycleOwner, filterInfoObserver)
    }

    private fun unregisterFilterInfoObserver() {
        viewModel.filterInfo.removeObserver(filterInfoObserver)
    }

    private fun setEmptyStringOnToolbarSubtitle() {
        binding.tbExpense.subtitle = ""
    }

    private fun showItemDetail(detail: ExpenseDetailUiModel) {
        detailDialog?.dismiss()
        //Show Selected Dialog
        detailDialog = ExpenseDetailDialog()
        detailDialog?.setOnDeleteClickListener {
            detailDialog?.dismiss()
            viewModel.onSingleDeletePrepared(it.id)
        }
        detailDialog?.setOnEditClickListener {
            navigateToExpenseEntryFragment(detail.id)
        }
        detailDialog?.showDetail(parentFragmentManager, detail,isDeleteEnabled = true)
    }


    private fun navigateToExpenseEntryFragment(id: Int) {
        val action = ExpenseFragmentDirections.actionExpenseToEntry(expenseId = id)
        findNavController().navigate(action, entryNavOption)
    }

    private fun changeUiDefault() {
        val appBarElevation = binding.appBar.elevation
        with(binding.tbExpense) {
            menu.findItem(R.id.delete)?.isVisible = false
            menu.findItem(R.id.filter)?.isVisible = true
            title = getString(R.string.expense_logs)
            setNavigationIcon(R.drawable.ic_menu)
            setNavigationOnClickListener(::openNavDrawer)
        }
        binding.appBar.elevation = appBarElevation
        registerFilterInfoObserver()
        navigationDrawer.unlockDrawer() // Release from Selection
    }

    private fun changeUiSelection() {
        val appBarElevation = binding.appBar.elevation
        with(binding.tbExpense) {
            menu.findItem(R.id.delete)?.isVisible = true
            menu.findItem(R.id.filter)?.isVisible = false
            title = ""
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener(::clearSelectedItems)
        }
        binding.appBar.elevation = appBarElevation
        setEmptyStringOnToolbarSubtitle()
        unregisterFilterInfoObserver()
        navigationDrawer.lockDrawer()// Focus on Selection, Navigating other UI should'nt be on selection
    }

    private fun showNoExpenseInfo() {
        binding.layoutNoData.root.asVisible()
    }

    private fun hideNoExpenseInfo() {
        binding.layoutNoData.root.asInvisible()

    }

    private fun openNavDrawer(v: View) {
        navigationDrawer.openDrawer()
    }

    private fun clearSelectedItems(v: View) {
        viewModel.clearState()
    }


}
