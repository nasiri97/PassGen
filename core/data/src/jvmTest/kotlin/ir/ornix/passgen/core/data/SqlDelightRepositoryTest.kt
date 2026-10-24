package ir.ornix.passgen.core.data

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.russhwolf.settings.MapSettings
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.data.repository.SettingsAppConfigRepository
import ir.ornix.passgen.core.data.repository.SqlDelightCredentialRepository
import ir.ornix.passgen.core.data.repository.SqlDelightLocalAuthRepository
import ir.ornix.passgen.core.data.repository.SqlDelightPassGenConfigRepository
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.Password.Companion.toPassword
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SqlDelightRepositoryTest {

    @AfterTest
    fun cleanup() {
        File("passgen.db").delete()
    }

    @Test
    fun testAccountRepositorySaveAndDelete() = runTest {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PassGenDatabase.Schema.synchronous().create(driver)
        val db = PassGenDatabase(driver)
        val repo = SqlDelightCredentialRepository(db, driver)

        val credential = Credential(
            id = 0,
            title = "Test Account",
            username = "testuser",
            passwordValue = "SecretPassword123!".toPassword(32),
            email = "user@example.com",
            associatedPackageNames = setOf("com.example.app"),
            associatedWebDomains = setOf("example.com")
        )

        repo.save(credential)
        val accounts = repo.getAll().first()
        assertEquals(1, accounts.size)
        assertEquals("Test Account", accounts[0].title)
        assertEquals("testuser", accounts[0].username)
        assertEquals("SecretPassword123!", accounts[0].passwordValue.value.concatToString())
        assertEquals("user@example.com", accounts[0].email)
        assertEquals(setOf("com.example.app"), accounts[0].associatedPackageNames)
        assertEquals(setOf("example.com"), accounts[0].associatedWebDomains)

        val savedId = accounts[0].id
        repo.delete(savedId)
        val accountsAfterDelete = repo.getAll().first()
        assertTrue(accountsAfterDelete.isEmpty())
    }

    @Test
    fun testAppConfigRepository() = runTest {
        val repo = SettingsAppConfigRepository(MapSettings())

        repo.setLanguage(AppLanguage.PERSIAN)
        testScheduler.advanceUntilIdle()
        assertEquals(AppLanguage.PERSIAN, repo.getLanguage().value)

        repo.setTheme(AppTheme.DARK)
        testScheduler.advanceUntilIdle()
        assertEquals(AppTheme.DARK, repo.getTheme().value)

        repo.setCopyEnabled(true)
        testScheduler.advanceUntilIdle()
        assertTrue(repo.isCopyEnabled().value)

        repo.setVisibilityTimeout(PasswordVisibilityTimeout.MINUTE_1)
        testScheduler.advanceUntilIdle()
        assertEquals(PasswordVisibilityTimeout.MINUTE_1, repo.getVisibilityTimeout().value)
    }

    @Test
    fun testLocalAuthRepository() = runTest {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PassGenDatabase.Schema.synchronous().create(driver)
        val db = PassGenDatabase(driver)
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val repo = SqlDelightLocalAuthRepository(
            database = db,
            driver = driver,
            ioDispatcher = testDispatcher
        )

        testScheduler.advanceUntilIdle()
        assertEquals(LocalAuthType.NONE, repo.getLocalAuthType().value)

        repo.setLocalAuthType(LocalAuthType.PIN)
        testScheduler.advanceUntilIdle()
        assertEquals(LocalAuthType.PIN, repo.getLocalAuthType().value)

        val secret = byteArrayOf(1, 2, 3, 4, 5)
        repo.saveEncryptedSecret(secret)
        testScheduler.advanceUntilIdle()
        assertTrue(secret.contentEquals(repo.getEncryptedSecret()))

        repo.setBiometricEnabled(true)
        testScheduler.advanceUntilIdle()
        assertTrue(repo.isBiometricEnabled().value)

        repo.clearAuth()
        testScheduler.advanceUntilIdle()
        assertEquals(LocalAuthType.NONE, repo.getLocalAuthType().value)
        assertFalse(repo.isBiometricEnabled().value)
    }

    @Test
    fun testPassGenConfigRepository() = runTest {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PassGenDatabase.Schema.synchronous().create(driver)
        val db = PassGenDatabase(driver)
        val repo = SqlDelightPassGenConfigRepository(db, driver)

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
