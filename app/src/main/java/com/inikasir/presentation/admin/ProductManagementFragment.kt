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
        AlertDialog.Builder(requireContext())
            .setTitle("Jenis Produk")
            .setMessage("Apakah produk ini memiliki varian?")
            .setPositiveButton("Produk Tunggal") { _, _ ->
                showAddSingleProductDialog()
            }
            .setNegativeButton("Produk dengan Varian") { _, _ ->
                showAddProductWithVariantsDialog()
            }
            .show()
    }

    private fun showAddSingleProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val etName = dialogView.findViewById<EditText>(R.id.etProductName)
        val etPrice = dialogView.findViewById<EditText>(R.id.etProductPrice)
        
        // Hide stock field initially - will show after choosing product type
        val etStock = dialogView.findViewById<EditText>(R.id.etProductStock)
        etStock.visibility = View.GONE

        etStock.hint = "Stok"

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Produk Tunggal")
            .setView(dialogView)
            .setPositiveButton("Lanjut") { _, _ ->
                val name = etName.text.toString().trim()
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isBlank() || price <= 0) {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Nama dan harga harus valid",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

<<<<<<< Updated upstream
                // Tanya apakah ini varian atau produk utama
                showProductTypeDialog(name, price)
=======
                viewModel.addProduct(name, price, stock)
>>>>>>> Stashed changes
            }
            .setNegativeButton("Batal", null)
            .show()
    }

