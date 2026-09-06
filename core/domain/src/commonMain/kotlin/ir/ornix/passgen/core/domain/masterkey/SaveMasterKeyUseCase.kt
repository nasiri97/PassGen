package ir.ornix.passgen.core.domain.masterkey

import ir.ornix.passgen.core.domain.MasterKeyRepository

class SaveMasterKeyUseCase(
    private val repo: MasterKeyRepository
) {
    suspend operator fun invoke(masterKey: String) = repo.saveMasterKey(masterKey)
}