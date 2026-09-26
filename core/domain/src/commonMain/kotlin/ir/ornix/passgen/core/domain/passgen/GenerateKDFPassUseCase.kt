package ir.ornix.passgen.core.domain.passgen

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.Password.Companion.toPassword

class GenerateKDFPassUseCase(private val hmacSigner: HmacSigner) {

    suspend operator fun invoke(config: KDFPassGenConfig, input: String): Password? {
        val passGen = config.createPassGen()
        val processedInput = config.preprocessConfig(input)

        val passwordSeed = hmacSigner.sign(mkdId = "${config.id}", processedInput)
        val password = passGen.generate(passwordSeed)?.toPassword()
        passwordSeed.fill(0)

        return password
    }
}
