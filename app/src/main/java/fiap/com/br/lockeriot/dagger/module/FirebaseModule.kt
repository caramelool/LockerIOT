package fiap.com.br.lockeriot.dagger.module

import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class FirebaseModule {

    @Provides
    fun provideDatabase() = FirebaseDatabase.getInstance()

    @Provides
    @Named("locker")
    fun provideLockerReference(database: FirebaseDatabase)
            = database.getReference("locker")

    @Provides
    @Named("failure")
    fun provideFailureReference(database: FirebaseDatabase)
            = database.getReference("failure")
}