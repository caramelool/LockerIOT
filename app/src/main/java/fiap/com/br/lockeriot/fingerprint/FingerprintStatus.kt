package fiap.com.br.lockeriot.fingerprint

sealed class FingerprintStatus {
    class None: FingerprintStatus()
    class Idle : FingerprintStatus()
    class Failure : FingerprintStatus()
    data class Error(val errMsgId: Int) : FingerprintStatus()
    class Success : FingerprintStatus()
}