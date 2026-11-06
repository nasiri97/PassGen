package ir.ornix.passgen.core.domain.passgenconfig.kdf

import ir.ornix.passgen.core.domain.KdfPassGenConfigRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.uuid.Uuid

private val mutex = Mutex()

class GenerateMasterKeyDigestIdUseCase(
    private val repo: KdfPassGenConfigRepository,
) {
    suspend operator fun invoke(): String = mutex.withLock {
        var mkdId: String

        while (true) {
            mkdId = Uuid.random().toString()
            if (!repo.doesMasterKeyDigestIdExist(mkdId)) break
        }

        return mkdId
    }
}