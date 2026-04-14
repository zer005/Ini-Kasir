package com.inikasir.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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
        observeData()
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionHistoryAdapter()
        
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }
    
    private fun observeData() {
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
            binding.tvEmptyTransactions.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}