<<<<<<< Updated upstream
    private fun showProductTypeDialog(name: String, price: Double) {
        AlertDialog.Builder(requireContext())
            .setTitle("Apakah produk ini memiliki varian?")
            .setMessage("Contoh: Pop Ice dengan berbagai rasa")
            .setPositiveButton("Ya, banyak varian") { _, _ ->
                showAddVariantDialog(name, price)
            }
            .setNegativeButton("Tidak, produk tunggal") { _, _ ->
                // Show stock input for single product
                showStockInputDialog(name, price, isVariant = false)
            }
            .show()
    }

    private fun showStockInputDialog(name: String, price: Double, isVariant: Boolean, parentId: Long? = null, variantName: String? = null) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val etStock = dialogView.findViewById<EditText>(R.id.etProductStock)
        
        // Hide name and price fields
        dialogView.findViewById<View>(R.id.etProductName).visibility = View.GONE
        dialogView.findViewById<View>(R.id.etProductPrice).visibility = View.GONE
        
        etStock.hint = "Stok"
        etStock.setText("0")

        AlertDialog.Builder(requireContext())
            .setTitle(if (isVariant) "Tambah Stok Varian" else "Tambah Stok Produk")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val stock = etStock.text.toString().toIntOrNull() ?: 0
                
                if (isVariant) {
                    viewModel.addProduct(name, price, stock, parentId, variantName)
                } else {
                    viewModel.addProduct(name, price, stock)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showAddVariantDialog(parentName: String, parentPrice: Double) {
        // First, save the main product with stock 0
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val parentId = withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(requireContext())
                        .let { db ->
                            val productRepo = com.inikasir.data.repository.ProductRepository(db)
                            productRepo.insertProduct(parentName, parentPrice, 0)
                        }
                }

                // Show dialog to add variant
                val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
                val etVariantName = dialogView.findViewById<EditText>(R.id.etProductName)
                val etVariantPrice = dialogView.findViewById<EditText>(R.id.etProductPrice)
                
                // Hide stock field - will be shown in next step
                val etVariantStock = dialogView.findViewById<EditText>(R.id.etProductStock)
                etVariantStock.visibility = View.GONE

                etVariantName.hint = "Nama Varian (contoh: Rasa Strawberry)"
                etVariantPrice.setText(parentPrice.toString())

                AlertDialog.Builder(requireContext())
                    .setTitle("Tambah Varian untuk $parentName")
                    .setView(dialogView)
                    .setPositiveButton("Lanjut") { _, _ ->
                        val variantName = etVariantName.text.toString().trim()
                        val variantPrice = etVariantPrice.text.toString().toDoubleOrNull() ?: parentPrice

                        if (variantName.isBlank()) {
                            android.widget.Toast.makeText(
                                requireContext(),
                                "Nama varian harus diisi",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            return@setPositiveButton
                        }

                        // Now ask for stock
                        showStockInputDialog(parentName, variantPrice, isVariant = true, parentId = parentId, variantName = variantName)

                        // Ask if want to add more variants
                        AlertDialog.Builder(requireContext())
                            .setTitle("Tambah varian lain?")
                            .setPositiveButton("Ya") { _, _ ->
                                showAddVariantDialog(parentName, parentPrice)
                            }
                            .setNegativeButton("Tidak", null)
                            .show()
                    }
                    .setNegativeButton("Lewati") { _, _ ->
                        // Already saved main product, no variants added
                    }
                    .show()
=======
    private fun showAddProductWithVariantsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val etName = dialogView.findViewById<EditText>(R.id.etProductName)
        val etPrice = dialogView.findViewById<EditText>(R.id.etProductPrice)
        val etStock = dialogView.findViewById<EditText>(R.id.etProductStock)

        etStock.visibility = View.GONE

        AlertDialog.Builder(requireContext())
            .setTitle("Produk dengan Varian - Info Dasar")
            .setMessage("Masukkan nama produk dan harga. Varian akan ditambahkan setelahnya.")
            .setView(dialogView)
            .setPositiveButton("Lanjut") { _, _ ->
                val name = etName.text.toString().trim()
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isBlank() || price <= 0) {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Nama dan harga harus valid",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                saveMainProductAndAddVariants(name, price)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun saveMainProductAndAddVariants(name: String, price: Double) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val parentId = withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(requireContext())
                    val productRepo = com.inikasir.data.repository.ProductRepository(db)
                    productRepo.insertProduct(name, price, 0)
                }

                showAddVariantDialog(name, price, parentId)
>>>>>>> Stashed changes
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Gagal menambah produk: ${e.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showAddVariantDialog(parentName: String, parentPrice: Double, parentId: Long) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val etVariantName = dialogView.findViewById<EditText>(R.id.etProductName)
        val etPrice = dialogView.findViewById<EditText>(R.id.etProductPrice)
        val etStock = dialogView.findViewById<EditText>(R.id.etProductStock)

        etVariantName?.hint = "Nama Varian (contoh: Rasa Strawberry)"
        etPrice?.setText(parentPrice.toString().toInt().toString())
        etStock?.hint = "Stok varian ini"

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Varian - $parentName")
            .setMessage("Masukkan nama varian dan stok")
            .setView(dialogView)
            .setPositiveButton("Tambah Varian", null) // null dulu, override nanti
            .setNegativeButton("Batal", null)
            .show().also { dialog ->
                // Override positive button agar tidak auto-dismiss
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val variantName = etVariantName?.text.toString().trim()
                    val variantPrice = etPrice?.text.toString().toDoubleOrNull() ?: parentPrice
                    val variantStock = etStock?.text.toString().toIntOrNull() ?: 0

                    if (variantName.isBlank()) {
                        android.widget.Toast.makeText(
                            requireContext(),
                            "Nama varian harus diisi",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    viewModel.addProduct(parentName, variantPrice, variantStock, parentId, variantName)

                    dialog.dismiss()

                    // Tanya apakah mau tambah varian lain
                    AlertDialog.Builder(requireContext())
                        .setTitle("Tambah varian lain?")
                        .setPositiveButton("Ya") { _, _ ->
                            showAddVariantDialog(parentName, parentPrice, parentId)
                        }
                        .setNegativeButton("Tidak, selesai") { _, _ ->
                        }
                        .show()
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

        if (product.variantName != null) {
            etName.hint = "Nama Varian"
            etName.setText(product.variantName)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (product.variantName != null) "Edit Varian" else "Edit Produk")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = if (product.variantName != null) {
                    product.name
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
