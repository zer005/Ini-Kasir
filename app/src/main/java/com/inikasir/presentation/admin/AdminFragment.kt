package com.inikasir.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.inikasir.databinding.FragmentAdminBinding
import com.inikasir.presentation.kasir.KasirFragment

class AdminFragment : Fragment() {
    
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AdminViewModel by viewModels {
        AdminViewModelFactory(requireContext())
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        observeData()
    }
    
    private fun setupViewPager() {
        val fragments = listOf(
            ProductManagementFragment(),
            TransactionHistoryFragment(),
            BackupFragment()  // Tambahkan ini
        )
        
        val titles = listOf("Produk", "Transaksi", "Backup")  // Update titles
        
        val adapter = AdminPagerAdapter(this, fragments)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
    
    private fun observeData() {
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}