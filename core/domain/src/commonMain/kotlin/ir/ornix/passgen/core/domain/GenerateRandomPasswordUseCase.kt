package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.domain.core.RandomPassGenConfig
import ir.ornix.passgen.passwordgenerator.model.PassEncoder


class GenerateRandomPasswordUseCase() {

    operator fun invoke(passwordLength: Int) =
        RandomPassGenConfig(
            id = 0,
            name = "Random Password Generator",
            passEncoder = PassEncoder.Base64PassEncoder,
            passwordLength = passwordLength
        ).createPassGen().generate()
}