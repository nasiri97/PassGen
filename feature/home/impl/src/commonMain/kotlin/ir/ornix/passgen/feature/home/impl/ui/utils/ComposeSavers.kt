package ir.ornix.passgen.feature.home.impl.ui.utils


import androidx.compose.runtime.saveable.Saver
import ir.ornix.passgen.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.passwordgenerator.model.HashingType
import ir.ornix.passgen.passwordgenerator.model.Password
import ir.ornix.passgen.passwordgenerator.model.Password.Companion.toPassword

internal val HashingTypeSaver: Saver<HashingType, String> = Saver(
    save = { it.key },
    restore = { HashingType.fromKey(it) }
)


internal val PassEncoderSaver: Saver<PassEncoder, String> = Saver(
    save = { it.key },
    restore = { PassEncoder.fromKey(it) }
)


internal val PasswordSaver: Saver<Password, String> = Saver(
    save = { it.value },
    restore = { it.toPassword() }
)