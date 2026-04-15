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
    private val onItemClick: (ProductEntity) -> Unit,
    private val onShowVariants: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductGridAdapter.ProductViewHolder>(ProductDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onItemClick, onShowVariants)
    }
    
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ProductViewHolder(
        private val binding: ItemProductGridBinding,
        private val onItemClick: (ProductEntity) -> Unit,
        private val onShowVariants: (ProductEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductEntity) {
<<<<<<< Updated upstream
            // For main products (no parentId), show only the product name without variant info
            val displayName = if (product.parentId == null && product.variantName == null) {
                product.name
            } else if (product.variantName != null) {
                "${product.name} (${product.variantName})"
            } else {
                product.name
            }
            
            binding.tvProductName.text = displayName
            binding.tvProductPrice.text = "Rp ${String.format("%,.0f", product.price)}"
            
            // Calculate total stock for main products with variants
            val displayStock = if (product.parentId == null && product.variantName == null) {
                // Main product - will show variants on click
                "Tap untuk varian"
            } else {
                if (product.stock > 0) {
                    "Stok: ${product.stock}"
                } else {
                    "Habis"
                }
            }
            
            binding.tvStock.text = displayStock
            
            // Visual state based on stock
            if (product.parentId == null && product.variantName == null) {
                // Main product - always enabled
                binding.tvStock.setTextColor(Color.BLUE)
                binding.root.alpha = 1.0f
                binding.root.isEnabled = true
            } else if (product.stock > 0) {
                binding.tvStock.setTextColor(Color.GRAY)
                binding.root.alpha = 1.0f
                binding.root.isEnabled = true
            } else {
                binding.tvStock.setTextColor(Color.RED)
                binding.root.alpha = 0.5f
                binding.root.isEnabled = false
=======
            // Only show main products (parentId == null)
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "Rp ${String.format("%,.0f", product.price)}"

            // For main products, show total stock from all variants
            if (product.parentId == null && product.variantName == null) {
                // This is a main product - we'll load variants on click
                binding.tvStock.text = "Tap untuk pilih varian"
                binding.tvStock.setTextColor(android.graphics.Color.parseColor("#9E9E9E"))
                binding.root.alpha = 1.0f
                binding.root.isEnabled = true
            } else {
                // This shouldn't happen since we only show main products
                binding.tvStock.text = "Stok: ${product.stock}"
                binding.tvStock.setTextColor(android.graphics.Color.GRAY)
                binding.root.alpha = 1.0f
                binding.root.isEnabled = true
>>>>>>> Stashed changes
            }

            binding.root.setOnClickListener {
<<<<<<< Updated upstream
                if (product.parentId == null && product.variantName == null) {
                    // Main product - show variants dialog
                    onShowVariants(product)
                } else if (product.stock > 0) {
                    // Variant or single product - add directly
                    onItemClick(product)
                }
=======
                onItemClick(product)
>>>>>>> Stashed changes
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