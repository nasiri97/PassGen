package ir.ornix.passgen.core.domain


import ir.ornix.passgen.core.domain.core.KDFPassGenConfig
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.Password.Companion.toPassword
import ir.ornix.passgen.passwordgenerator.kdf.KDFPassGen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class PassGenWrapper(val passGenConfig: KDFPassGenConfig) {

    private val passGen: KDFPassGen = passGenConfig.createPassGen()

    override fun equals(other: Any?): Boolean {
        return if (this === other) true
        else if (other !is PassGenWrapper) false
        else passGen == other.passGen
    }

    override fun hashCode() = passGen.hashCode()

    private var lastFeed: PassGenFeed? = null
    private var lastCalculatedFeed: PassGenFeed? = null

    private val _password = MutableStateFlow<Password?>(null)
    val password: StateFlow<Password?> = _password.asStateFlow()

    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating

    private val calculationMutex = Mutex()
    private val updatingMutex = Mutex()

    suspend fun generate() {
        calculationMutex.withLock {
            lastFeed?.let {
                if (lastCalculatedFeed != it) {
                    _password.value = null
                    lastCalculatedFeed = it

                    val normalizedInput = it.masterKey +
                            PreprocessConfig.SINGLE_SPACE +
                            passGenConfig.preprocessConfig(it.input)

                    _password.value = passGen.generate(normalizedInput)?.toPassword()
                }
            }

            updateIsCalculating()
        }
    }

    private suspend fun updateIsCalculating() {
        updatingMutex.withLock {
            _isCalculating.value =
                if (lastFeed != lastCalculatedFeed) true
                else if (password.value == null) true
                else false
        }
    }

    suspend fun updateFeed(feed: PassGenFeed?) {
        lastFeed = feed
        updateIsCalculating()
    }
}
