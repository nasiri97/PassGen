package ir.ornix.passgen.core.ui.util

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.model.Password

val InputHasherSaver: Saver<InputHasher, String> = Saver(
    save = { it.key },
    restore = { InputHasher.fromKey(it) }
)

val PassEncoderSaver: Saver<PassEncoder, String> = Saver(
    save = { it.key },
    restore = { PassEncoder.fromKey(it) }
)

val PasswordSaver = listSaver<Password, Any>(
    save = {
        listOf(
            it.value,
            it.entropyByteSize
        )
    },
    restore = {
        Password(
            value = it[0] as String,
            entropyByteSize = it[1] as Int
        )
    }
)