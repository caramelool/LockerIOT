package fiap.com.br.lockeriot.dagger.module

import android.app.KeyguardManager
import android.content.Context
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AndroidModule {

    @Provides
    @Singleton
    fun provideFingerprintManager(context: Context)
            = FingerprintManagerCompat.from(context)

    @Provides
    @Singleton
    fun provideKeyguardManager(context: Context)
            = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
}