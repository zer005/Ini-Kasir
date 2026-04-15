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
            // Display product name with variant info
            val displayName = if (product.variantName != null) {
                "${product.name} - ${product.variantName}"
            } else {
                product.name
            }
            binding.tvProductName.text = displayName
            binding.tvProductPrice.text = "Rp ${String.format("%,.0f", product.price)}"

            // Display stock info
            binding.tvStock.text = "Stok: ${product.stock}"

            // Show/hide variant badge
            if (product.variantName != null) {
                binding.tvVariantBadge.visibility = android.view.View.VISIBLE
                binding.tvVariantBadge.text = "Varian"
            } else {
                binding.tvVariantBadge.visibility = android.view.View.GONE
            }

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