package com.adam.lib.safe

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


/**
 * Created By Adam on 2020/8/11
 */
class RSAUtil {
    companion object {
        val RSA = "RSA"
        val ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding"
        // 秘钥默认长度
        val DEFAULT_KEY_SIZE = 2048
        // 当要加密的内容超过bufferSize，则采用partSplit进行分块加密
        val DEFAULT_SPLIT = "#PART#".toByteArray()
        // 当前秘钥支持加密的最大字节数
        val DEFAULT_BUFFERSIZE = DEFAULT_KEY_SIZE / 8 - 11

        // 随机生成RSA密钥对，密钥长度，范围：512～2048
        fun generateRSAKeyPair(keyLength: Int): KeyPair? {
            try {
                val kpg = KeyPairGenerator.getInstance(RSAUtil.RSA)
                kpg.initialize(keyLength)
                return kpg.genKeyPair()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                return null
            }

        }

        /** 获取公钥，打印为48-12613448136942-12272-122-913111503-126115048-12...等等一长串用-拼接的数字  */
        fun getPublicKey(keyPair: KeyPair): ByteArray {
            val rsaPublicKey = keyPair.public as RSAPublicKey
            return rsaPublicKey.getEncoded()
        }

        /** 获取私钥，同上  */
        fun getPrivateKey(keyPair: KeyPair): ByteArray {
            val rsaPrivateKey = keyPair.private as RSAPrivateKey
            return rsaPrivateKey.getEncoded()
        }
        /**
         * 私钥加密
         * @param data       待加密数据
         * @param privateKey 密钥
         * @return byte[] 加密数据
         */
        @Throws(Exception::class)
        fun encryptByPrivateKey(data: ByteArray, privateKey: ByteArray): ByteArray {
            // 得到私钥
            val keySpec = PKCS8EncodedKeySpec(privateKey)
            val kf = KeyFactory.getInstance(RSAUtil.RSA)
            val keyPrivate = kf.generatePrivate(keySpec)
            // 数据加密
            val cipher = Cipher.getInstance(RSAUtil.ECB_PKCS1_PADDING)
            cipher.init(Cipher.ENCRYPT_MODE, keyPrivate)
            return cipher.doFinal(data)
        }

        // 使用私钥进行解密
        @Throws(Exception::class)
        fun decryptByPrivateKey(encrypted: ByteArray, privateKey: ByteArray): ByteArray {
            // 得到私钥
            val keySpec = PKCS8EncodedKeySpec(privateKey)
            val kf = KeyFactory.getInstance(RSAUtil.RSA)
            val keyPrivate = kf.generatePrivate(keySpec)
            // 解密数据
            val cp = Cipher.getInstance(RSAUtil.ECB_PKCS1_PADDING)
            cp.init(Cipher.DECRYPT_MODE, keyPrivate)
            return cp.doFinal(encrypted)
        }

        // 用公钥对字符串进行加密
        @Throws(Exception::class)
        fun encryptByPublicKey(data: ByteArray, publicKey: ByteArray): ByteArray {
            // 得到公钥
            val keySpec = X509EncodedKeySpec(publicKey)
            val kf = KeyFactory.getInstance(RSAUtil.RSA)
            val keyPublic = kf.generatePublic(keySpec)
            // 加密数据
            val cp = Cipher.getInstance(RSAUtil.ECB_PKCS1_PADDING)
            cp.init(Cipher.ENCRYPT_MODE, keyPublic)
            return cp.doFinal(data)
        }

        /**
         * 公钥解密
         * @param data      待解密数据
         * @param publicKey 密钥
         * @return byte[] 解密数据
         */
        @Throws(Exception::class)
        fun decryptByPublicKey(data: ByteArray, publicKey: ByteArray): ByteArray {
            // 得到公钥
            val keySpec = X509EncodedKeySpec(publicKey)
            val kf = KeyFactory.getInstance(RSAUtil.RSA)
            val keyPublic = kf.generatePublic(keySpec)
            // 数据解密
            val cipher = Cipher.getInstance(RSAUtil.ECB_PKCS1_PADDING)
            cipher.init(Cipher.DECRYPT_MODE, keyPublic)
            return cipher.doFinal(data)
        }

        // 以下开始分段解密
        // 使用私钥分段解密
        @Throws(Exception::class)
        fun decryptByPrivateKeyForSpilt(encrypted: ByteArray, privateKey: ByteArray): ByteArray {
            val splitLen = RSAUtil.DEFAULT_SPLIT.size
            if (splitLen <= 0) {
                return decryptByPrivateKey(encrypted, privateKey)
            }
            val dataLen = encrypted.size
            val allBytes = ArrayList<Byte>(1024)
            var latestStartIndex = 0
            run {
                var i = 0
                while (i < dataLen) {
                    val bt = encrypted[i]
                    var isMatchSplit = false
                    if (i == dataLen - 1) {
                        // 到data的最后了
                        val part = ByteArray(dataLen - latestStartIndex)
                        System.arraycopy(encrypted, latestStartIndex, part, 0, part.size)
                        val decryptPart = decryptByPrivateKey(part, privateKey)
                        for (b in decryptPart) {
                            allBytes.add(b)
                        }
                        latestStartIndex = i + splitLen
                        i = latestStartIndex - 1
                    } else if (bt == RSAUtil.DEFAULT_SPLIT[0]) {
                        // 这个是以split[0]开头
                        if (splitLen > 1) {
                            if (i + splitLen < dataLen) {
                                // 没有超出data的范围
                                for (j in 1 until splitLen) {
                                    if (RSAUtil.DEFAULT_SPLIT[j] != encrypted[i + j]) {
                                        break
                                    }
                                    if (j == splitLen - 1) {
                                        // 验证到split的最后一位，都没有break，则表明已经确认是split段
                                        isMatchSplit = true
                                    }
                                }
                            }
                        } else {
                            // split只有一位，则已经匹配了
                            isMatchSplit = true
                        }
                    }
                    if (isMatchSplit) {
                        val part = ByteArray(i - latestStartIndex)
                        System.arraycopy(encrypted, latestStartIndex, part, 0, part.size)
                        val decryptPart = decryptByPrivateKey(part, privateKey)
                        for (b in decryptPart) {
                            allBytes.add(b)
                        }
                        latestStartIndex = i + splitLen
                        i = latestStartIndex - 1
                    }
                    i++
                }
            }
            val bytes = ByteArray(allBytes.size)
            run {
                var i = 0
                for (b in allBytes) {
                    bytes[i++] = b
                }
            }
            return bytes
        }

        // 私钥分段加密
        @Throws(Exception::class)
        fun encryptByPrivateKeyForSpilt(data: ByteArray, privateKey: ByteArray): ByteArray {
            val dataLen = data.size
            if (dataLen <= RSAUtil.DEFAULT_BUFFERSIZE) {
                return encryptByPrivateKey(data, privateKey)
            }
            val allBytes = ArrayList<Byte>(2048)
            var bufIndex = 0
            var subDataLoop = 0
            var buf: ByteArray? = ByteArray(RSAUtil.DEFAULT_BUFFERSIZE)
            for (i in 0 until dataLen) {
                buf!![bufIndex] = data[i]
                if (++bufIndex == RSAUtil.DEFAULT_BUFFERSIZE || i == dataLen - 1) {
                    subDataLoop++
                    if (subDataLoop != 1) {
                        for (b in RSAUtil.DEFAULT_SPLIT) {
                            allBytes.add(b)
                        }
                    }
                    val encryptBytes = encryptByPrivateKey(buf, privateKey)
                    for (b in encryptBytes) {
                        allBytes.add(b)
                    }
                    bufIndex = 0
                    if (i == dataLen - 1) {
                        buf = null
                    } else {
                        buf = ByteArray(Math.min(RSAUtil.DEFAULT_BUFFERSIZE, dataLen - i - 1))
                    }
                }
            }
            val bytes = ByteArray(allBytes.size)
            run {
                var i = 0
                for (b in allBytes) {
                    bytes[i++] = b
                }
            }
            return bytes
        }

        // 用公钥对字符串进行分段加密
        @Throws(Exception::class)
        fun encryptByPublicKeyForSpilt(data: ByteArray, publicKey: ByteArray): ByteArray {
            val dataLen = data.size
            if (dataLen <= RSAUtil.DEFAULT_BUFFERSIZE) {
                return encryptByPublicKey(data, publicKey)
            }
            val allBytes = ArrayList<Byte>(2048)
            var bufIndex = 0
            var subDataLoop = 0
            var buf: ByteArray? = ByteArray(RSAUtil.DEFAULT_BUFFERSIZE)
            for (i in 0 until dataLen) {
                buf!![bufIndex] = data[i]
                if (++bufIndex == RSAUtil.DEFAULT_BUFFERSIZE || i == dataLen - 1) {
                    subDataLoop++
                    if (subDataLoop != 1) {
                        for (b in RSAUtil.DEFAULT_SPLIT) {
                            allBytes.add(b)
                        }
                    }
                    val encryptBytes = encryptByPublicKey(buf, publicKey)
                    for (b in encryptBytes) {
                        allBytes.add(b)
                    }
                    bufIndex = 0
                    if (i == dataLen - 1) {
                        buf = null
                    } else {
                        buf = ByteArray(Math.min(RSAUtil.DEFAULT_BUFFERSIZE, dataLen - i - 1))
                    }
                }
            }
            val bytes = ByteArray(allBytes.size)
            run {
                var i = 0
                for (b in allBytes) {
                    bytes[i++] = b
                }
            }
            return bytes
        }

        // 公钥分段解密
        @Throws(Exception::class)
        fun decryptByPublicKeyForSpilt(encrypted: ByteArray, publicKey: ByteArray): ByteArray {
            val splitLen = RSAUtil.DEFAULT_SPLIT.size
            if (splitLen <= 0) {
                return decryptByPublicKey(encrypted, publicKey)
            }
            val dataLen = encrypted.size
            val allBytes = ArrayList<Byte>(1024)
            var latestStartIndex = 0
            run {
                var i = 0
                while (i < dataLen) {
                    val bt = encrypted[i]
                    var isMatchSplit = false
                    if (i == dataLen - 1) {
                        // 到data的最后了
                        val part = ByteArray(dataLen - latestStartIndex)
                        System.arraycopy(encrypted, latestStartIndex, part, 0, part.size)
                        val decryptPart = decryptByPublicKey(part, publicKey)
                        for (b in decryptPart) {
                            allBytes.add(b)
                        }
                        latestStartIndex = i + splitLen
                        i = latestStartIndex - 1
                    } else if (bt == RSAUtil.DEFAULT_SPLIT[0]) {
                        // 这个是以split[0]开头
                        if (splitLen > 1) {
                            if (i + splitLen < dataLen) {
                                // 没有超出data的范围
                                for (j in 1 until splitLen) {
                                    if (RSAUtil.DEFAULT_SPLIT[j] != encrypted[i + j]) {
                                        break
                                    }
                                    if (j == splitLen - 1) {
                                        // 验证到split的最后一位，都没有break，则表明已经确认是split段
                                        isMatchSplit = true
                                    }
                                }
                            }
                        } else {
                            // split只有一位，则已经匹配了
                            isMatchSplit = true
                        }
                    }
                    if (isMatchSplit) {
                        val part = ByteArray(i - latestStartIndex)
                        System.arraycopy(encrypted, latestStartIndex, part, 0, part.size)
                        val decryptPart = decryptByPublicKey(part, publicKey)
                        for (b in decryptPart) {
                            allBytes.add(b)
                        }
                        latestStartIndex = i + splitLen
                        i = latestStartIndex - 1
                    }
                    i++
                }
            }
            val bytes = ByteArray(allBytes.size)
            run {
                var i = 0
                for (b in allBytes) {
                    bytes[i++] = b
                }
            }
            return bytes
        }
    }
}