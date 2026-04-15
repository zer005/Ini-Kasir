package com.inikasir.presentation.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inikasir.data.local.dao.TransactionDetailWithProduct
import com.inikasir.databinding.ItemTransactionDetailBinding

class TransactionDetailAdapter : ListAdapter<TransactionDetailWithProduct, TransactionDetailAdapter.ViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ViewHolder(private val binding: ItemTransactionDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(detail: TransactionDetailWithProduct) {
            val displayName = if (detail.variantName != null) {
                "${detail.productName} (${detail.variantName})"
            } else {
                detail.productName
            }
            
            binding.tvProductName.text = displayName
            binding.tvQuantity.text = "${detail.quantity} x Rp ${String.format("%,.0f", detail.price)}"
            binding.tvSubtotal.text = "Rp ${String.format("%,.0f", detail.subtotal)}"
        }
    }
    
    class DiffCallback : DiffUtil.ItemCallback<TransactionDetailWithProduct>() {
        override fun areItemsTheSame(oldItem: TransactionDetailWithProduct, newItem: TransactionDetailWithProduct): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: TransactionDetailWithProduct, newItem: TransactionDetailWithProduct): Boolean {
            return oldItem == newItem
        }
    }
}