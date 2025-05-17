package com.blackcube.utils.encryption

import io.github.cdimascio.dotenv.dotenv
import java.security.PrivateKey
import java.util.Base64
import javax.crypto.Cipher

/**
 * Утилита для расшифровки данных, зашифрованных на фронте публичным ключом RSA
 * Параметры алгоритма шифрования конфигурируются через ENV RSA_CIPHER_TRANSFORMATION
 */
object RsaUtil {
    private val env = dotenv()
    private val transformation: String =
        env["RSA_CIPHER_TRANSFORMATION"] ?: "RSA/ECB/PKCS1Padding"

    /**
     * Расшифровать Base64-строку, используя приватный ключ
     * @param encryptedBase64 строка в Base64
     * @param privateKey приватный ключ
     * @return расшифрованные байты
     */
    fun decrypt(encryptedBase64: String, privateKey: PrivateKey): ByteArray {
        val encryptedBytes = Base64.getDecoder().decode(encryptedBase64)
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(encryptedBytes)
    }
}