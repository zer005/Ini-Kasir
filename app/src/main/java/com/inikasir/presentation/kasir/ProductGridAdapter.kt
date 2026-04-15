package com.inikasir.presentation.kasir

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.databinding.ItemProductGridBinding

class ProductGridAdapter(
    private val onItemClick: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductGridAdapter.ProductViewHolder>(ProductDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onItemClick)
    }
    
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ProductViewHolder(
        private val binding: ItemProductGridBinding,
        private val onItemClick: (ProductEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(product: ProductEntity) {
            val displayName = if (product.variantName != null) {
                "${product.name} (${product.variantName})"
            } else {
                product.name
            }
            
            binding.tvProductName.text = displayName
            binding.tvProductPrice.text = "Rp ${String.format("%,.0f", product.price)}"
            
            // Stock info
            if (product.stock > 0) {
                binding.tvStock.text = "Stok: ${product.stock}"
                binding.tvStock.setTextColor(Color.GRAY)
                binding.root.alpha = 1.0f
                binding.root.isEnabled = true
            } else {
                binding.tvStock.text = "Habis"
                binding.tvStock.setTextColor(Color.RED)
                binding.root.alpha = 0.5f
                binding.root.isEnabled = false
            }
            
            binding.root.setOnClickListener {
                if (product.stock > 0) {
                    onItemClick(product)
                }
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