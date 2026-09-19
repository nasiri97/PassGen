package ir.ornix.passgen.core.domain.passgen

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.Password.Companion.toPassword
import ir.ornix.passgen.passwordgenerator.kdf.KDFPassGen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

data class PassGenWrapper(
    val passGenConfig: KDFPassGenConfig,
    val hmacSigner: HmacSigner,
    val input: Flow<String>
) {

    private val passGen: KDFPassGen = passGenConfig.createPassGen()

    override fun equals(other: Any?): Boolean {
        return if (this === other) true
        else if (other !is PassGenWrapper) false
        else passGen == other.passGen
    }

    override fun hashCode() = passGen.hashCode()

    val isCalculating: StateFlow<Boolean>
        field = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val password: Flow<Password?> = input.mapLatest { input ->
        isCalculating.value = true
        val processedInput = passGenConfig.preprocessConfig(input)

        val result = hmacSigner.sign(keyId = "${passGenConfig.id}", processedInput)
        val password = passGen.generate(result)?.toPassword()
        result.fill(0)

        currentCoroutineContext().ensureActive()
        isCalculating.value = false
        password
    }.flowOn(Dispatchers.Default)
}