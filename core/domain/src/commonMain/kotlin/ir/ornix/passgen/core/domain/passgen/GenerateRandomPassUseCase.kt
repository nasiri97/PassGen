package ir.ornix.passgen.core.domain.passgen

import ir.ornix.passgen.core.domain.passgenconfig.model.RandomPassGenConfig

class GenerateRandomPassUseCase() {

    suspend operator fun invoke(config: RandomPassGenConfig) =
        config.createPassGen().generate()
}