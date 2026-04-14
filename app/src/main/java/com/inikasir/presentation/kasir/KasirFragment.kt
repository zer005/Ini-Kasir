package com.inikasir.presentation.kasir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.inikasir.R
import com.inikasir.databinding.FragmentKasirBinding
import com.inikasir.presentation.common.BaseFragment

class KasirFragment : BaseFragment<FragmentKasirBinding>() {
    
    private lateinit var productAdapter: ProductGridAdapter
    private lateinit var cartAdapter: CartAdapter
    
    private val viewModel: KasirViewModel by viewModels {
        KasirViewModelFactory(requireContext())
    }
    
    override fun getLayoutRes(): Int = R.layout.fragment_kasir
    
    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentKasirBinding {
        return FragmentKasirBinding.inflate(inflater, container, false)
    }
    
    override fun setupViews() {
        setupProductGrid()
        setupCart()
        setupListeners()
    }
    
    private fun setupProductGrid() {
        productAdapter = ProductGridAdapter { product ->
            viewModel.addToCart(product)
        }
        
        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }
    
    private fun setupCart() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { productId, quantity ->
                viewModel.updateQuantity(productId, quantity)
            },
            onRemoveItem = { productId ->
                viewModel.removeFromCart(productId)
            }
        )
        
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }
    
    private fun setupListeners() {
        binding.btnCheckout.setOnClickListener {
            viewModel.checkout()
        }
        
        binding.btnClearCart.setOnClickListener {
            viewModel.clearCart()
        }
        
        binding.etSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.updateSearchQuery(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.updateSearchQuery(it) }
                return true
            }
        })
    }
    
    override fun observeData() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            val query = viewModel.searchQuery.value ?: ""
            val filteredProducts = if (query.isEmpty()) {
                products
            } else {
                products.filter { 
                    it.name.contains(query, ignoreCase = true) 
                }
            }
            productAdapter.submitList(filteredProducts)
        }
        
        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.submitList(cartItems)
            binding.tvEmptyCart.visibility = if (cartItems.isEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.tvTotal.text = "Total: Rp ${String.format("%,.0f", total)}"
        }
        
        viewModel.transactionResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is KasirViewModel.TransactionResult.Success -> {
                    Toast.makeText(requireContext(), "Transaksi berhasil! ID: ${result.transactionId}", Toast.LENGTH_LONG).show()
                }
                is KasirViewModel.TransactionResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}