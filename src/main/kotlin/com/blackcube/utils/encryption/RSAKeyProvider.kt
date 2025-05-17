package com.blackcube.utils.encryption

import io.github.cdimascio.dotenv.dotenv
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

/**
 * Провайдер RSA-ключей: генерирует пару при первом старте и сохраняет в файловой системе,
 * далее загружает из файлов, возвращая PublicKey и PrivateKey.
 */
object RSAKeyProvider {
    private val env = dotenv()
    private val keyDir = env["RSA_KEY_DIR"] ?: "./keys"
    private val publicKeyFile = Paths.get(keyDir, "public.key")
    private val privateKeyFile = Paths.get(keyDir, "private.key")

    lateinit var publicKey: PublicKey
        private set
    lateinit var privateKey: PrivateKey
        private set

    fun init() {
        Files.createDirectories(Paths.get(keyDir))
        if (Files.exists(publicKeyFile) && Files.exists(privateKeyFile)) {
            publicKey = loadPublicKey(Files.readAllBytes(publicKeyFile))
            privateKey = loadPrivateKey(Files.readAllBytes(privateKeyFile))
        } else {
            val keyPair = generateKeyPair()
            publicKey = keyPair.public
            privateKey = keyPair.private
            Files.write(publicKeyFile, Base64.getEncoder().encode(publicKey.encoded))
            Files.write(privateKeyFile, Base64.getEncoder().encode(privateKey.encoded))
        }
    }

    /**
     * Генерация пары RSA-ключей (2048 бит). Выполняется единожды при отсутствии ключей.
     * Для высокой нагрузки рекомендуется хранение в KMS/HSM.
     */
    private fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2048)
        return keyGen.generateKeyPair()
    }

    private fun loadPublicKey(bytes: ByteArray): PublicKey {
        val decoded = Base64.getDecoder().decode(bytes)
        val spec = X509EncodedKeySpec(decoded)
        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }

    private fun loadPrivateKey(bytes: ByteArray): PrivateKey {
        val decoded = Base64.getDecoder().decode(bytes)
        val spec = PKCS8EncodedKeySpec(decoded)
        return KeyFactory.getInstance("RSA").generatePrivate(spec)
    }
}