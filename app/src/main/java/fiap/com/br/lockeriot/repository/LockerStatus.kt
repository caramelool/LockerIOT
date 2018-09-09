package fiap.com.br.lockeriot.repository

import android.arch.lifecycle.MutableLiveData
import javax.inject.Inject

sealed class LockerStatus {
    class Close : LockerStatus()
    class Open : LockerStatus()
}

class LockerStatusRepository @Inject constructor(

) : MutableLiveData<LockerStatus>() {

    init {
        value = LockerStatus.Close()
    }

    fun notifty() {
        when (value) {
            is LockerStatus.Open -> {
                postValue(LockerStatus.Close())
            }
            is LockerStatus.Close -> {
                postValue(LockerStatus.Open())
            }
        }
    }

}