package com.triple7.healthshield254

import android.app.Application
import com.cloudinary.android.MediaManager
import java.util.HashMap

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // --- Initialize Cloudinary MediaManager ---
        // This is done once when your application starts.
        val config = HashMap<String, String>()
        config["cloud_name"] = "ds98xoivg"
        // The SDK will automatically use unsigned uploads if you don't provide an api_key and api_secret.
        // Make sure you have created an "unsigned" upload preset on your Cloudinary dashboard.
        MediaManager.init(this, config)
    }
}
