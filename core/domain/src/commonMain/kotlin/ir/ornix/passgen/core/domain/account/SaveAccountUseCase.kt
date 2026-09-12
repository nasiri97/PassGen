package ir.ornix.passgen.core.domain.account

import ir.ornix.passgen.core.domain.AccountRepository
import ir.ornix.passgen.core.model.Account

class SaveAccountUseCase(private val repository: AccountRepository) {
    suspend operator fun invoke(account: Account) = repository.save(account)
}
