package com.inikasir

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inikasir.presentation.kasir.KasirFragment

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, KasirFragment())
                .commit()
        }
    }
}