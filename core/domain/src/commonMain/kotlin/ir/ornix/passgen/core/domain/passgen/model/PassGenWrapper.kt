package ir.ornix.passgen.core.domain.passgen.model

import ir.ornix.passgen.core.domain.passgenconfig.model.PreprocessConfig
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.Password.Companion.toPassword
import ir.ornix.passgen.passwordgenerator.kdf.KDFPassGen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

data class PassGenWrapper(val passGenConfig: KDFPassGenConfig, val feed: Flow<PassGenFeed?>) {

    private val passGen: KDFPassGen = passGenConfig.createPassGen()

    override fun equals(other: Any?): Boolean {
        return if (this === other) true
        else if (other !is PassGenWrapper) false
        else passGen == other.passGen
    }

    override fun hashCode() = passGen.hashCode()

    val isCalculating: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val password: Flow<Password?> = feed.map {
        it?.let {
            isCalculating.value = true
            val normalizedInput = it.masterKey +
                    PreprocessConfig.SINGLE_SPACE +
                    passGenConfig.preprocessConfig(it.input)

            val password = passGen.generate(normalizedInput)?.toPassword()

            isCalculating.value = false
            password
        }
    }.flowOn(Dispatchers.Default)
}