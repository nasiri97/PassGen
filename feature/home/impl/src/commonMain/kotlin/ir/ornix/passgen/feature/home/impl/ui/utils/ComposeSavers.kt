package ir.ornix.passgen.feature.home.impl.ui.utils


import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.model.Password

internal val InputHasherSaver: Saver<InputHasher, String> = Saver(
    save = { it.key },
    restore = { InputHasher.fromKey(it) }
)


internal val PassEncoderSaver: Saver<PassEncoder, String> = Saver(
    save = { it.key },
    restore = { PassEncoder.fromKey(it) }
)


internal val PasswordSaver = listSaver<Password, Any>(
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