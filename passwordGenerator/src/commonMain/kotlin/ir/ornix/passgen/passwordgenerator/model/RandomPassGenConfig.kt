package ir.ornix.passgen.passwordgenerator.model

data class RandomPassGenConfig(
    override val passwordLength: Int
) : PassGenConfig {

    override val id = 0
    override val name = "Random Password Generator Config "
    override val typeBrief by lazy {
        "$name - $passwordLength"
    }
}