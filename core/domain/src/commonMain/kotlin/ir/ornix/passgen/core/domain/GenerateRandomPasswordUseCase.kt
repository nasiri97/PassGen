package ir.ornix.passgen.core.domain

import ir.ornix.passgen.passwordgenerator.RandomPassGen
import ir.ornix.passgen.passwordgenerator.model.RandomPassGenConfig

class GenerateRandomPasswordUseCase() {

    operator fun invoke(passwordLength: Int) =
        RandomPassGen(RandomPassGenConfig(passwordLength = passwordLength)).generate()
}