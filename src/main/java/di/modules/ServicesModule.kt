package di.modules

import app.sdk.SomeService1
import app.sdk.SomeService2
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author kitttn
 */

@Module
class ServicesModule {
    @Provides @Singleton
    fun provideS1() = SomeService1()

    @Provides @Singleton
    fun provideS2() = SomeService2()
}