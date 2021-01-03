package com.arduia.expense.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arduia.expense.databinding.ItemLanguageBinding
import com.arduia.expense.ui.common.language.LanguageUiModel

class LangListAdapter(private val layoutInflater: LayoutInflater) :
    ListAdapter<LanguageUiModel, LangListAdapter.VH>(
        DIFFER
    ) {

    private var onItemClickListener: (LanguageUiModel) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemLanguageBinding.inflate(layoutInflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        with(holder.binding) {
            val item = getItem(position)
            tvLanguageName.text = item.name
            imvFlag.setImageResource(item.flag)
            imvChecked.visibility = item.isSelectedVisible
        }
    }

    fun setOnItemClickListener(listener: (LanguageUiModel)->Unit){
        this.onItemClickListener = listener
    }

    inner class VH(val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cdLanguage.setOnClickListener {
                onItemClickListener.invoke(getItem(adapterPosition))
            }
        }
    }
}

private val DIFFER = object : DiffUtil.ItemCallback<LanguageUiModel>() {
    override fun areItemsTheSame(oldItem: LanguageUiModel, newItem: LanguageUiModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: LanguageUiModel, newItem: LanguageUiModel): Boolean {
        return (oldItem.name == newItem.name) and
                (oldItem.flag == newItem.flag) and
                (oldItem.id == newItem.id) and
                (oldItem.isSelectedVisible == newItem.isSelectedVisible)
    }
}