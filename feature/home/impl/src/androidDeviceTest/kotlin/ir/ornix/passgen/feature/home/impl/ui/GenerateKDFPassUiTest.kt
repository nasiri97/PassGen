package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.russhwolf.settings.MapSettings
import ir.ornix.passgen.core.common.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.core.common.codec.Base64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.codec.Z85BinaryCodec
import ir.ornix.passgen.core.common.isAndroidDebugBuild
import ir.ornix.passgen.core.data.PlatformHmacSigner
import ir.ornix.passgen.core.data.SettingsPassGenConfigRepository
import ir.ornix.passgen.core.domain.AccountRepository
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.account.SaveAccountUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateKDFPassUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateRandomPassUseCase
import ir.ornix.passgen.core.domain.passgenconfig.kdf.AddKdfPassGenConfigUseCase
import ir.ornix.passgen.core.domain.passgenconfig.kdf.GetAllKdfPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.kdf.RemoveKdfPassGenConfigUseCase
import ir.ornix.passgen.core.model.Account
import ir.ornix.passgen.feature.config.impl.kdf.presentation.AddKdfConfigViewModel
import ir.ornix.passgen.feature.config.impl.kdf.ui.AddKdfConfigScreen
import ir.ornix.passgen.feature.home.impl.presentation.HomeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest

