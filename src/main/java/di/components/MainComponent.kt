package di.components

import app.MainActivity
import app.fragments.ChatListFragment
import app.fragments.ChatRoomFragment
import dagger.Component
import dagger.Subcomponent
import di.annotaitons.PerActivity
import di.modules.InteractorsModule
import di.modules.PresentersModule
import di.modules.ServicesModule

/**
 * @author kitttn
 */

@PerActivity
@Subcomponent(modules = arrayOf(PresentersModule::class, InteractorsModule::class))
interface MainComponent {
    fun inject(fragment: ChatListFragment)
    fun inject(fragment: ChatRoomFragment)
    fun inject(activity: MainActivity)
}