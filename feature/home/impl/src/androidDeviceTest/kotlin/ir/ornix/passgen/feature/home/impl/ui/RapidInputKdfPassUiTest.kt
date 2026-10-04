package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.feature.home.impl.ui.KdfTestHarness.togglePasswordVisibility
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import kotlin.time.Duration.Companion.milliseconds

class RapidInputKdfPassUiTest : KoinTest {

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
    fun rapidInputTyping_cancelsPreviousCalculations_andDisplaysResultForLastInput() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            val configName = "Rapid Typing Test Config"
            val hasher = InputHasher.ARGON2ID
            val encoder = StringPassEncoder.Z85PassEncoder
            val length = 16

            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = configName,
                hasher = hasher,
                encoder = encoder,
            )

            val inputNode = composeTestRule.onNodeWithText("Input Phrase")

            val firstInput = "FastInputNumber1"
            val secondInput = "FastInputNumber2"
            val finalInput = "FinalInputString123"

            // Rapid input changes without waiting
            inputNode.performTextInput(firstInput)
            inputNode.performTextReplacement(secondInput)
            inputNode.performTextReplacement(finalInput)

            // Compute expected passwords
            val expectedFinalPass = KdfTestHarness.computeExpectedPassword(
                configName = configName,
                input = finalInput,
                hasher = hasher,
                encoder = encoder,
                passwordLength = length
            )

            val expectedOldPass = KdfTestHarness.computeExpectedPassword(
                configName = configName,
                input = firstInput,
                hasher = hasher,
                encoder = encoder,
                passwordLength = length
            )

            val finalPrefix =
                expectedFinalPass.substring(0, 16.coerceAtMost(expectedFinalPass.length))
            val oldPrefix =
                expectedOldPass.substring(0, 16.coerceAtMost(expectedOldPass.length))

            // Wait for password calculation to complete and assert password
            KdfTestHarness.waitForPasswordSubstring(
                rule = composeTestRule,
                configName = configName,
                expectedSubstring = finalPrefix
            )

            val startTime = System.currentTimeMillis()

            while (System.currentTimeMillis() - startTime < 10_000) {

                // Reveal password
                togglePasswordVisibility(
                    configName = configName,
                    isVisible = true,
                    rule = composeTestRule
                )

                // Final input's password prefix must be displayed
                KdfTestHarness.getPasswordNode(
                    configName = configName,
                    rule = composeTestRule,
                    expectedPass = finalPrefix,
                    substring = true
                ).assertIsDisplayed()

                // Intermediate/old input's password prefix must NOT be displayed
                KdfTestHarness.getPasswordNode(
                    configName = configName,
                    rule = composeTestRule,
                    expectedPass = oldPrefix,
                    substring = true
                ).assertIsNotDisplayed()

                delay(5.milliseconds)
            }
        }
    }
}
