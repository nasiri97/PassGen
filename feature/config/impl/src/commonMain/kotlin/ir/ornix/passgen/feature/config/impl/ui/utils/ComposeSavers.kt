package ir.ornix.passgen.feature.config.impl.ui.utils

import androidx.compose.runtime.saveable.Saver
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder

internal val InputHasherSaver: Saver<InputHasher, String> = Saver(
    save = { it.key },
    restore = { InputHasher.fromKey(it) }
)

internal val PassEncoderSaver: Saver<PassEncoder, String> = Saver(
    save = { it.key },
    restore = { PassEncoder.fromKey(it) }
)
