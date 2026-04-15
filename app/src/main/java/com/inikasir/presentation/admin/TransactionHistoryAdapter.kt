package com.inikasir.presentation.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inikasir.data.local.entity.TransactionEntity
import com.inikasir.databinding.ItemTransactionHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionHistoryAdapter(
    private val onItemClick: (TransactionEntity) -> Unit  // Tambah callback
) : ListAdapter<TransactionEntity, TransactionHistoryAdapter.TransactionViewHolder>(TransactionDiffCallback()) {
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding, dateFormat, onItemClick)
    }
    
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class TransactionViewHolder(
        private val binding: ItemTransactionHistoryBinding,
        private val dateFormat: SimpleDateFormat,
        private val onItemClick: (TransactionEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(transaction: TransactionEntity) {
            binding.tvTransactionId.text = "#${transaction.id}"
            binding.tvTransactionTotal.text = "Rp ${String.format("%,.0f", transaction.total)}"
            binding.tvTransactionDate.text = dateFormat.format(Date(transaction.date))
            
            binding.root.setOnClickListener {
                onItemClick(transaction)
            }
        }
    }
    
    class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionEntity>() {
        override fun areItemsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity): Boolean {
            return oldItem == newItem
        }
    }
}