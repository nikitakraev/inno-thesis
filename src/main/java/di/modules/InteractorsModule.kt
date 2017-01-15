package di.modules

import app.interactors.ChatListInteractor
import app.interactors.MainInteractor
import app.sdk.SomeService1
import app.sdk.SomeService2
import dagger.Module
import dagger.Provides
import di.annotaitons.PerActivity

/**
 * @author kitttn
 */

@Module
class InteractorsModule {
    @Provides
    @PerActivity
    fun provideMainInteractor(service: SomeService1, s2: SomeService2): MainInteractor {
        return MainInteractor(service, s2)
    }

    @Provides
    @PerActivity
    fun provideChatListInteractor(s1: SomeService1): ChatListInteractor {
        return ChatListInteractor(s1)
    }
}
