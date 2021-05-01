package com.powapp.powason.util

import android.content.Context

class FileHelper {
    companion object {

        //With filename
        fun parseFromRaw(context: Context, fileName: String): String {
            return context.assets.open(fileName).use { it ->
                it.bufferedReader().use {
                    it.readText()
                }
            }
        }

        //With resource id
        fun parseFromRaw(context: Context, resourceId: Int): String {
            return context.resources.openRawResource(resourceId).use { it ->
                it.bufferedReader().use {
                    it.readText()
                }
            }
        }
    }
}