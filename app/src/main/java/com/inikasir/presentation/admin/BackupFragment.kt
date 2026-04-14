package com.inikasir.presentation.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.inikasir.InikasirApplication
import com.inikasir.databinding.FragmentBackupBinding
import java.io.File

class BackupFragment : Fragment() {
    
    private var _binding: FragmentBackupBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: BackupViewModel by viewModels {
        BackupViewModelFactory(requireContext())
    }
    
    private var selectedProductsFile: File? = null
    private var selectedTransactionsFile: File? = null
    private var selectedDetailsFile: File? = null
    
    private val pickProductsFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { 
            selectedProductsFile = uriToFile(it)
            binding.tvProductsFile.text = selectedProductsFile?.name ?: "File dipilih"
        }
    }
    
    private val pickTransactionsFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { 
            selectedTransactionsFile = uriToFile(it)
            binding.tvTransactionsFile.text = selectedTransactionsFile?.name ?: "File dipilih"
        }
    }
    
    private val pickDetailsFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { 
            selectedDetailsFile = uriToFile(it)
            binding.tvDetailsFile.text = selectedDetailsFile?.name ?: "File dipilih"
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBackupBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeData()
    }
    
    private fun setupListeners() {
        binding.btnExport.setOnClickListener {
            viewModel.exportToCSV()
        }
        
        binding.btnSelectProducts.setOnClickListener {
            pickProductsFile.launch("*/*")
        }
        
        binding.btnSelectTransactions.setOnClickListener {
            pickTransactionsFile.launch("*/*")
        }
        
        binding.btnSelectDetails.setOnClickListener {
            pickDetailsFile.launch("*/*")
        }
        
        binding.btnImport.setOnClickListener {
            viewModel.importFromCSV(
                selectedProductsFile,
                selectedTransactionsFile,
                selectedDetailsFile
            )
        }
    }
    
    private fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.backupResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is BackupViewModel.BackupResult.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Backup berhasil!\nProduk: ${result.productsPath}\nTransaksi: ${result.transactionsPath}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is BackupViewModel.BackupResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
        
        viewModel.restoreResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is BackupViewModel.RestoreResult.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Restore berhasil!\nProduk: ${result.productsCount}\nTransaksi: ${result.transactionsCount}\nDetail: ${result.detailsCount}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is BackupViewModel.RestoreResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
    
    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File(requireContext().cacheDir, "temp_${System.currentTimeMillis()}.csv")
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            inputStream?.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}