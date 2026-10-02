package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest

class GenerateKDFPassUiTest : KoinTest {

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
    fun generatesExpectedPassword_withArgon2idHasher() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            val configName = "Argon2id Config"
            val inputPhrase = "Hello World"
            val hasher = InputHasher.ARGON2ID
            val encoder = StringPassEncoder.Z85PassEncoder

            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = configName,
                hasher = hasher,
                encoder = encoder,
            )

            KdfTestHarness.enterInputPhrase(composeTestRule, inputPhrase)

            val expectedPass = KdfTestHarness.computeExpectedPassword(
                configName = configName,
                input = inputPhrase,
                hasher = hasher,
                encoder = encoder,
            )
            val expectedPrefix = expectedPass.substring(0, 16.coerceAtMost(expectedPass.length))

            KdfTestHarness.waitForPasswordSubstring(composeTestRule, expectedPrefix)
        }
    }

    @Test
    fun generatesExpectedPassword_withBcryptHasher() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            val configName = "Bcrypt Config"
            val inputPhrase = "Hello World"
            val hasher = InputHasher.BCrypt
            val encoder = StringPassEncoder.Base64PassEncoder

            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = configName,
                hasher = hasher,
                encoder = encoder,
            )

            KdfTestHarness.enterInputPhrase(composeTestRule, inputPhrase)

            val expectedPass = KdfTestHarness.computeExpectedPassword(
                configName = configName,
                input = inputPhrase,
                hasher = hasher,
                encoder = encoder,
            )
            val expectedPrefix = expectedPass.substring(0, 16.coerceAtMost(expectedPass.length))

            KdfTestHarness.waitForPasswordSubstring(composeTestRule, expectedPrefix)
        }
    }

    @Test
    fun generatesExpectedPassword_withSha256Hasher() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            val configName = "SHA-256 Config"
            val inputPhrase = "Hello World"
            val hasher = InputHasher.SHA256
            val encoder = StringPassEncoder.HexPassEncoder

            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = configName,
                hasher = hasher,
                encoder = encoder,
            )

            KdfTestHarness.enterInputPhrase(composeTestRule, inputPhrase)

            val expectedPass = KdfTestHarness.computeExpectedPassword(
                configName = configName,
                input = inputPhrase,
                hasher = hasher,
                encoder = encoder,
            )
            val expectedPrefix = expectedPass.substring(0, 16.coerceAtMost(expectedPass.length))

            KdfTestHarness.waitForPasswordSubstring(composeTestRule, expectedPrefix)
        }
    }

    @Test
    fun generatesExpectedPassword_withSha512Hasher() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            val configName = "SHA-512 Config"
            val inputPhrase = "Hello World"
            val hasher = InputHasher.SHA512
            val encoder = StringPassEncoder.HexPassEncoder

            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = configName,
                hasher = hasher,
                encoder = encoder,
            )

            KdfTestHarness.enterInputPhrase(composeTestRule, inputPhrase)

            val expectedPass = KdfTestHarness.computeExpectedPassword(
                configName = configName,
                input = inputPhrase,
                hasher = hasher,
                encoder = encoder,
            )
            val expectedPrefix = expectedPass.substring(0, 16.coerceAtMost(expectedPass.length))

            KdfTestHarness.waitForPasswordSubstring(composeTestRule, expectedPrefix)
        }
    }

    @Test
    fun generatesExpectedPassword_withPreprocessingOptions() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            val configName = "Preprocessed SHA256"
            val rawInput = "  HELLO WORLD  "
            val hasher = InputHasher.SHA256
            val encoder = StringPassEncoder.HexPassEncoder

            val preprocessConfig = PreprocessConfig(
                trimLeadingAndTrailingSpaces = true,
                collapseMultipleSpaces = false,
                convertToLowercase = true,
            )

            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = configName,
                hasher = hasher,
                encoder = encoder,
                toggleTrimSpaces = true,
                toggleLowercase = true,
            )

            KdfTestHarness.enterInputPhrase(composeTestRule, rawInput)

            val expectedPass = KdfTestHarness.computeExpectedPassword(
                configName = configName,
                input = rawInput,
                hasher = hasher,
                encoder = encoder,
                preprocessConfig = preprocessConfig,
            )
            val expectedPrefix = expectedPass.substring(0, 16.coerceAtMost(expectedPass.length))

            KdfTestHarness.waitForPasswordSubstring(composeTestRule, expectedPrefix)
        }
    }

    @Test
    fun generatesExpectedPassword_withBip39Encoder() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            val configName = "BIP39 Config"
            val inputPhrase = "Hello World"
            val hasher = InputHasher.SHA256
            val encoder = SeedPassEncoder.Bip39L12PassEncoder

            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = configName,
                hasher = hasher,
                encoder = encoder,
            )

            KdfTestHarness.enterInputPhrase(composeTestRule, inputPhrase)

            val expectedPass = KdfTestHarness.computeExpectedPassword(
                configName = configName,
                input = inputPhrase,
                hasher = hasher,
                encoder = encoder,
            )
            val firstTwoWords = expectedPass.split(" ").take(2).joinToString(" ")

            KdfTestHarness.waitForPasswordSubstring(composeTestRule, firstTwoWords)
        }
    }

    @Test
    fun togglesPasswordVisibility_betweenMaskedAndPlaintext() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            val configName = "Visibility Test Config"
            val inputPhrase = "Secret Input"
            val hasher = InputHasher.SHA256
            val encoder = StringPassEncoder.HexPassEncoder

            KdfTestHarness.addConfig(
                rule = composeTestRule,
                configName = configName,
                hasher = hasher,
                encoder = encoder,
            )

            KdfTestHarness.enterInputPhrase(composeTestRule, inputPhrase)

            val expectedPass = KdfTestHarness.computeExpectedPassword(
                configName = configName,
                input = inputPhrase,
                hasher = hasher,
                encoder = encoder,
            )

            // Wait until generated and revealed
            KdfTestHarness.waitForPasswordSubstring(composeTestRule, expectedPass)

            // Hide password by clicking 'Hide'
            composeTestRule.onNodeWithContentDescription("Hide").performClick()

            // Plaintext should not be visible anymore
            composeTestRule.onNodeWithText(expectedPass).assertDoesNotExist()

            // Reveal password again by clicking 'Show'
            composeTestRule.onNodeWithContentDescription("Show").performClick()

            // Plaintext should be visible again
            composeTestRule.onNodeWithText(expectedPass).assertIsDisplayed()
        }
    }
}
