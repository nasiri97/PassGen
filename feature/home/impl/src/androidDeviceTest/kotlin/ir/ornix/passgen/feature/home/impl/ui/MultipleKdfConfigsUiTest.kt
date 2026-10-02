package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest

class MultipleKdfConfigsUiTest : KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        KdfTestHarness.setup()
    }

    @After
    fun tearDown() {
        KdfTestHarness.tearDown()
    }

    @Test
    fun displaysMultiplePasswordConfigs_andCalculatesPasswordsForBoth() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            val name1 = "Config SHA256"
            val hasher1 = InputHasher.SHA256
            val encoder1 = StringPassEncoder.HexPassEncoder

            val name2 = "Config Argon2id"
            val hasher2 = InputHasher.ARGON2ID
            val encoder2 = StringPassEncoder.Z85PassEncoder

            val inputPhrase = "Hello World"

            // Add first config
            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = name1,
                hasher = hasher1,
                encoder = encoder1,
                isFirstConfig = true,
            )

            // Add second config
            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = name2,
                hasher = hasher2,
                encoder = encoder2,
                isFirstConfig = false,
            )

            // Both config titles are displayed
            composeTestRule.onNodeWithText(name1).assertIsDisplayed()
            composeTestRule.onNodeWithText(name2).assertIsDisplayed()

            // Enter input phrase
            KdfTestHarness.enterInputPhrase(composeTestRule, inputPhrase)

            // Compute expected passwords
            val pass1 = KdfTestHarness.computeExpectedPassword(
                configId = 1,
                configName = name1,
                input = inputPhrase,
                hasher = hasher1,
                encoder = encoder1,
            )
            val prefix1 = pass1.substring(0, 16.coerceAtMost(pass1.length))

            val pass2 = KdfTestHarness.computeExpectedPassword(
                configId = 2,
                configName = name2,
                input = inputPhrase,
                hasher = hasher2,
                encoder = encoder2,
            )
            val prefix2 = pass2.substring(0, 16.coerceAtMost(pass2.length))

            // Wait and assert passwords for both items
            KdfTestHarness.waitForPasswordSubstring(composeTestRule, prefix1)
            KdfTestHarness.waitForPasswordSubstring(composeTestRule, prefix2)
        }
    }

    @Test
    fun updatesAllPasswordConfigs_whenInputPhraseChanges() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            val name1 = "Config SHA256"
            val hasher1 = InputHasher.SHA256
            val encoder1 = StringPassEncoder.HexPassEncoder

            val name2 = "Config SHA512"
            val hasher2 = InputHasher.SHA512
            val encoder2 = StringPassEncoder.HexPassEncoder

            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = name1,
                hasher = hasher1,
                encoder = encoder1,
                isFirstConfig = true,
            )

            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = name2,
                hasher = hasher2,
                encoder = encoder2,
                isFirstConfig = false,
            )

            // 1. First Input Phrase
            val phrase1 = "Phrase One"
            KdfTestHarness.enterInputPhrase(composeTestRule, phrase1)

            val pass1Phrase1 = KdfTestHarness.computeExpectedPassword(
                configId = 1, configName = name1, input = phrase1, hasher = hasher1, encoder = encoder1
            )
            val pass2Phrase1 = KdfTestHarness.computeExpectedPassword(
                configId = 2, configName = name2, input = phrase1, hasher = hasher2, encoder = encoder2
            )

            KdfTestHarness.waitForPasswordSubstring(composeTestRule, pass1Phrase1.substring(0, 16))
            KdfTestHarness.waitForPasswordSubstring(composeTestRule, pass2Phrase1.substring(0, 16))

            // 2. Change to Second Input Phrase
            val phrase2 = "Phrase Two"
            KdfTestHarness.enterInputPhrase(composeTestRule, phrase2)

            val pass1Phrase2 = KdfTestHarness.computeExpectedPassword(
                configId = 1, configName = name1, input = phrase2, hasher = hasher1, encoder = encoder1
            )
            val pass2Phrase2 = KdfTestHarness.computeExpectedPassword(
                configId = 2, configName = name2, input = phrase2, hasher = hasher2, encoder = encoder2
            )

            KdfTestHarness.waitForPasswordSubstring(composeTestRule, pass1Phrase2.substring(0, 16))
            KdfTestHarness.waitForPasswordSubstring(composeTestRule, pass2Phrase2.substring(0, 16))
        }
    }
}
