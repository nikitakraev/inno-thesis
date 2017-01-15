package di.modules

import app.interactors.ChatListInteractor
import app.interactors.MainInteractor
import app.presenters.ChatListPresenter
import app.presenters.ChatRoomPresenter
import app.presenters.MainNavigator
import dagger.Module
import dagger.Provides
import di.annotaitons.PerActivity

/**
 * @author kitttn
 */

@Module
class PresentersModule {
    @Provides
    @PerActivity
    fun provideChatListPresenter(interactor: ChatListInteractor, navi: MainNavigator): ChatListPresenter {
        return ChatListPresenter(interactor, navi)
    }

    @Provides
    @PerActivity
    fun provideMainNavigator(interactor: MainInteractor): MainNavigator {
        return MainNavigator(interactor)
    }

    @Provides
    @PerActivity
    fun provideChatRoomPresenter(interactor: MainInteractor): ChatRoomPresenter = ChatRoomPresenter(interactor)
}