class GenerateKDFPassUiTest : KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val base64Codec = Base64BinaryCodec()
    private val bCryptCodec = BCryptBase64BinaryCodec()
    private val hexCodec = HexBinaryCodec(false)

    private suspend fun createTestCase(
        masterKey: String,
        input: String,
        argon2id: String,
        bcrypt: String,
        sha512: String,
        sha256: String
    ) = TestCase(
        masterKey = masterKey,
        input = input,
        argon2id = base64Codec.decode(argon2id.substring(argon2id.lastIndexOf('$') + 1)),
        bcrypt = bCryptCodec.decode(bcrypt.substring(29)),
        sha512 = hexCodec.decode(sha512),
        sha256 = hexCodec.decode(sha256)
    )

    companion object {
        private const val MK1 = "MASTER_KEY_NUMBER1_ABCDabcd_1234$*?!"
        private const val MK2 = "MASTER_KEY_NUMBER2_ABCDabcd_1234$*?!"

        private const val INPUT1 = ""
        private const val INPUT2 = "Hello World"

        private const val GENERATION_TIMEOUT_MS = 60_000L
    }

    private suspend fun testCase1() = createTestCase(
        masterKey = MK1,
        input = INPUT1,
        argon2id = $$"$argon2id$v=19$m=131072,t=4,p=1$pXey1W36qe9vQXV+cQRDmQ$4LmaKI3VBU8RMcBlZZb5P7upnXDrfrW9iBlBS+85cqmPa+kolG6c3kkIXlH5cj+AFjMq5Dd5uTjti5w7+E3eCA",
        bcrypt = $$"$2b$12$nVcwzU14oc7tOVT8aOPBkOTAenggmnwEZ05x0I2N6XR6KAl/tlEdy",
        sha512 = "e12c3072d4dc896a983a011c3a6607e194d82def65ce767a1d4b43c56ef4b921c0780a98af739a20fec2d5f4e9568998ed9ce7a4eef32dd0275c2cd0bd6b9062",
        sha256 = "a577b2d56dfaa9ef6f41757e71044399bf778161718106c47d28d08ee15862f3"
    )

    private suspend fun testCase2() = createTestCase(
        masterKey = MK1,
        input = INPUT2,
        argon2id = $$"$argon2id$v=19$m=131072,t=4,p=1$sc2Pdl6nCZvTASFM/cFYpg$L2BWAdIzwctqbgz7vx9qxbV+oX3YIoTCfUMrdP8Z3TUERxeL7RpIs5RHkyakgYr/ZSDwJHLuW+aLOxL7aAiF2A",
        bcrypt = $$"$2b$12$qa0Nbj4lAXtR.QDK9aDWneA6ZqTMCkn237knCS7vQ/7ramWhP/3dK",
        sha512 = "1f5488591763277ffc5dda6ffd07ae07f7da2506259f1180774a43cb55cda18c98402ff3a2855c897d87ae9ec5fe58e23fb67c228f6c83dc08bb7abc96b76818",
        sha256 = "b1cd8f765ea7099bd301214cfdc158a6177637181ffdb4ea8845770f93a22d4a"
    )

    private suspend fun testCase3() = createTestCase(
        masterKey = MK2,
        input = INPUT1,
        argon2id = $$"$argon2id$v=19$m=131072,t=4,p=1$ddLWT2nJB+zIA135609Knw$8h3g9QfLtVrWdZZtWY+u1fGjRpWOUekTXodqsH2CUBa9o9YZBVnYgM6tHJCI7LJvdEURr3gBC78A44Hmb5qUKQ",
        bcrypt = $$"$2b$12$bbJUR0lH/8xG.z134y7Ilubmjk40c01yBqaP3Mkb8PjFwEQdXxSgS",
        sha512 = "39cd14bbf79addb2fbaa88f1a479b41cecaac17d1ba858974fed236dcdbd540d40aa4792baef4d53cb30584659e5c349e8c2cd066df50404048fa8d99827b1d5",
        sha256 = "75d2d64f69c907ecc8035df9eb4f4a9f835be281ec3419d149d35c4b314fd787"
    )

    private suspend fun testCase4() = createTestCase(
        masterKey = MK2,
        input = INPUT2,
        argon2id = $$"$argon2id$v=19$m=131072,t=4,p=1$8TdghHqXH+EepUJbfp8OaA$7EQDsCVR9Yna8ODy3o9aKnAoisD3CTy7wuwxv9Btkdo9wGcRntMeyUrRHVxYnP3pugXgZyvMtCAjmL+OmkbzOQ",
        bcrypt = $$"$2b$12$6RbefFoVF8CcnSHZdn6MY.n2PKDvt/H7LqS.nsMN72uNFzo9VW09e",
        sha512 = "31a31910e0b7b4c449128efe72997b9934e03c520dd76bb9bf1a9513f885edd28e564f5e2cebc44b6d71f5f3398f27b451fe23d581a80cd7e70648f0e360c41c",
        sha256 = "f13760847a971fe11ea5425b7e9f0e685f4c406980963a2d6b039b2980a8a1b0"
    )

    private val testModule = module {
        // In-memory settings: fresh per test, nothing persisted between runs.
        single { SettingsPassGenConfigRepository(MapSettings()) } bind PassGenConfigRepository::class
        single { PlatformHmacSigner } bind HmacSigner::class
        single {
            object : AccountRepository {
                override suspend fun save(account: Account) {}
                override suspend fun delete(accountId: Int) {
                    TODO("Not yet implemented")
                }

                override fun getAll(): Flow<List<Account>> = flowOf(emptyList())
            }
        } bind AccountRepository::class

        factoryOf(::GenerateKDFPassUseCase)
        factoryOf(::AddKdfPassGenConfigUseCase)
        factoryOf(::GetAllKdfPassGenConfigsUseCase)
        factoryOf(::RemoveKdfPassGenConfigUseCase)
        factoryOf(::SaveAccountUseCase)
        factory { GenerateRandomPassUseCase() }

        viewModelOf(::HomeViewModel)
        viewModelOf(::AddKdfConfigViewModel)
    }

    @Before
    fun setup() {
        stopKoin()
        startKoin { modules(testModule) }
        isAndroidDebugBuild = true
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    // One test per vector so each starts with an empty config list.
    @Test
    fun generatesExpectedPassword_case1() = runCase { testCase1() }

    @Test
    fun generatesExpectedPassword_case2() = runCase { testCase2() }

    @Test
    fun generatesExpectedPassword_case3() = runCase { testCase3() }

    @Test
    fun generatesExpectedPassword_case4() = runCase { testCase4() }

    private fun runCase(testCase: suspend () -> TestCase) = runBlocking {
        val case = testCase()
        setContent()
        testGeneratePasswordFlow(case)
    }

    private fun setContent() {
        composeTestRule.setContent {
            KoinContext {
                var showAddConfigScreen by remember { mutableStateOf(false) }

                if (showAddConfigScreen) {
                    AddKdfConfigScreen(
                        onNavigateBack = { showAddConfigScreen = false },
                        onConfigCreated = { showAddConfigScreen = false }
                    )
                } else {
                    HomeScreen(
                        onNavigateToCreateConfig = { showAddConfigScreen = true }
                    )
                }
            }
        }
    }

    private suspend fun testGeneratePasswordFlow(testCase: TestCase) {
        // 1. Empty state -> add first config
        composeTestRule.onNodeWithText("No password configurations yet.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add your first config").performClick()

        // 2. Step 1: master key
        composeTestRule.onNodeWithTag("master_key_input").performTextInput(testCase.masterKey)
        composeTestRule.onNodeWithTag("confirm_master_key_input")
            .performTextInput(testCase.masterKey)
        composeTestRule.onNodeWithTag("next_button").performClick()

        // 3. Step 2: config details
        composeTestRule.onNodeWithTag("config_name_input").performTextInput("Test Config")
        composeTestRule.onNodeWithTag("Convert to lowercase").performClick()

        composeTestRule
            .onNodeWithTag("scroll_container")
            .performScrollToNode(hasTestTag("submit_button"))
        composeTestRule.onNodeWithTag("submit_button").performClick()

        // 4. Config added, create screen gone
        composeTestRule.onNodeWithText("Test Config").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Password Config").assertDoesNotExist()

        // 5. Enter the input phrase for this case
        if (testCase.input.isNotEmpty()) {
            composeTestRule.onNodeWithText("Input Phrase").performTextInput(testCase.input)
        }

        // 6. Poll until the generated password is visible, revealing it only when needed
        val expectedPrefix = Z85BinaryCodec().encode(testCase.argon2id).substring(0, 16)

        composeTestRule.waitUntil(GENERATION_TIMEOUT_MS) {
            val revealed = composeTestRule
                .onAllNodesWithText(expectedPrefix, substring = true)
                .fetchSemanticsNodes().isNotEmpty()

            if (!revealed) {
                val showButtons = composeTestRule.onAllNodesWithContentDescription("Show")
                if (showButtons.fetchSemanticsNodes().isNotEmpty()) {
                    showButtons[0].performClick()
                }
            }
            revealed
        }
        composeTestRule.onNodeWithText(expectedPrefix, substring = true).assertIsDisplayed()
    }
}

private class TestCase(
    val masterKey: String,
    val input: String,
    val argon2id: ByteArray,
    val bcrypt: ByteArray,
    val sha512: ByteArray,
    val sha256: ByteArray
)