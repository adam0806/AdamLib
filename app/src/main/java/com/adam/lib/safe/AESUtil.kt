package com.adam.lib.safe

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * Created By Adam on 2020/8/10
 */
class AESUtil {
    var key = "abcdefghijklmnop"
    // 初始化向量参数，AES 为16bytes. DES 为8bytes， 16*8=128
    var initVector = "0000000000000000"
    lateinit var iv: IvParameterSpec
    lateinit var skeySpec: SecretKeySpec
    lateinit var cipher: Cipher

    companion object{
        var instance = Holder.instance
    }
    private object Holder{
        var instance = AESUtil()
    }

    constructor(){
        try {
            iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
            skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
            // 这是CBC模式
            // cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            // 默认就是ECB模式
            cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

    }

    fun encrypt(value: String): String? {
        try {
            // CBC模式需要传入向量，ECB模式不需要
            // cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
            val encrypted = cipher.doFinal(value.toByteArray())
            return Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun decrypt(encrypted: String): String? {
        try {
            // CBC模式需要传入向量，ECB模式不需要
            // cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec)
            val original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT))
            return String(original)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }
}