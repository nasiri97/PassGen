package ir.ornix.passgen.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.CredentialRepository
import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.Password
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightCredentialRepository(
    database: PassGenDatabase,
    private val driver: SqlDriver,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : CredentialRepository {

    private val queries = database.credentialQueries

    override suspend fun save(credential: Credential): Unit = withContext(ioDispatcher) {
        try {
            PassGenDatabase.Schema.create(driver).await()
        } catch (_: Exception) {
        }

        val packageNamesStr = credential.associatedPackageNames.joinToString(",")
        val webDomainsStr = credential.associatedWebDomains.joinToString(",")
        val keywordsStr = credential.associatedKeywords.joinToString(",")

        if (credential.id > 0) {
            queries.updateCredential(
                id = credential.id,
                title = credential.title,
                description = credential.description,
                password_value = credential.passwordValue.concatToString(),
                password_entropy_byte_size = credential.passwordEntropyByteSize?.toLong(),
                username = credential.username,
                email = credential.email,
                phone = credential.phone,
                associated_package_names = packageNamesStr,
                associated_web_domains = webDomainsStr,
                associated_keywords = keywordsStr
            )
        } else {
            queries.insertCredentialAutoId(
                title = credential.title,
                description = credential.description,
                password_value = credential.passwordValue.concatToString(),
                password_entropy_byte_size = credential.passwordEntropyByteSize?.toLong(),
                username = credential.username,
                email = credential.email,
                phone = credential.phone,
                associated_package_names = packageNamesStr,
                associated_web_domains = webDomainsStr,
                associated_keywords = keywordsStr
            )
        }
    }

    override suspend fun delete(credentialId: Long): Unit = withContext(ioDispatcher) {
        try {
            PassGenDatabase.Schema.create(driver).await()
        } catch (_: Exception) {
        }
        queries.deleteCredentialById(credentialId)
    }

    override fun getAll(): Flow<List<Credential>> {
        return queries.selectAllCredentials()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { list ->
                list.map { entity ->
                    Credential(
                        id = entity.id,
                        title = entity.title,
                        description = entity.description,
                        password = Password(
                            value = entity.password_value.toCharArray(),
                            entropyByteSize = entity.password_entropy_byte_size?.toInt()
                        ),
                        username = entity.username,
                        email = entity.email,
                        phone = entity.phone,
                        associatedPackageNames = entity.associated_package_names.split(",")
                            .filter { it.isNotBlank() }
                            .toSet(),
                        associatedWebDomains = entity.associated_web_domains.split(",")
                            .filter { it.isNotBlank() }
                            .toSet(),
                        associatedKeywords = entity.associated_keywords.split(",")
                            .filter { it.isNotBlank() }
                            .toSet()
                    )
                }
            }
    }
}
