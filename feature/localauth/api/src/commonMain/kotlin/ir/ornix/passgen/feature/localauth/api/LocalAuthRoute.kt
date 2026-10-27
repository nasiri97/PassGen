package ir.ornix.passgen.feature.localauth.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class LocalAuthRoute(val instanceId: String = "${Clock.System.now().toEpochMilliseconds()}") :
    NavKey
