package com.adam.lib.safe

import android.util.Base64
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * Created By Adam on 2020/8/10
 */
class MD5SHABase64 {
    companion object{
        fun toMd5(str: String): String? {
            val algorithm: MessageDigest
            try {
                algorithm = MessageDigest.getInstance("MD5")
                algorithm.reset()
                algorithm.update(str.toByteArray())
                return toHexString(algorithm.digest(), "")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

            return null
        }

        fun toSHA(str: String): String? {
            val algorithm: MessageDigest
            try {
                algorithm = MessageDigest.getInstance("SHA")
                algorithm.reset()
                algorithm.update(str.toByteArray())
                return toHexString(algorithm.digest(), "")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

            return null
        }

        fun toHexString(bytes: ByteArray, separtor: String): String {
            val hexString = StringBuilder()
            for (b in bytes) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) {
                    hexString.append("0")
                }
                hexString.append(hex).append(separtor)
            }
            return hexString.toString()
        }

        fun base64Encode(str: String): String {
            val encodeBytes = Base64.encodeToString(str.toByteArray(), Base64.DEFAULT);
            return encodeBytes
        }

        fun base64Decode(str: String): String {
            val decodeBytes = Base64.decode(str.toByteArray(), Base64.DEFAULT)
            return String(decodeBytes)
        }
    }


}