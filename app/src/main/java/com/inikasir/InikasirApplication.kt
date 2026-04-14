package com.inikasir

import android.app.Application

class InikasirApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: InikasirApplication
            private set
    }
}