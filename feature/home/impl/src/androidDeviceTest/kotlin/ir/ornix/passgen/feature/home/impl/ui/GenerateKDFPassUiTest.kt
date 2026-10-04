package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import ir.ornix.passgen.feature.home.impl.ui.KdfTestHarness.togglePasswordVisibility
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

    private fun verifyKnownTestCaseAcrossHashers(
        testCaseProvider: suspend () -> KnownVector,
        testNamePrefix: String
    ) {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)
            val testCase = testCaseProvider()

            val configs = listOf(
                Triple(
                    "$testNamePrefix Argon2id",
                    InputHasher.ARGON2ID,
                    StringPassEncoder.Z85PassEncoder
                ),
                Triple(
                    "$testNamePrefix BCrypt",
                    InputHasher.BCrypt,
                    StringPassEncoder.Base64PassEncoder
                ),
                Triple(
                    "$testNamePrefix SHA-512",
                    InputHasher.SHA512,
                    StringPassEncoder.HexPassEncoder
                ),
                Triple(
                    "$testNamePrefix SHA-256",
                    InputHasher.SHA256,
                    StringPassEncoder.HexPassEncoder
                ),
            )

            configs.forEachIndexed { index, (configName, hasher, encoder) ->
                KdfTestHarness.addConfig(
                    rule = composeTestRule,
                    configName = configName,
                    masterKey = testCase.masterKey,
                    hasher = hasher,
                    encoder = encoder,
                    lowercase = false,
                    isFirstConfig = index == 0,
                )
            }

            KdfTestHarness.enterInputPhrase(composeTestRule, testCase.input)

            configs.forEach { (configName, hasher, encoder) ->
                val expectedPrefix = testCase.expectedPrefixFor(hasher, encoder)
                KdfTestHarness.waitForPasswordSubstring(
                    rule = composeTestRule,
                    configName = configName,
                    expectedSubstring = expectedPrefix
                )
            }
        }
    }

    @Test
    fun generatesExpectedPassword_knownVector1_allHashers_masterKey1_emptyInput() {
        verifyKnownTestCaseAcrossHashers(
            testCaseProvider = { KdfTestHarness.knownVector1() },
            testNamePrefix = "V1",
        )
    }

    @Test
    fun generatesExpectedPassword_knownVector2_allHashers_masterKey1_helloWorldInput() {
        verifyKnownTestCaseAcrossHashers(
            testCaseProvider = { KdfTestHarness.knownVector2() },
            testNamePrefix = "V2",
        )
    }

    @Test
    fun generatesExpectedPassword_knownVector3_allHashers_masterKey2_emptyInput() {
        verifyKnownTestCaseAcrossHashers(
            testCaseProvider = { KdfTestHarness.knownVector3() },
            testNamePrefix = "V3",
        )
    }

    @Test
    fun generatesExpectedPassword_knownVector4_allHashers_masterKey2_helloWorldInput() {
        verifyKnownTestCaseAcrossHashers(
            testCaseProvider = { KdfTestHarness.knownVector4() },
            testNamePrefix = "V4",
        )
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
                trimSpaces = true,
                collapseSpaces = false,
                lowercase = true,
            )

            KdfTestHarness.enterInputPhrase(composeTestRule, rawInput)

            val expectedPass = KdfTestHarness.computeExpectedPassword(
                configName = configName,
                input = rawInput,
                hasher = hasher,
                encoder = encoder,
                preprocessConfig = preprocessConfig,
                passwordLength = 24
            )
            val expectedPrefix = expectedPass.substring(0, 16.coerceAtMost(expectedPass.length))

            KdfTestHarness.waitForPasswordSubstring(
                rule = composeTestRule,
                configName = configName,
                expectedSubstring = expectedPrefix
            )
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
                passwordLength = null
            )
            val firstTwoWords = expectedPass.split(" ").take(2).joinToString(" ")

            KdfTestHarness.waitForPasswordSubstring(
                rule = composeTestRule,
                configName = configName,
                expectedSubstring = firstTwoWords
            )
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
                passwordLength = 16
            )

            // Assert password
            KdfTestHarness.waitForPasswordSubstring(
                rule = composeTestRule,
                configName = configName,
                expectedSubstring = expectedPass
            )

            // Hide password
            togglePasswordVisibility(
                configName = configName,
                isVisible = false,
                rule = composeTestRule
            )

            // Plaintext should not be visible anymore
            composeTestRule.onNodeWithText(expectedPass).assertDoesNotExist()

            // Reveal password
            togglePasswordVisibility(
                configName = configName,
                isVisible = true,
                rule = composeTestRule
            )

            // Plaintext should be visible again
            KdfTestHarness.getPasswordNode(
                rule = composeTestRule,
                configName = configName,
                expectedPass = expectedPass
            ).assertIsDisplayed()
        }
    }
}
