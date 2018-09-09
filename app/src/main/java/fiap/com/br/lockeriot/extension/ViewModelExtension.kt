package fiap.com.br.lockeriot.extension

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observe: (T?) -> Unit) {
    liveData.observe(this, Observer {
        observe(it)
    })
}

