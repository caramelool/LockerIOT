package fiap.com.br.lockeriot

import android.app.Activity
import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import fiap.com.br.lockeriot.dagger.DaggerApplicationComponent
import fiap.com.br.lockeriot.dagger.module.ApplicationModule
import javax.inject.Inject

//Comment test
class LockerApplication: Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
                .inject(this)
    }

    override fun activityInjector() = dispatchingActivityInjector
}