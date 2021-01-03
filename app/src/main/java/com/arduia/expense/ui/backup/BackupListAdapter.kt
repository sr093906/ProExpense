package com.arduia.expense.ui.backup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.R
import com.arduia.expense.databinding.ItemBackupBinding

class BackupListAdapter(private val layoutInflater: LayoutInflater) :
    ListAdapter<BackupUiModel, BackupListAdapter.VH>(DIFF_UTIL) {

    private var itemClickListener = { _: BackupUiModel -> }

    private val itemsSuffix = layoutInflater.context.getString(R.string.single_item_suffix)
    private val singleItemSuffix = layoutInflater.context.getString(R.string.multi_item_suffix)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewBinding = ItemBackupBinding.inflate(layoutInflater)

        return VH(viewBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        with(holder.viewBinding) {
            val item = getItem(position)

            tvBackupName.text = item.name
            tvDate.text = item.date
            tvItems.text = item.items
            pbStatus.visibility = if (item.onProgress) View.VISIBLE else View.INVISIBLE
            imvDelete.visibility = if (item.onProgress) View.INVISIBLE else View.VISIBLE
            if (item.onProgress.not()) {
                rlBackup.setOnClickListener(holder)
            }
        }
    }

    inner class VH(val viewBinding: ItemBackupBinding) :
        RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener {

        override fun onClick(v: View?) {
            val item = getItem(adapterPosition)

            itemClickListener.invoke(item)
        }

    }

    fun setItemClickListener(listener: (BackupUiModel) -> Unit) {
        this.itemClickListener = listener
    }
}

private val DIFF_UTIL
    get() = object : DiffUtil.ItemCallback<BackupUiModel>() {
        override fun areItemsTheSame(oldItem: BackupUiModel, newItem: BackupUiModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BackupUiModel, newItem: BackupUiModel): Boolean {
            return (oldItem.date == newItem.date) &&
                    (oldItem.id == newItem.id) &&
                    (oldItem.name == newItem.name) &&
                    (oldItem.onProgress == newItem.onProgress) &&
                    (oldItem.items == newItem.items)
        }
    }