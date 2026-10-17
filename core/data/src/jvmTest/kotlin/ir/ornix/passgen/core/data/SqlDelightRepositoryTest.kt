package ir.ornix.passgen.core.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.data.repository.SqlDelightAccountRepository
import ir.ornix.passgen.core.data.repository.SqlDelightAppConfigRepository
import ir.ornix.passgen.core.data.repository.SqlDelightLocalAuthRepository
import ir.ornix.passgen.core.data.repository.SqlDelightPassGenConfigRepository
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.model.Account
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.Password.Companion.toPassword
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SqlDelightRepositoryTest {

    private fun createInMemoryTestDb(): PassGenDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PassGenDatabase.Schema.create(driver)
        return PassGenDatabase(driver)
    }

    @AfterTest
    fun cleanup() {
        File("passgen.db").delete()
    }

    @Test
    fun testAccountRepositorySaveAndDelete() = runTest {
        val db = createInMemoryTestDb()
        val repo = SqlDelightAccountRepository(db)

        val account = Account(
            id = 0,
            username = "testuser",
            password = "SecretPassword123!".toPassword(32)
        )

        repo.save(account)
        val accounts = repo.getAll().first()
        assertEquals(1, accounts.size)
        assertEquals("testuser", accounts[0].username)
        assertEquals("SecretPassword123!", accounts[0].password.value.concatToString())

        val savedId = accounts[0].id
        repo.delete(savedId)
        val accountsAfterDelete = repo.getAll().first()
        assertTrue(accountsAfterDelete.isEmpty())
    }

    @Test
    fun testAppConfigRepository() = runTest {
        val db = createInMemoryTestDb()
        val repo = SqlDelightAppConfigRepository(database = db)

        assertTrue(repo.isFirstLaunch())
        repo.setFirstLaunch(false)
        assertFalse(repo.isFirstLaunch())

        repo.setLanguage(AppLanguage.PERSIAN)
        assertEquals(AppLanguage.PERSIAN, repo.getLanguage().value)

        repo.setTheme(AppTheme.DARK)
        assertEquals(AppTheme.DARK, repo.getTheme().value)

        repo.setCopyEnabled(true)
        assertTrue(repo.isCopyEnabled().value)

        repo.setVisibilityTimeout(PasswordVisibilityTimeout.MINUTE_1)
        assertEquals(PasswordVisibilityTimeout.MINUTE_1, repo.getVisibilityTimeout().value)
    }

    @Test
    fun testLocalAuthRepository() = runTest {
        val db = createInMemoryTestDb()
        val repo = SqlDelightLocalAuthRepository(database = db)

        assertEquals(LocalAuthType.NONE, repo.getLocalAuthType().value)
        repo.setLocalAuthType(LocalAuthType.PIN)
        assertEquals(LocalAuthType.PIN, repo.getLocalAuthType().value)

        val secret = byteArrayOf(1, 2, 3, 4, 5)
        repo.saveEncryptedSecret(secret)
        assertTrue(secret.contentEquals(repo.getEncryptedSecret()))

        repo.setBiometricEnabled(true)
        assertTrue(repo.isBiometricEnabled().value)

        repo.clearAuth()
        assertEquals(LocalAuthType.NONE, repo.getLocalAuthType().value)
        assertFalse(repo.isBiometricEnabled().value)
    }

    @Test
    fun testPassGenConfigRepository() = runTest {
        val db = createInMemoryTestDb()
        val repo = SqlDelightPassGenConfigRepository(db)

        val kdfConfig = KdfPassGenConfig(
            id = 0,
            name = "Test KDF Config",
            preprocessConfig = PreprocessConfig(
                trimLeadingAndTrailingSpaces = true,
                collapseMultipleSpaces = true,
                convertToLowercase = false
            ),
            inputHasher = InputHasher.SHA256,
            passEncoder = PassEncoder.fromKey(PassEncoder.KEY_PASS_ENCODER_BASE64_STANDARD),
            passwordLength = 16
        )

        val randomConfig = RandomPassGenConfig(
            id = 0,
            name = "Test Random Config",
            passEncoder = PassEncoder.fromKey(PassEncoder.KEY_PASS_ENCODER_BASE16_HEXADECIMAL),
            passwordLength = 20
        )

        val kdfId = repo.add(kdfConfig)
        val randomId = repo.add(randomConfig)

        val configs = repo.getAll().first()
        assertEquals(2, configs.size)

        val savedKdf = configs.first { it.id == kdfId } as KdfPassGenConfig
        assertEquals("Test KDF Config", savedKdf.name)
        assertEquals(InputHasher.SHA256, savedKdf.inputHasher)
        assertEquals(16, savedKdf.passwordLength)
        assertTrue(savedKdf.preprocessConfig.trimLeadingAndTrailingSpaces)
        assertTrue(savedKdf.preprocessConfig.collapseMultipleSpaces)
        assertFalse(savedKdf.preprocessConfig.convertToLowercase)

        val savedRandom = configs.first { it.id == randomId } as RandomPassGenConfig
        assertEquals("Test Random Config", savedRandom.name)
        assertEquals(20, savedRandom.passwordLength)

        repo.removeById(kdfId)
        val configsAfterRemove = repo.getAll().first()
        assertEquals(1, configsAfterRemove.size)
        assertEquals(randomId, configsAfterRemove[0].id)
    }
}
