package di.components

import dagger.Component
import di.modules.ServicesModule
import javax.inject.Singleton

/**
 * This component is used across the whole application
 * @author kitttn
 */

@Singleton
@Component(modules = arrayOf(ServicesModule::class))
interface AppComponent {
    fun add(): MainComponent
}