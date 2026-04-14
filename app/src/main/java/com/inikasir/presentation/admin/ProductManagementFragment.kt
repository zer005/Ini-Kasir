package com.inikasir.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.inikasir.R
import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.databinding.FragmentProductManagementBinding

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
        
        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Produk")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val name = etName.text.toString()
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                viewModel.addProduct(name, price)
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    private fun showEditProductDialog(product: ProductEntity) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val etName = dialogView.findViewById<EditText>(R.id.etProductName)
        val etPrice = dialogView.findViewById<EditText>(R.id.etProductPrice)
        
        etName.setText(product.name)
        etPrice.setText(product.price.toString())
        
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Produk")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = etName.text.toString()
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                viewModel.updateProduct(product.id, name, price)
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