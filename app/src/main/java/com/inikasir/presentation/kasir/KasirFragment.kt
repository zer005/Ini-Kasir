package com.inikasir.presentation.kasir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.inikasir.R
import com.inikasir.databinding.FragmentKasirBinding

class KasirFragment : Fragment() {
    
    private var _binding: FragmentKasirBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var productAdapter: ProductGridAdapter
    private lateinit var cartAdapter: CartAdapter
    
    private val viewModel: KasirViewModel by viewModels {
        KasirViewModelFactory(requireContext())
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKasirBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeData()
    }
    
    private fun setupViews() {
        setupProductGrid()
        setupCart()
        setupListeners()
    }
    
    private fun setupProductGrid() {
        productAdapter = ProductGridAdapter { product ->
            viewModel.addToCart(product)
        }
        
        val spanCount = resources.getInteger(R.integer.product_grid_span)
        
        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), spanCount)
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
        
        binding.rvCart!!.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }
    
    private fun setupListeners() {
        binding.btnCheckout!!.setOnClickListener {
            viewModel.checkout()
        }
        
        binding.btnClearCart!!.setOnClickListener {
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
    
    private fun observeData() {
        // Observe products from database
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
            
            // Update visibility
            if (cartItems.isEmpty()) {
                binding.tvEmptyCart!!.visibility = View.VISIBLE
                binding.rvCart!!.visibility = View.GONE
            } else {
                binding.tvEmptyCart!!.visibility = View.GONE
                binding.rvCart!!.visibility = View.VISIBLE
            }
            
            // Update cart count
            val itemCount = cartItems.sumOf { it.quantity }
            binding.tvCartCount!!.text = if (itemCount > 0) "$itemCount item" else "0 item"
        }
        
        viewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.tvTotal!!.text = "Rp ${String.format("%,.0f", total)}"
        }
        
        viewModel.transactionResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is KasirViewModel.TransactionResult.Success -> {
                    Toast.makeText(requireContext(), "✅ Transaksi berhasil!", Toast.LENGTH_SHORT).show()
                }
                is KasirViewModel.TransactionResult.Error -> {
                    Toast.makeText(requireContext(), "❌ ${result.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}