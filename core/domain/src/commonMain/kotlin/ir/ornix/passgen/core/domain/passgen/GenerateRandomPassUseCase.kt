package ir.ornix.passgen.core.domain.passgen

import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.Password.Companion.toPassword
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig

class GenerateRandomPassUseCase() {

    suspend operator fun invoke(config: RandomPassGenConfig): Password =
        config.createPassGen().generate().toPassword(entropyByteSize = config.entropyByteSize)
}