package ir.ornix.passgen.core.domain.masterkey

import ir.ornix.passgen.core.domain.MasterKeyRepository

class ClearMasterKeyUseCase(
    private val repo: MasterKeyRepository
) {

    suspend operator fun invoke() = repo.clearMasterKey()
}