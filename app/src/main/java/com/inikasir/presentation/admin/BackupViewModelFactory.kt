package com.inikasir.presentation.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inikasir.InikasirApplication

class BackupViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BackupViewModel::class.java)) {
            val database = InikasirApplication.instance.database
            
            @Suppress("UNCHECKED_CAST")
            return BackupViewModel(context, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}