package ir.ornix.passgen.feature.home.impl.model

import ir.ornix.passgen.passwordgenerator.KDFPassGen
import ir.ornix.passgen.passwordgenerator.model.PassGenFeed
import ir.ornix.passgen.passwordgenerator.model.Password
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class PassGenWrapper(val passGen: KDFPassGen) {

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
                    _password.value = passGen.generate(feed = it)
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
