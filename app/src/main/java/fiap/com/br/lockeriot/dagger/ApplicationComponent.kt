package fiap.com.br.lockeriot.dagger

import dagger.Component
import fiap.com.br.lockeriot.LockerApplication
import fiap.com.br.lockeriot.dagger.module.*
import fiap.com.br.lockeriot.main.MainContributeModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class,
    RetrofitModule::class,
    AndroidModule::class,
    ViewModelModule::class,
    FirebaseModule::class,
    MainContributeModule::class
])
interface ApplicationComponent {
    fun inject(application: LockerApplication)
}