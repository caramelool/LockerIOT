package fiap.com.br.lockeriot.main

import android.arch.lifecycle.*
import fiap.com.br.lockeriot.fingerprint.FingerprintHandler
import fiap.com.br.lockeriot.fingerprint.FingerprintStatus
import fiap.com.br.lockeriot.repository.LockerStatus
import fiap.com.br.lockeriot.repository.LockerStatusRepository
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class MainViewModel(
        private val fingerprintHandler: FingerprintHandler,
        private val lockerStatusRepository: LockerStatusRepository
): ViewModel(), LifecycleObserver {

    val fingerprintLiveData = MutableLiveData<FingerprintStatus>()
        get() = field.apply {
            if (value == null) {
                value = fingerprintHandler.value
                observerFingerprint()
            }
        }

    val lockerStatusLiveData = MutableLiveData<LockerStatus>()
        get() = field.apply {
            if (value == null) {
                value = lockerStatusRepository.value
                observerLockerStatus()
            }
        }

    private fun observerFingerprint() = with(fingerprintLiveData) {
        fingerprintHandler.observeForever {
            postValue(it)
        }
        observeForever { status ->
            launch(UI) {
                when (status) {
                    is FingerprintStatus.Success -> {
                        notifyLockerRepository()
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

    private fun observerLockerStatus() = with(lockerStatusLiveData) {
        lockerStatusRepository.observeForever {
            postValue(it)
        }
    }

    private fun notifyLockerRepository() {
        lockerStatusRepository.notifty()
    }

    private suspend fun delayToIdle(time: Long = 2000) {
        delay(time)
        fingerprintLiveData.postValue(FingerprintStatus.Idle())
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
            private val fingerprintHandler: FingerprintHandler,
            private val lockerStatusRepository: LockerStatusRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(fingerprintHandler, lockerStatusRepository) as T
        }
    }
}