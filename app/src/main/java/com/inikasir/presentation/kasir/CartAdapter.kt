package com.inikasir.presentation.kasir

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inikasir.databinding.ItemCartBinding
import com.inikasir.domain.model.CartItem

class CartAdapter(
    private val onQuantityChanged: (Long, Int) -> Unit,
    private val onRemoveItem: (Long) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding, onQuantityChanged, onRemoveItem)
    }
    
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class CartViewHolder(
        private val binding: ItemCartBinding,
        private val onQuantityChanged: (Long, Int) -> Unit,
        private val onRemoveItem: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private var currentItem: CartItem? = null
        
        init {
            binding.btnMinus.setOnClickListener {
                currentItem?.let { item ->
                    onQuantityChanged(item.productId, item.quantity - 1)
                }
            }
            
            binding.btnPlus.setOnClickListener {
                currentItem?.let { item ->
                    onQuantityChanged(item.productId, item.quantity + 1)
                }
            }
            
            binding.btnRemove.setOnClickListener {
                currentItem?.let { item ->
                    onRemoveItem(item.productId)
                }
            }
        }
        
        fun bind(item: CartItem) {
            currentItem = item
            binding.tvCartProductName.text = item.productName
            binding.tvCartQuantity.text = "${item.quantity}x"
            binding.tvCartSubtotal.text = "Rp ${String.format("%,.0f", item.subtotal)}"
        }
    }
    
    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.productId == newItem.productId
        }
        
        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}