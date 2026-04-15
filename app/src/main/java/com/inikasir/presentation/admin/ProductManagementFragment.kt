package com.inikasir.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.inikasir.R
import com.inikasir.data.local.AppDatabase
import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.databinding.FragmentProductManagementBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductManagementFragment : Fragment() {

    private var _binding: FragmentProductManagementBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    ) {
        AdminViewModelFactory(requireContext())
    }

    private lateinit var productAdapter: ProductManagementAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductManagementBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeData()
    }
    
    private fun setupRecyclerView() {
        productAdapter = ProductManagementAdapter(
            onEditClick = { product ->
                showEditProductDialog(product)
            },
            onDeleteClick = { product ->
                showDeleteConfirmation(product)
            }
        )
        
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }
    
    private fun setupFab() {
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fabAddProduct)
        fab.setOnClickListener {
            showAddProductDialog()
        }
    }
    
    private fun observeData() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
            
            val emptyState = binding.root.findViewById<View>(R.id.emptyState)
            val rvProducts = binding.rvProducts
            
            if (products.isEmpty()) {
                emptyState.visibility = View.VISIBLE
                rvProducts.visibility = View.GONE
            } else {
                emptyState.visibility = View.GONE
                rvProducts.visibility = View.VISIBLE
            }
        }
    }
    
    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val etName = dialogView.findViewById<EditText>(R.id.etProductName)
        val etPrice = dialogView.findViewById<EditText>(R.id.etProductPrice)
        val etStock = dialogView.findViewById<EditText>(R.id.etProductStock)

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Produk")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val name = etName.text.toString().trim()
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                val stock = etStock.text.toString().toIntOrNull() ?: 0

                if (name.isBlank() || price <= 0) {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Nama dan harga harus valid",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                // Tanya apakah ini varian atau produk utama
                showProductTypeDialog(name, price, stock, null, null)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showProductTypeDialog(
        name: String,
        price: Double,
        stock: Int,
        parentId: Long?,
        variantName: String?
    ) {
        if (parentId == null) {
            // Ini produk utama, tanyakan apakah ada varian
            AlertDialog.Builder(requireContext())
                .setTitle("Apakah produk ini memiliki varian?")
                .setMessage("Contoh: Pop Ice dengan berbagai rasa")
                .setPositiveButton("Ya, tambah varian") { _, _ ->
                    showAddVariantDialog(name, price, stock)
                }
                .setNegativeButton("Tidak, produk tunggal") { _, _ ->
                    viewModel.addProduct(name, price, stock)
                }
                .show()
        } else {
            // Ini varian
            viewModel.addProduct(name, price, stock, parentId, variantName)
        }
    }

    private fun showAddVariantDialog(parentName: String, parentPrice: Double, parentStock: Int) {
        // Pertama, simpan produk utama
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val parentId = withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(requireContext())
                        .let { db ->
                            val productRepo = com.inikasir.data.repository.ProductRepository(db)
                            productRepo.insertProduct(parentName, parentPrice, parentStock)
                        }
                }

                // Tampilkan dialog untuk tambah varian
                val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
                val etVariantName = dialogView.findViewById<EditText>(R.id.etProductName)
                val etVariantPrice = dialogView.findViewById<EditText>(R.id.etProductPrice)
                val etVariantStock = dialogView.findViewById<EditText>(R.id.etProductStock)

                etVariantName.hint = "Nama Varian (contoh: Rasa Strawberry)"
                etVariantPrice.setText(parentPrice.toString())
                etVariantStock.setText(parentStock.toString())

                AlertDialog.Builder(requireContext())
                    .setTitle("Tambah Varian untuk $parentName")
                    .setView(dialogView)
                    .setPositiveButton("Tambah Varian") { _, _ ->
                        val variantName = etVariantName.text.toString().trim()
                        val variantPrice = etVariantPrice.text.toString().toDoubleOrNull() ?: parentPrice
                        val variantStock = etVariantStock.text.toString().toIntOrNull() ?: parentStock

                        if (variantName.isBlank()) {
                            android.widget.Toast.makeText(
                                requireContext(),
                                "Nama varian harus diisi",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            return@setPositiveButton
                        }

                        viewModel.addProduct(parentName, variantPrice, variantStock, parentId, variantName)

                        // Tanya apakah mau tambah varian lagi
                        AlertDialog.Builder(requireContext())
                            .setTitle("Tambah varian lain?")
                            .setPositiveButton("Ya") { _, _ ->
                                showAddVariantDialog(parentName, parentPrice, parentStock)
                            }
                            .setNegativeButton("Tidak", null)
                            .show()
                    }
                    .setNegativeButton("Lewati") { _, _ ->
                        // Sudah simpan produk utama, tidak tambah varian
                    }
                    .show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Gagal menambah produk: ${e.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun showEditProductDialog(product: ProductEntity) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val etName = dialogView.findViewById<EditText>(R.id.etProductName)
        val etPrice = dialogView.findViewById<EditText>(R.id.etProductPrice)
        val etStock = dialogView.findViewById<EditText>(R.id.etProductStock)

        etName.setText(product.name)
        etPrice.setText(product.price.toString())
        etStock.setText(product.stock.toString())

        // Jika produk memiliki varian, tampilkan info
        if (product.variantName != null) {
            etName.hint = "Nama Varian"
            etName.setText(product.variantName)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (product.variantName != null) "Edit Varian" else "Edit Produk")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = if (product.variantName != null) {
                    product.name // Nama produk utama tetap
                } else {
                    etName.text.toString()
                }
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                val stock = etStock.text.toString().toIntOrNull() ?: 0
                val variantName = if (product.variantName != null) {
                    etName.text.toString()
                } else {
                    null
                }

                viewModel.updateProduct(product.id, name, price, stock, product.parentId, variantName)
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    private fun showDeleteConfirmation(product: ProductEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Produk")
            .setMessage("Yakin ingin menghapus ${product.name}?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteProduct(product)
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}