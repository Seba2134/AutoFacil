package com.project.autofacil.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.autofacil.data.AutoDao

class AutoViewModelFactory(private val autoDao: AutoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AutoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AutoViewModel(autoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
