package ir.ornix.passgen.composeapp.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import ir.ornix.passgen.feature.about.api.AboutRoute
import ir.ornix.passgen.feature.config.api.AddConfigRoute
import ir.ornix.passgen.feature.home.api.HomeRoute
import ir.ornix.passgen.feature.localauth.api.LocalAuthRoute
import ir.ornix.passgen.feature.random.api.RandomRoute
import ir.ornix.passgen.feature.savedpasswords.api.SavedPasswordsRoute
import ir.ornix.passgen.feature.settings.api.SettingsRoute
import ir.ornix.passgen.feature.unlock.api.UnlockRoute
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val appNavConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(HomeRoute::class, HomeRoute.serializer())
            subclass(RandomRoute::class, RandomRoute.serializer())
            subclass(SavedPasswordsRoute::class, SavedPasswordsRoute.serializer())
            subclass(SettingsRoute::class, SettingsRoute.serializer())
            subclass(AboutRoute::class, AboutRoute.serializer())
            subclass(LocalAuthRoute::class, LocalAuthRoute.serializer())
            subclass(UnlockRoute::class, UnlockRoute.serializer())
            subclass(AddConfigRoute::class, AddConfigRoute.serializer())
        }
    }
}
