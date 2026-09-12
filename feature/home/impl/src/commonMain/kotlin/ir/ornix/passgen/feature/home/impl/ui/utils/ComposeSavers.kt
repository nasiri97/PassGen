package ir.ornix.passgen.feature.home.impl.ui.utils


import androidx.compose.runtime.saveable.Saver
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.Password.Companion.toPassword
import ir.ornix.passgen.passwordgenerator.model.InputHasher
import ir.ornix.passgen.passwordgenerator.model.PassEncoder

internal val InputHasherSaver: Saver<InputHasher, String> = Saver(
    save = { it.key },
    restore = { InputHasher.fromKey(it) }
)


internal val PassEncoderSaver: Saver<PassEncoder, String> = Saver(
    save = { it.key },
    restore = { PassEncoder.fromKey(it) }
)


internal val PasswordSaver: Saver<Password, String> = Saver(
    save = { it.value },
    restore = { it.toPassword() }
)