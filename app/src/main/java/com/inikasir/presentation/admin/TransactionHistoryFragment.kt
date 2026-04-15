package com.inikasir.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.inikasir.R
import com.inikasir.databinding.FragmentTransactionHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionHistoryFragment : Fragment() {
    
    private var _binding: FragmentTransactionHistoryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AdminViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    ) {
        AdminViewModelFactory(requireContext())
    }
    
    private lateinit var transactionAdapter: TransactionHistoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeData()
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionHistoryAdapter { transaction ->
            showTransactionDetailDialog(transaction.id)
        }
        
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }
    
    private fun setupListeners() {
        binding.btnRecap.setOnClickListener {
            showRecapDialog()
        }
        
        binding.btnViewRecapHistory.setOnClickListener {
            showRecapHistoryDialog()
        }
    }
    
    private fun observeData() {
        // Observe unrecapped transactions
        viewModel.unrecappedTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
            
            // Update empty state
            if (transactions.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.rvTransactions.visibility = View.GONE
                binding.btnRecap.isEnabled = false
            } else {
                binding.emptyState.visibility = View.GONE
                binding.rvTransactions.visibility = View.VISIBLE
                binding.btnRecap.isEnabled = true
            }
            
            // Update summary
            val totalTransactions = transactions.size
            val totalRevenue = transactions.sumOf { it.total }
            
            binding.tvTotalTransactions.text = "$totalTransactions"
            binding.tvTotalRevenue.text = "Rp ${String.format("%,.0f", totalRevenue)}"
        }
        
        viewModel.recapResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AdminViewModel.RecapResult.Success -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("✅ Rekap Berhasil")
                        .setMessage("${result.transactionCount} transaksi telah direkap!\nTotal: Rp ${String.format("%,.0f", result.totalRevenue)}")
                        .setPositiveButton("OK") { _, _ ->
                            // Refresh the list after dialog is dismissed
                            viewModel.loadUnrecappedTransactions()
                        }
                        .show()
                }
                is AdminViewModel.RecapResult.Error -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("❌ Rekap Gagal")
                        .setMessage(result.message)
                        .setPositiveButton("OK", null)
                        .show()
                }
                else -> {}
            }
        }
    }
    
    private fun showRecapDialog() {
        val totalTransactions = viewModel.unrecappedTransactions.value?.size ?: 0
        val totalRevenue = viewModel.unrecappedTransactions.value?.sumOf { it.total } ?: 0.0
        
        AlertDialog.Builder(requireContext())
            .setTitle("📊 Rekap Transaksi")
            .setMessage("""
                Transaksi: $totalTransactions
                Total: Rp ${String.format("%,.0f", totalRevenue)}
                
                Semua transaksi yang belum direkap akan dipindahkan ke riwayat rekap.
                
                Lanjutkan?
            """.trimIndent())
            .setPositiveButton("✅ Rekap Sekarang") { _, _ ->
                viewModel.createRecap()
                // Clear the list immediately to prevent showing old transactions
                transactionAdapter.submitList(emptyList())
            }
            .setNegativeButton("❌ Batal", null)
            .show()
    }
    
    private fun showTransactionDetailDialog(transactionId: Long) {
        viewModel.getTransactionDetail(transactionId) { transaction, details ->
            val dialogView = layoutInflater.inflate(R.layout.dialog_transaction_detail, null)
            val tvTransactionInfo = dialogView.findViewById<TextView>(R.id.tvTransactionInfo)
            val rvDetailItems = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvDetailItems)
            val tvTotalDetail = dialogView.findViewById<TextView>(R.id.tvTotalDetail)
            
            val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID"))
            tvTransactionInfo.text = "Transaksi #${transaction?.id ?: ""}\n${dateFormat.format(Date(transaction?.date ?: 0))}"
            tvTotalDetail.text = "Total: Rp ${String.format("%,.0f", transaction?.total ?: 0.0)}"
            
            val detailAdapter = TransactionDetailAdapter()
            rvDetailItems.layoutManager = LinearLayoutManager(requireContext())
            rvDetailItems.adapter = detailAdapter
            detailAdapter.submitList(details)
            
            AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Tutup", null)
                .show()
        }
    }
    
    private fun showRecapHistoryDialog() {
        val recaps = viewModel.recaps.value
        if (recaps.isNullOrEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("📊 Riwayat Rekap")
                .setMessage("Belum ada rekap")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val items = recaps.map { recap ->
            "${dateFormat.format(Date(recap.startDate))} - ${dateFormat.format(Date(recap.endDate))}\n" +
            "${recap.transactionCount} transaksi | Rp ${String.format("%,.0f", recap.totalRevenue)}"
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("📊 Riwayat Rekap")
            .setItems(items) { _, which ->
                val selectedRecap = recaps[which]
                showRecapDetailDialog(selectedRecap)
            }
            .setPositiveButton("Tutup", null)
            .show()
    }

    private fun showRecapDetailDialog(recap: com.inikasir.data.local.entity.RecapEntity) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID"))
        val message = """
            Periode: ${dateFormat.format(Date(recap.startDate))} - ${dateFormat.format(Date(recap.endDate))}
            Total Transaksi: ${recap.transactionCount}
            Total Pendapatan: Rp ${String.format("%,.0f", recap.totalRevenue)}
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("📋 Detail Rekap #${recap.id}")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}