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
    }
    
    private fun observeData() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
        }
        
        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.submitList(cartItems)
            binding.tvEmptyCart.visibility = if (cartItems.isEmpty()) View.VISIBLE else View.GONE
            binding.rvCart.visibility = if (cartItems.isEmpty()) View.GONE else View.VISIBLE
            
            // Update cart count
            val itemCount = cartItems.sumOf { it.quantity }
            binding.tvCartCount.text = if (itemCount > 0) "$itemCount item" else "0 item"
        }
        
        viewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.tvTotal.text = "Total: Rp ${String.format("%,.0f", total)}"
        }
        
        viewModel.transactionResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is KasirViewModel.TransactionResult.Success -> {
                    Toast.makeText(requireContext(), "Transaksi berhasil!", Toast.LENGTH_SHORT).show()
                }
                is KasirViewModel.TransactionResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
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