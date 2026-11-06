package ir.ornix.passgen.core.domain.passgenconfig.random

import ir.ornix.passgen.core.domain.RandomPassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.flow.Flow

class GetAllRandomPassGenConfigsUseCase(
    private val repo: RandomPassGenConfigRepository
) {
    operator fun invoke(): Flow<List<RandomPassGenConfig>> = repo.getAllConfigs()
}