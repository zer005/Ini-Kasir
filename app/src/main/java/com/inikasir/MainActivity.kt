package com.inikasir

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.inikasir.databinding.ActivityMainBinding
import com.inikasir.presentation.admin.AdminFragment
import com.inikasir.presentation.kasir.KasirFragment

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        
        if (savedInstanceState == null) {
            loadFragment(KasirFragment())
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_kasir -> {
                    loadFragment(KasirFragment())
                    binding.fabAddProduct.visibility = View.GONE
                    true
                }
                R.id.nav_admin -> {
                    loadFragment(AdminFragment())
                    binding.fabAddProduct.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}