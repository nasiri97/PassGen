package ir.ornix.passgen.core.domain.passgen

import ir.ornix.passgen.core.domain.passgenconfig.model.RandomPassGenConfig
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder

class GenerateRandomPassUseCase() {

    operator fun invoke(passwordLength: Int) =
        RandomPassGenConfig(
            id = 0,
            name = "Random Password Generator",
            passEncoder = StringPassEncoder.Base64PassEncoder,
            passwordLength = passwordLength
        ).createPassGen().generate()
}