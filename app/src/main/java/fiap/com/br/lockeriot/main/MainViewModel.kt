package fiap.com.br.lockeriot.main

import android.arch.lifecycle.*
import fiap.com.br.lockeriot.fingerprint.FingerprintHandler
import fiap.com.br.lockeriot.fingerprint.FingerprintStatus
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class MainViewModel(
        private val fingerprintHandler: FingerprintHandler
): ViewModel(), LifecycleObserver {

    val statusLiveData = MutableLiveData<FingerprintStatus>()
        get() = field.apply {
            if (value == null) {
                value = fingerprintHandler.value
                observerFingerprint()
            }
        }

    private fun observerFingerprint() = with(statusLiveData) {
        fingerprintHandler.observeForever {
            postValue(it)
        }
        observeForever { status ->
            launch(UI) {
                when (status) {
                    is FingerprintStatus.Success -> {
                        delayToIdle()
                        startFingerprint()
                    }
                    is FingerprintStatus.Failure -> {
                        delayToIdle()
                    }
                    is FingerprintStatus.Error -> {
                        delayToIdle(30000)
                        startFingerprint()
                    }
                }
            }
        }
    }

    private suspend fun delayToIdle(time: Long = 2000) {
        delay(time)
        statusLiveData.postValue(FingerprintStatus.Idle())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun startFingerprint() {
        fingerprintHandler.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun stopFingerprint() {
        fingerprintHandler.stop()
    }

    /**
     * Factory for instantiate MainViewModel using ViewModelProvider.of
     */

    class Factory(
            private val handler: FingerprintHandler
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(handler) as T
        }
    }
}