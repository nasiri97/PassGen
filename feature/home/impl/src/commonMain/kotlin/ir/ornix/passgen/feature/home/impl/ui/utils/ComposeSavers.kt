package ir.ornix.passgen.feature.home.impl.ui.utils


import androidx.compose.runtime.saveable.Saver
import ir.ornix.passgen.passwordgenerator.model.EncoderType
import ir.ornix.passgen.passwordgenerator.model.HashingType
import ir.ornix.passgen.passwordgenerator.model.Password
import ir.ornix.passgen.passwordgenerator.model.Password.Companion.toPassword

internal val HashingTypeSaver: Saver<HashingType, String> = Saver(
    save = { it.key },
    restore = { HashingType.fromKey(it) }
)


internal val EncoderTypeSaver: Saver<EncoderType, String> = Saver(
    save = { it.key },
    restore = { EncoderType.fromKey(it) }
)


internal val PasswordSaver: Saver<Password, String> = Saver(
    save = { it.value },
    restore = { it.toPassword() }
)