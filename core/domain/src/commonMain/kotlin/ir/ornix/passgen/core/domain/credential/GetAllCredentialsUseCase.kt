package ir.ornix.passgen.core.domain.credential

import ir.ornix.passgen.core.domain.CredentialRepository
import ir.ornix.passgen.core.model.Credential
import kotlinx.coroutines.flow.Flow

class GetAllCredentialsUseCase(private val repository: CredentialRepository) {
    operator fun invoke(): Flow<List<Credential>> = repository.getAll()
}
