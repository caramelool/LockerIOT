package fiap.com.br.lockeriot.fingerprint

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object FingerprintSecurityUtil {

    private const val KEY_STORE_ALIAS = "AaAa-bBBb-CcCc-dDDd"

    @RequiresApi(Build.VERSION_CODES.M)
    fun provideKeyStore() : KeyStore {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")

        val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore")

        keyStore.load(null)

        val gen = KeyGenParameterSpec.Builder(KEY_STORE_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()
        keyGenerator.init(gen)
        keyGenerator.generateKey()

        return keyStore
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun provideCipher(keyStore: KeyStore): Cipher {
        val transform = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
        val cipher = Cipher.getInstance(transform)

        keyStore.load(null)

        val key = keyStore.getKey(KEY_STORE_ALIAS,null) as SecretKey
        cipher.init(Cipher.ENCRYPT_MODE, key)

        return cipher
    }

}