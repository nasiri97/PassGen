package ir.ornix.passgen.core.domain

import ir.ornix.passgen.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.passwordgenerator.random.RandomPassGen
import ir.ornix.passgen.passwordgenerator.random.RandomPassGenConfig

class GenerateRandomPasswordUseCase() {

    operator fun invoke(passwordLength: Int) =
        RandomPassGen(
            RandomPassGenConfig(
                id = 0,
                name = "Random Pass Gen",
                passEncoder = PassEncoder.Base64PassEncoder,
                passwordLength = passwordLength
            )
        ).generate()
}