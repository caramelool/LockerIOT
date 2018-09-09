package fiap.com.br.lockeriot.main

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import fiap.com.br.lockeriot.fingerprint.FingerprintHandler
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class MainScope

@Module
class MainActivityProvides {
    @Provides
    @MainScope
    fun provideViewModel(
            mainActivity: MainActivity,
            handler: FingerprintHandler
    ): MainViewModel {
        val factory = MainViewModel.Factory(handler)
        return ViewModelProviders.of(mainActivity, factory)[MainViewModel::class.java]
    }
}

@Module
abstract class MainContributeModule {
    @MainScope
    @ContributesAndroidInjector(modules = [MainActivityProvides::class])
    abstract fun contributeMainActivity(): MainActivity
}