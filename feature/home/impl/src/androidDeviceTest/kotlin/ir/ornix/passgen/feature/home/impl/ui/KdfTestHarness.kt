package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.russhwolf.settings.MapSettings
import ir.ornix.passgen.core.common.isAndroidDebugBuild
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
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
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import ir.ornix.passgen.feature.config.impl.kdf.presentation.AddKdfConfigViewModel
import ir.ornix.passgen.feature.config.impl.kdf.ui.AddKdfConfigScreen
import ir.ornix.passgen.feature.home.impl.presentation.HomeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

object KdfTestHarness {

    const val MASTER_KEY_1 = "MASTER_KEY_NUMBER1_ABCDabcd_1234$*?!"
    const val MASTER_KEY_2 = "MASTER_KEY_NUMBER2_ABCDabcd_1234$*?!"

    const val GENERATION_TIMEOUT_MS = 60_000L

    val testModule = module {
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

    fun setup() {
        stopKoin()
        startKoin { modules(testModule) }
        isAndroidDebugBuild = true
    }

    fun tearDown() {
        stopKoin()
    }

    fun setContent(composeTestRule: ComposeContentTestRule) {
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

    fun addConfig(
        rule: ComposeContentTestRule,
        configName: String,
        masterKey: String = MASTER_KEY_1,
        hasher: InputHasher? = null,
        encoder: PassEncoder? = null,
        toggleTrimSpaces: Boolean = false,
        toggleCollapseSpaces: Boolean = false,
        toggleLowercase: Boolean = false,
        isFirstConfig: Boolean = true
    ) {
        if (isFirstConfig) {
            rule.onNodeWithText("No password configurations yet.").assertIsDisplayed()
            rule.onNodeWithText("Add your first config").performClick()
        } else {
            rule.onNodeWithContentDescription("Add Config").performClick()
        }

        // Step 1: Master Key
        rule.onNodeWithTag("master_key_input").performTextInput(masterKey)
        rule.onNodeWithTag("confirm_master_key_input").performTextInput(masterKey)
        rule.onNodeWithTag("next_button").performClick()

        // Step 2: Config Details
        rule.onNodeWithTag("config_name_input").performTextInput(configName)

        if (toggleTrimSpaces) {
            rule.onNodeWithText("Trim leading/trailing spaces").performClick()
        }
        if (toggleCollapseSpaces) {
            rule.onNodeWithText("Collapse multiple spaces").performClick()
        }
        if (toggleLowercase) {
            rule.onNodeWithText("Convert to lowercase").performClick()
        }

        if (hasher != null) {
            rule.onNodeWithText("Hashing Algorithm").performClick()
            rule.onNodeWithText(hasher.fullName).performClick()
        }

        if (encoder != null) {
            rule.onNodeWithText("Encoder Type").performClick()
            rule.onNodeWithText(encoder.fullName).performClick()
        }

        rule.onNodeWithTag("scroll_container").performScrollToNode(hasTestTag("submit_button"))
        rule.onNodeWithTag("submit_button").performClick()

        rule.onNodeWithText(configName).assertIsDisplayed()
    }

    fun enterInputPhrase(rule: ComposeContentTestRule, phrase: String) {
        val inputField = rule.onNodeWithText("Input Phrase")
        inputField.performTextReplacement(phrase)
    }

    fun waitForPasswordSubstring(
        rule: ComposeContentTestRule,
        expectedSubstring: String,
        timeoutMs: Long = GENERATION_TIMEOUT_MS
    ) {
        rule.waitUntil(timeoutMs) {
            val revealed = rule
                .onAllNodesWithText(expectedSubstring, substring = true)
                .fetchSemanticsNodes().isNotEmpty()

            if (!revealed) {
                val showButtons = rule.onAllNodesWithContentDescription("Show")
                val numButtons = showButtons.fetchSemanticsNodes().size
                for (i in 0 until numButtons) {
                    showButtons[i].performClick()
                }
            }
            revealed
        }
        rule.onNodeWithText(expectedSubstring, substring = true).assertIsDisplayed()
    }

    suspend fun computeExpectedPassword(
        configId: Int = 1,
        configName: String = "Test Config",
        masterKey: String = MASTER_KEY_1,
        input: String,
        hasher: InputHasher = InputHasher.ARGON2ID,
        encoder: PassEncoder = PassEncoder.getValidItems(hasher).first(),
        preprocessConfig: PreprocessConfig = PreprocessConfig(
            trimLeadingAndTrailingSpaces = false,
            collapseMultipleSpaces = false,
            convertToLowercase = false
        ),
        passwordLength: Int? = null
    ): String {
        val kdfConfig = KdfPassGenConfig(
            id = configId,
            name = configName,
            preprocessConfig = preprocessConfig,
            inputHasher = hasher,
            passEncoder = encoder,
            passwordLength = passwordLength
        )

        val hmacSigner = PlatformHmacSigner
        hmacSigner.registerKey(configId.toString(), masterKey.encodeToByteArray())

        val useCase = GenerateKDFPassUseCase(
            configRepo = object : PassGenConfigRepository {
                override fun getAll(): Flow<List<PassGenConfig>> = flowOf(emptyList())
                override suspend fun add(config: PassGenConfig): Int = config.id
                override suspend fun removeById(configId: Int) {}
            },
            hmacSigner = hmacSigner
        )

        val pass = useCase(kdfConfig, input)
        return pass?.value?.concatToString() ?: ""
    }
}
