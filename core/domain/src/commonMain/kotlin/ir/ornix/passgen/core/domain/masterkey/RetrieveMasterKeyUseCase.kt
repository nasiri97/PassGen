package ir.ornix.passgen.core.domain.masterkey

import ir.ornix.passgen.core.domain.MasterKeyRepository
import kotlinx.coroutines.flow.Flow

class RetrieveMasterKeyUseCase (
    private val repo: MasterKeyRepository
) {
    operator fun invoke(): Flow<String?> = repo.getMasterKey()
}