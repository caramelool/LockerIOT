package fiap.com.br.lockeriot.fingerprint

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.arch.lifecycle.MutableLiveData
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal
import android.util.Log
import javax.inject.Inject

class FingerprintHandler @Inject constructor (
    private val fingerprintManager: FingerprintManagerCompat,
    private val keyguardManager: KeyguardManager
): MutableLiveData<FingerprintStatus>() {

    init {
        value = FingerprintStatus.None()
    }

    private var cancellationSignal: CancellationSignal? = null
        get() {
            if (field == null) {
                field = CancellationSignal()
            }
            return field
        }

    @TargetApi(Build.VERSION_CODES.M)
    fun start() {
        Log.d("authenticationCallback", "start")
        if (keyguardManager.isKeyguardSecure
                && fingerprintManager.isHardwareDetected
                && fingerprintManager.hasEnrolledFingerprints()) {
            val keyStore = FingerprintSecurityUtil.provideKeyStore()
            val cipher = FingerprintSecurityUtil.provideCipher(keyStore)
            val cryptoObject = FingerprintManagerCompat.CryptoObject(cipher)
            fingerprintManager.authenticate(
                    cryptoObject,
                    0,
                    cancellationSignal,
                    authenticationCallback,
                    null)
            postValue(FingerprintStatus.Idle())
        }
    }

    fun stop() {
        cancellationSignal?.cancel()
        cancellationSignal = null
    }

    private val authenticationCallback = object : FingerprintManagerCompat.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            Log.d("authenticationCallback", "succeeded")
            postValue(FingerprintStatus.Success())
        }

        override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
            super.onAuthenticationError(errMsgId, errString)
            Log.d("authenticationCallback", "errMsgId: $errMsgId - errString: $errString")
            when (errMsgId) {
                FingerprintManager.FINGERPRINT_ERROR_LOCKOUT -> {
                    postValue(FingerprintStatus.Error(errMsgId))
                }
            }
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Log.d("authenticationCallback", "failed")
            postValue(FingerprintStatus.Failure())
        }
    }

}