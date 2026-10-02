package ir.ornix.passgen.core.ui.util

import androidx.compose.runtime.saveable.Saver
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder

val InputHasherSaver: Saver<InputHasher, String> = Saver(
    save = { it.key },
    restore = { InputHasher.fromKey(it) }
)

val PassEncoderSaver: Saver<PassEncoder, String> = Saver(
    save = { it.key },
    restore = { PassEncoder.fromKey(it) }
)