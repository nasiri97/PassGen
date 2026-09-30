package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.FakePassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.KDFPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetAllKDFPassGenConfigsUseCaseTest {

    private lateinit var repository: FakePassGenConfigRepository
    private lateinit var useCase: GetAllKDFPassGenConfigsUseCase

    private val defaultConfig1 = KDFPassGenConfig(
        id = 1,
        name = "Config 1",
        preprocessConfig = PreprocessConfig(
            trimLeadingAndTrailingSpaces = true,
            collapseMultipleSpaces = false,
            convertToLowercase = false
        ),
        inputHasher = InputHasher.SHA256,
        passEncoder = StringPassEncoder.HexPassEncoder,
        passwordLength = 32
    )

    private val defaultConfig2 = KDFPassGenConfig(
        id = 2,
        name = "Config 2",
        preprocessConfig = PreprocessConfig(
            trimLeadingAndTrailingSpaces = false,
            collapseMultipleSpaces = true,
            convertToLowercase = true
        ),
        inputHasher = InputHasher.SHA512,
        passEncoder = StringPassEncoder.Base64PassEncoder,
        passwordLength = 64
    )

    @BeforeTest
    fun setup() {
        repository = FakePassGenConfigRepository()
        useCase = GetAllKDFPassGenConfigsUseCase(repository)
    }

    @Test
    fun `invoke should emit empty list when repository is empty`() = runTest {
        val configs = useCase().first()
        assertTrue(configs.isEmpty(), "Expected empty list from empty repository")
    }

    @Test
    fun `invoke should emit list containing single config when repository has one config`() =
        runTest {
            repository.add(defaultConfig1)

            val configs = useCase().first()

            assertEquals(1, configs.size)
            assertEquals(defaultConfig1, configs.first())
        }

    @Test
    fun `invoke should emit list containing all configs when repository has multiple configs`() =
        runTest {
            repository.add(defaultConfig1)
            repository.add(defaultConfig2)

            val configs = useCase().first()

            assertEquals(2, configs.size)
            assertEquals(listOf(defaultConfig1, defaultConfig2), configs)
        }

    @Test
    fun `invoke flow should reflect dynamic additions and removals from repository`() = runTest {
        // Initial state: empty
        assertEquals(0, useCase().first().size)

        // Add config 1
        repository.add(defaultConfig1)
        val afterAdd1 = useCase().first()
        assertEquals(1, afterAdd1.size)
        assertEquals(defaultConfig1, afterAdd1.first())

        // Add config 2
        repository.add(defaultConfig2)
        val afterAdd2 = useCase().first()
        assertEquals(2, afterAdd2.size)
        assertEquals(listOf(defaultConfig1, defaultConfig2), afterAdd2)

        // Remove config 1
        repository.removeById(defaultConfig1.id)
        val afterRemove1 = useCase().first()
        assertEquals(1, afterRemove1.size)
        assertEquals(defaultConfig2, afterRemove1.first())

        // Remove config 2
        repository.removeById(defaultConfig2.id)
        val afterRemove2 = useCase().first()
        assertTrue(afterRemove2.isEmpty(), "Repository should be empty after removing all configs")
    }

    @Test
    fun `invoke should preserve all config properties intact`() = runTest {
        val customConfig = KDFPassGenConfig(
            id = 100,
            name = "Custom Config Name",
            preprocessConfig = PreprocessConfig(
                trimLeadingAndTrailingSpaces = true,
                collapseMultipleSpaces = true,
                convertToLowercase = true
            ),
            inputHasher = InputHasher.ARGON2ID,
            passEncoder = StringPassEncoder.Z85PassEncoder,
            passwordLength = 40
        )
        repository.add(customConfig)

        val retrievedConfig = useCase().first().first()

        assertEquals(100, retrievedConfig.id)
        assertEquals("Custom Config Name", retrievedConfig.name)
        assertEquals(InputHasher.ARGON2ID, retrievedConfig.inputHasher)
        assertEquals(StringPassEncoder.Z85PassEncoder, retrievedConfig.passEncoder)
        assertEquals(40, retrievedConfig.passwordLength)
        assertEquals(true, retrievedConfig.preprocessConfig.trimLeadingAndTrailingSpaces)
        assertEquals(true, retrievedConfig.preprocessConfig.collapseMultipleSpaces)
        assertEquals(true, retrievedConfig.preprocessConfig.convertToLowercase)
    }
}
