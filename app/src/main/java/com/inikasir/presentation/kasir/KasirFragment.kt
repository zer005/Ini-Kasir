package com.inikasir.presentation.kasir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< Updated upstream
import android.widget.Toast
=======
>>>>>>> Stashed changes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.inikasir.R
import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.databinding.FragmentKasirBinding
import java.text.SimpleDateFormat
import java.util.*

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
<<<<<<< Updated upstream
        productAdapter = ProductGridAdapter(
            onItemClick = { product ->
                viewModel.handleProductClick(product)
            },
            onShowVariants = { mainProduct ->
                showVariantDialog(mainProduct)
            }
        )
        
=======
        productAdapter = ProductGridAdapter { product ->
            // When product is clicked, check if it has variants
            viewModel.loadVariants(product.id)
        }

>>>>>>> Stashed changes
        val spanCount = resources.getInteger(R.integer.product_grid_span)

        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            adapter = productAdapter
        }
    }
<<<<<<< Updated upstream
    
    private fun showVariantDialog(mainProduct: com.inikasir.data.local.entity.ProductEntity) {
        viewModel.getVariants(mainProduct.id) { variants ->
            if (variants.isEmpty()) {
                // No variants, add directly
                viewModel.addToCart(mainProduct)
                return@getVariants
            }
            
            val variantNames = variants.map { v ->
                "${v.variantName} - Stok: ${v.stock}"
            }.toTypedArray()
            
            AlertDialog.Builder(requireContext())
                .setTitle("Pilih Varian ${mainProduct.name}")
                .setItems(variantNames) { _, which ->
                    val selectedVariant = variants[which]
                    if (selectedVariant.stock > 0) {
                        viewModel.addToCart(selectedVariant)
                    } else {
                        Toast.makeText(requireContext(), "Stok habis!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }
    
=======

>>>>>>> Stashed changes
    private fun setupCart() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { productId, quantity ->
                viewModel.updateQuantity(productId, quantity)
            },
            onRemoveItem = { productId ->
                viewModel.removeFromCart(productId)
            }
        )

        binding.rvCart?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupListeners() {
        binding.btnCheckout?.setOnClickListener {
            viewModel.checkout()
        }

        binding.btnClearCart?.setOnClickListener {
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

        // Observe variants - show dialog to select variant
        viewModel.variants.observe(viewLifecycleOwner) { variants ->
            if (variants.isNotEmpty()) {
                showVariantDialog(variants)
            }
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.submitList(cartItems)

            // Update visibility
            if (cartItems.isEmpty()) {
                binding.tvEmptyCart?.visibility = View.VISIBLE
                binding.rvCart?.visibility = View.GONE
            } else {
                binding.tvEmptyCart?.visibility = View.GONE
                binding.rvCart?.visibility = View.VISIBLE
            }

            // Update cart count
            val itemCount = cartItems.sumOf { it.quantity }
            binding.tvCartCount?.text = if (itemCount > 0) "$itemCount item" else "0 item"
        }

        viewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.tvTotal?.text = "Rp ${String.format("%,.0f", total)}"
        }

        viewModel.transactionResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is KasirViewModel.TransactionResult.Success -> {
<<<<<<< Updated upstream
                    showTransactionSuccessDialog(result.totalAmount)
                    // Cart is already cleared by ViewModel after successful transaction
=======
                    showSuccessDialog(result.transactionId, result.totalAmount)
                    viewModel.resetTransactionResult()
>>>>>>> Stashed changes
                }
                is KasirViewModel.TransactionResult.Error -> {
                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("❌ Transaksi Gagal")
                        .setMessage(result.message)
                        .setPositiveButton("OK", null)
                        .show()
                    viewModel.resetTransactionResult()
                }
                else -> {}
            }
        }
    }
<<<<<<< Updated upstream
    
    private fun showTransactionSuccessDialog(totalAmount: Double) {
        AlertDialog.Builder(requireContext())
            .setTitle("✅ Transaksi Berhasil")
            .setMessage("Total pembayaran:\n\nRp ${String.format("%,0.f", totalAmount)}\n\nTerima kasih!")
            .setPositiveButton("OK", null)
            .show()
    }
    
=======

    private fun showVariantDialog(variants: List<ProductEntity>) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID"))
        
        // Build list items with stock info
        val items = variants.map { variant ->
            val stockInfo = if (variant.stock > 0) "Stok: ${variant.stock}" else "Habis"
            "${variant.variantName} - $stockInfo"
        }.toTypedArray()

        // Filter out variants with 0 stock
        val availableVariants = variants.filter { it.stock > 0 }
        val availableItems = availableVariants.map { variant ->
            "${variant.variantName} - Stok: ${variant.stock}"
        }.toTypedArray()

        if (availableVariants.isEmpty()) {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Stok Habis")
                .setMessage("Semua varian untuk produk ini stoknya habis")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Pilih Varian - ${availableVariants.first().name}")
            .setItems(availableItems) { _, which ->
                val selectedVariant = availableVariants[which]
                viewModel.addToCart(selectedVariant)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showSuccessDialog(transactionId: Long, total: Double) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID"))
        val message = """
            ID Transaksi: #$transactionId
            Waktu: ${dateFormat.format(Date())}
            
            Total Pembayaran:
            Rp ${String.format("%,.0f", total)}
        """.trimIndent()

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("✅ Transaksi Berhasil!")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

>>>>>>> Stashed changes
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}