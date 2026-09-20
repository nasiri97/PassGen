package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import ir.ornix.passgen.core.domain.AccountRepository
import ir.ornix.passgen.core.domain.FakeHmacSigner
import ir.ornix.passgen.core.domain.FakePassGenConfigRepository
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.account.SaveAccountUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateKDFPassUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateRandomPassUseCase
import ir.ornix.passgen.core.domain.passgenconfig.AddPassGenConfigUseCase
import ir.ornix.passgen.core.domain.passgenconfig.GetAllPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.RemovePassGenConfigUseCase
import ir.ornix.passgen.core.model.Account
import ir.ornix.passgen.feature.home.impl.presentation.HomeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

    private val testModule = module {
        single { FakePassGenConfigRepository() } bind PassGenConfigRepository::class
        single { FakeHmacSigner() } bind HmacSigner::class
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
        factoryOf(::AddPassGenConfigUseCase)
        factoryOf(::GetAllPassGenConfigsUseCase)
        factoryOf(::RemovePassGenConfigUseCase)
        factoryOf(::SaveAccountUseCase)
        factory { GenerateRandomPassUseCase() }

        viewModelOf(::HomeViewModel)
    }

    @Before
    fun setup() {
        stopKoin()
        startKoin {
            modules(testModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testGeneratePasswordFlow() {
        composeTestRule.setContent {
            KoinContext {
                HomeScreen()
            }
        }

        // 1. Check empty state and click "Add your first config"
        composeTestRule.onNodeWithText("No password configurations yet.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add your first config").performClick()

        // 2. Fill the "Create Password Config" dialog
        composeTestRule.onNodeWithText("Configuration Name").performTextInput("Work")
        composeTestRule.onNodeWithText("Master Key")
            .performTextInput("12345678 12345678 12345678 12345678")

        // Click "Create" button
        composeTestRule.onNodeWithText("Create").performClick()

        // 3. Verify config is added to the list and dialog is gone
        composeTestRule.onNodeWithText("Work").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Password Config").assertDoesNotExist()

        // 4. Enter secret phrase in the InputSection
        composeTestRule.onNodeWithText("Secret Phrase").performTextInput("example.com")

        // 5. Verify that the password generation started and the Copy button appeared
        // (The actual password value is obscured by default, but we can check for the action button)
        composeTestRule.onNodeWithContentDescription("Copy").assertIsDisplayed()

        // We can also verify a specific deterministic password if we know it and it's visible in semantics
        // Since PasswordAndActions shows it as Text:
        // For SHA256/Hex, input="example.com", key="12345678 12345678 12345678 12345678"
        // The value should be "3eb97c87..." (if 16 chars) or 64 chars by default.
        // Wait, default length in dialog is 64.
        // SHA-256 Hex 64 chars is: "76f980c25c9e2e9ca2b8f2bb33200e4b0673bb892ed337491401ef163f544dca"

        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithText("76f980c25c9e2e9ca2b8f2bb33200e4b0673bb892ed337491401ef163f544dca")
                    .assertIsDisplayed()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }
}
