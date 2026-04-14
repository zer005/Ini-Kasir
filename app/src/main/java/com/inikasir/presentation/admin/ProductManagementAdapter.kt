package com.inikasir.presentation.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.databinding.ItemProductManagementBinding

class ProductManagementAdapter(
    private val onEditClick: (ProductEntity) -> Unit,
    private val onDeleteClick: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductManagementAdapter.ProductViewHolder>(ProductDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductManagementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onEditClick, onDeleteClick)
    }
    
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ProductViewHolder(
        private val binding: ItemProductManagementBinding,
        private val onEditClick: (ProductEntity) -> Unit,
        private val onDeleteClick: (ProductEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(product: ProductEntity) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "Rp ${String.format("%,.0f", product.price)}"
            
            binding.btnEdit.setOnClickListener {
                onEditClick(product)
            }
            
            binding.btnDelete.setOnClickListener {
                onDeleteClick(product)
            }
        }
    }
    
    class ProductDiffCallback : DiffUtil.ItemCallback<ProductEntity>() {
        override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
            return oldItem == newItem
        }
    }
}