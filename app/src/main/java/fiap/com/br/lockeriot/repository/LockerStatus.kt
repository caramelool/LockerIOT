package fiap.com.br.lockeriot.repository

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject
import javax.inject.Named

sealed class LockerStatus {
    class Close : LockerStatus()
    class Open : LockerStatus()
}

class LockerStatusRepository @Inject constructor(
    @Named("locker") private val reference: DatabaseReference
) : MutableLiveData<LockerStatus>(), ValueEventListener {

    init {
        value = LockerStatus.Close()
        reference.addValueEventListener(this)
    }

    fun notifty() {
        when (value) {
            is LockerStatus.Open -> {
                reference.setValue(false)
            }
            is LockerStatus.Close -> {
                reference.setValue(true)
            }
        }
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val statusValue = snapshot.getValue(Boolean::class.java) ?: false
        val status = if (statusValue) {
            LockerStatus.Open()
        } else {
            LockerStatus.Close()
        }
        postValue(status)
    }

    override fun onCancelled(error: DatabaseError) {
        //Do nothing
    }
}