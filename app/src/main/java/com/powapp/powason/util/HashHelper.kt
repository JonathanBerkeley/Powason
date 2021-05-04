package com.powapp.powason.util

import java.security.MessageDigest

//Modified from code by Sam Clarke
//https://www.samclarke.com/kotlin-hash-strings/

object HashHelper {

    //Converts string into SHA-1 hash
    fun sha1(input: String): String {
        if (input == "")
            return ""

        val hex = "0123456789ABCDEF"
        val rawBytes = MessageDigest
            .getInstance("SHA-1")
            .digest(input.toByteArray())
        val output = StringBuilder(rawBytes.size * 2)

        for (byte in rawBytes) {
            val i = byte.toInt()
            output.append(hex[i shr 4 and 0x0f])
            output.append(hex[i and 0x0f])
        }
        return output.toString()
    }
}