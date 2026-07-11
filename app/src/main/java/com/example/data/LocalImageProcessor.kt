package com.example.data

import android.graphics.Bitmap
import android.graphics.Color

object LocalImageProcessor {

    /**
     * Analyzes a Bitmap to compute a 256-bin brightness histogram
     * and applies a threshold-based background brightening filter.
     * This prepares the medicine image for a clean, consistent bright/white background look.
     */
    fun processMedicineImage(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val totalPixels = width * height
        
        // Create a mutable copy of the bitmap
        val mutableBitmap = src.copy(Bitmap.Config.ARGB_8888, true)
        val pixels = IntArray(totalPixels)
        mutableBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        
        // 1. Compute brightness histogram
        val histogram = IntArray(256)
        for (i in 0 until totalPixels) {
            val color = pixels[i]
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            // Luma formula (Y)
            val y = (0.299f * r + 0.587f * g + 0.114f * b).toInt().coerceIn(0, 255)
            histogram[y]++
        }
        
        // 2. Find background threshold (e.g., the top 20% brightest pixels represent the background)
        // We start from 255 and sum down until we hit 20% of pixels.
        val targetCount = (totalPixels * 0.20).toInt()
        var sum = 0
        var dynamicThreshold = 200 // default fallback
        for (y in 255 downTo 0) {
            sum += histogram[y]
            if (sum >= targetCount) {
                dynamicThreshold = y
                break
            }
        }
        
        // Clamp threshold to a safe range (160 to 225) to avoid over-whitening or no-whitening
        val threshold = dynamicThreshold.coerceIn(160, 225)
        
        // 3. Apply smooth threshold-based filter to brighten the background
        for (i in 0 until totalPixels) {
            val color = pixels[i]
            val a = Color.alpha(color)
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            
            val y = (0.299f * r + 0.587f * g + 0.114f * b).toInt().coerceIn(0, 255)
            
            if (y >= threshold) {
                // Smooth transition factor from threshold to 255
                val factor = (y - threshold).toFloat() / (255f - threshold)
                val newR = (r + (255 - r) * factor).toInt().coerceIn(0, 255)
                val newG = (g + (255 - g) * factor).toInt().coerceIn(0, 255)
                val newB = (b + (255 - b) * factor).toInt().coerceIn(0, 255)
                pixels[i] = Color.argb(a, newR, newG, newB)
            }
        }
        
        mutableBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return mutableBitmap
    }
}
