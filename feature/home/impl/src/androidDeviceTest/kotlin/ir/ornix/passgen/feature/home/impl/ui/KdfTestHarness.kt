package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.russhwolf.settings.MapSettings
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.HMAC
import dev.whyoleg.cryptography.algorithms.SHA512
import ir.ornix.passgen.core.common.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.core.common.codec.Base64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.codec.Z85BinaryCodec
import ir.ornix.passgen.core.common.hashing.Sha512Hasher
import ir.ornix.passgen.core.common.isAndroidDebugBuild
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.common.util.withLineBreakOpportunities
import ir.ornix.passgen.core.data.SettingsPassGenConfigRepository
import ir.ornix.passgen.core.domain.AccountRepository
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.SigningKeyNotFoundException
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
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

class TestHmacSigner : HmacSigner {
    private val masterKeyDigests = mutableMapOf<String, ByteArray>()
    private val sha512Hasher = Sha512Hasher()

    override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray) {
        masterKeyDigests[mkdId] = sha512Hasher.digest(rawMasterKey)
    }

    override suspend fun sign(mkdId: String, input: String): ByteArray {
        val hashedKey = masterKeyDigests[mkdId] ?: throw SigningKeyNotFoundException(mkdId)

        val hmacAlgorithm = CryptographyProvider.Default.get(HMAC)
        val keyDecoder = hmacAlgorithm.keyDecoder(SHA512)
        val importedKey = keyDecoder.decodeFromByteArray(HMAC.Key.Format.RAW, hashedKey)
        return importedKey.signatureGenerator().generateSignature(input.encodeToByteArray())
    }

    override suspend fun hasMasterKeyDigest(mkdId: String): Boolean =
        masterKeyDigests.containsKey(mkdId)

    override suspend fun deleteMasterKeyDigest(mkdId: String) {
        masterKeyDigests.remove(mkdId)
    }
}

class KnownVector(
    val masterKey: String,
    val input: String,
    val expectedSha256: ByteArray,
    val expectedSha512: ByteArray,
    val expectedBcrypt: ByteArray,
    val expectedArgon2id: ByteArray
) {
    suspend fun expectedPrefixFor(hasher: InputHasher, encoder: PassEncoder): String {
        val rawBytes = when (hasher) {
            InputHasher.ARGON2ID -> expectedArgon2id
            InputHasher.BCrypt -> expectedBcrypt
            InputHasher.SHA512 -> expectedSha512
            InputHasher.SHA256 -> expectedSha256
        }
        val encoded = when (encoder) {
            StringPassEncoder.HexPassEncoder -> KdfTestHarness.hexCodec.encode(rawBytes)
            StringPassEncoder.Base64PassEncoder -> KdfTestHarness.base64Codec.encode(rawBytes)
            StringPassEncoder.Z85PassEncoder -> KdfTestHarness.z85Codec.encode(rawBytes)
            else -> KdfTestHarness.z85Codec.encode(rawBytes)
        }
        return encoded.substring(0, 16.coerceAtMost(encoded.length))
    }
}

object KdfTestHarness {

    const val MASTER_KEY_1 = "MASTER_KEY_NUMBER1_ABCDabcd_1234$*?!"
    const val MASTER_KEY_2 = "MASTER_KEY_NUMBER2_ABCDabcd_1234$*?!"

    const val INPUT_1 = ""
    const val INPUT_2 = "Hello World"

    const val GENERATION_TIMEOUT_MS = 60_000L

    val base64Codec = Base64BinaryCodec()
    val bCryptCodec = BCryptBase64BinaryCodec()
    val hexCodec = HexBinaryCodec(false)
    val z85Codec = Z85BinaryCodec()

    private suspend fun createKnownVector(
        masterKey: String,
        input: String,
        expectedSha256: String,
        expectedSha512: String,
        expectedBcryptMcfEncoded: String,
        expectedArgon2id: String
    ) = KnownVector(
        masterKey = masterKey,
        input = input,
        expectedSha256 = hexCodec.decode(expectedSha256),
        expectedSha512 = hexCodec.decode(expectedSha512),
        expectedBcrypt = bCryptCodec.decode(
            expectedBcryptMcfEncoded.substring(29)
        ),
        expectedArgon2id = base64Codec.decode(
            expectedArgon2id.substring(
                expectedArgon2id.lastIndexOf(
                    '$'
                ) + 1
            )
        )
    )

    suspend fun knownVector1() = createKnownVector(
        masterKey = MASTER_KEY_1,
        input = INPUT_1,
        expectedSha256 = "a577b2d56dfaa9ef6f41757e71044399bf778161718106c47d28d08ee15862f3",
        expectedSha512 = "e12c3072d4dc896a983a011c3a6607e194d82def65ce767a1d4b43c56ef4b921c0780a98af739a20fec2d5f4e9568998ed9ce7a4eef32dd0275c2cd0bd6b9062",
        expectedBcryptMcfEncoded = $$"$2b$12$nVcwzU14oc7tOVT8aOPBkOTAenggmnwEZ05x0I2N6XR6KAl/tlEdy",
        expectedArgon2id = $$"$argon2id$v=19$m=131072,t=4,p=1$pXey1W36qe9vQXV+cQRDmQ$4LmaKI3VBU8RMcBlZZb5P7upnXDrfrW9iBlBS+85cqmPa+kolG6c3kkIXlH5cj+AFjMq5Dd5uTjti5w7+E3eCA"
    )

    suspend fun knownVector2() = createKnownVector(
        masterKey = MASTER_KEY_1,
        input = INPUT_2,
        expectedSha256 = "b1cd8f765ea7099bd301214cfdc158a6177637181ffdb4ea8845770f93a22d4a",
        expectedSha512 = "1f5488591763277ffc5dda6ffd07ae07f7da2506259f1180774a43cb55cda18c98402ff3a2855c897d87ae9ec5fe58e23fb67c228f6c83dc08bb7abc96b76818",
        expectedBcryptMcfEncoded = $$"$2b$12$qa0Nbj4lAXtR.QDK9aDWneA6ZqTMCkn237knCS7vQ/7ramWhP/3dK",
        expectedArgon2id = $$"$argon2id$v=19$m=131072,t=4,p=1$sc2Pdl6nCZvTASFM/cFYpg$L2BWAdIzwctqbgz7vx9qxbV+oX3YIoTCfUMrdP8Z3TUERxeL7RpIs5RHkyakgYr/ZSDwJHLuW+aLOxL7aAiF2A"
    )

    suspend fun knownVector3() = createKnownVector(
        masterKey = MASTER_KEY_2,
        input = INPUT_1,
        expectedSha256 = "75d2d64f69c907ecc8035df9eb4f4a9f835be281ec3419d149d35c4b314fd787",
        expectedSha512 = "39cd14bbf79addb2fbaa88f1a479b41cecaac17d1ba858974fed236dcdbd540d40aa4792baef4d53cb30584659e5c349e8c2cd066df50404048fa8d99827b1d5",
        expectedBcryptMcfEncoded = $$"$2b$12$bbJUR0lH/8xG.z134y7Ilubmjk40c01yBqaP3Mkb8PjFwEQdXxSgS",
        expectedArgon2id = $$"$argon2id$v=19$m=131072,t=4,p=1$ddLWT2nJB+zIA135609Knw$8h3g9QfLtVrWdZZtWY+u1fGjRpWOUekTXodqsH2CUBa9o9YZBVnYgM6tHJCI7LJvdEURr3gBC78A44Hmb5qUKQ"
    )

    suspend fun knownVector4() = createKnownVector(
        masterKey = MASTER_KEY_2,
        input = INPUT_2,
        expectedSha256 = "f13760847a971fe11ea5425b7e9f0e685f4c406980963a2d6b039b2980a8a1b0",
        expectedSha512 = "31a31910e0b7b4c449128efe72997b9934e03c520dd76bb9bf1a9513f885edd28e564f5e2cebc44b6d71f5f3398f27b451fe23d581a80cd7e70648f0e360c41c",
        expectedBcryptMcfEncoded = $$"$2b$12$6RbefFoVF8CcnSHZdn6MY.n2PKDvt/H7LqS.nsMN72uNFzo9VW09e",
        expectedArgon2id = $$"$argon2id$v=19$m=131072,t=4,p=1$8TdghHqXH+EepUJbfp8OaA$7EQDsCVR9Yna8ODy3o9aKnAoisD3CTy7wuwxv9Btkdo9wGcRntMeyUrRHVxYnP3pugXgZyvMtCAjmL+OmkbzOQ"
    )

    val testModule = module {
        single { SettingsPassGenConfigRepository(MapSettings()) } bind PassGenConfigRepository::class
        single { TestHmacSigner() } bind HmacSigner::class
        //  single { PlatformHmacSigner } bind HmacSigner::class
        single {
            object : AccountRepository {
                override suspend fun save(account: Account) {}
                override suspend fun delete(accountId: Int) {}
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
            var showAddConfigScreen by remember { mutableStateOf(false) }
            var configScreenKey by remember { mutableStateOf(0) }

            if (showAddConfigScreen) {
                val storeOwner = remember(configScreenKey) {
                    val store = ViewModelStore()
                    object : ViewModelStoreOwner {
                        override val viewModelStore = store
                    }
                }
                DisposableEffect(configScreenKey) {
                    onDispose {
                        storeOwner.viewModelStore.clear()
                    }
                }

                CompositionLocalProvider(LocalViewModelStoreOwner provides storeOwner) {
                    AddKdfConfigScreen(
                        onNavigateBack = { showAddConfigScreen = false },
                        onConfigCreated = { showAddConfigScreen = false },
                    )
                }
            } else {
                HomeScreen(
                    onNavigateToCreateConfig = {
                        configScreenKey++
                        showAddConfigScreen = true
                    },
                )
            }
        }
    }

    fun addConfig(
        rule: ComposeContentTestRule,
        configName: String,
        masterKey: String = MASTER_KEY_1,
        hasher: InputHasher = InputHasher.ARGON2ID,
        encoder: PassEncoder = StringPassEncoder.Z85PassEncoder,
        trimSpaces: Boolean = true,
        collapseSpaces: Boolean = true,
        lowercase: Boolean = true,
        isFirstConfig: Boolean = true,
    ) {
        if (isFirstConfig) {
            rule.onNodeWithText("No password configurations yet.").assertIsDisplayed()
            rule.onNodeWithText("Add your first config").performClick()
        } else {
            rule.onNodeWithContentDescription("Add Config").performClick()
        }

        // Step 0
        rule.onNodeWithText("I have read and understood the information above.").performClick()

        // Step 1: Master Key
        rule.onNodeWithTag("master_key_input").performTextInput(masterKey)
        rule.onNodeWithTag("confirm_master_key_input").performTextInput(masterKey)
        rule.onNodeWithTag("next_button").performClick()

        // Wait for Step 2 to be rendered
        rule.onNodeWithTag("config_name_input").assertIsDisplayed()

        // Step 2: Config Details
        rule.onNodeWithTag("config_name_input").performTextInput(configName)

        // UI defaults are trimSpaces=true, collapseSpaces=true, lowercase=true. Toggle if target differs:
        if (!trimSpaces) {
            rule.onNodeWithText("Trim leading/trailing spaces").performClick()
        }
        if (!collapseSpaces) {
            rule.onNodeWithText("Collapse multiple spaces").performClick()
        }
        if (!lowercase) {
            rule.onNodeWithText("Convert to lowercase").performClick()
        }

        // Select Hasher
        rule.onNodeWithText("Hashing Algorithm").performClick()
        rule.onAllNodesWithText(hasher.fullName).filterToOne(
            hasParent(hasTestTag("hasher_type_selector"))
        ).performClick()

        // Select Encoder
        rule.onNodeWithText("Encoder Type").performClick()
        rule.onAllNodesWithText(encoder.fullName).filterToOne(
            hasParent(hasTestTag("encoder_type_selector"))
        ).performClick()

        rule.onNodeWithTag("scroll_container").performScrollToNode(hasTestTag("submit_button"))
        rule.onNodeWithTag("submit_button").performClick()

        rule.onNodeWithText(configName).assertIsDisplayed()
    }

    fun acknowledgeMasterKeyNotice(rule: ComposeContentTestRule) {
        rule.onNodeWithTag("master_key_ack").performClick()
    }

    fun enterInputPhrase(rule: ComposeContentTestRule, phrase: String) {
        val inputField = rule.onNodeWithText("Input Phrase")
        inputField.performTextReplacement(phrase)
    }

    fun waitForPasswordSubstring(
        rule: ComposeContentTestRule,
        configName: String,
        expectedSubstring: String,
        timeoutMs: Long = GENERATION_TIMEOUT_MS
    ) {
        // Wait until the password calculation completes
        rule.waitUntil(timeoutMs) {
            try {
                // Reveal password
                togglePasswordVisibility(
                    configName = configName,
                    isVisible = true,
                    rule = rule
                )

                // Password assertion
                getPasswordNode(
                    rule = rule,
                    configName = configName,
                    expectedPass = expectedSubstring,
                    substring = true
                ).assertIsDisplayed()

                true
            } catch (_: AssertionError) {
                false
            }
        }
    }


    fun getPasswordNode(
        rule: ComposeContentTestRule,
        configName: String,
        expectedPass: String,
        substring: Boolean = false
    ) = rule.onAllNodesWithText(
        text = expectedPass
            .toCharArray()
            .withLineBreakOpportunities()
            .concatToString(),
        substring = substring,
        useUnmergedTree = true
    ).filterToOne(
        hasPassGenItemCardParent(configName)
    )


    fun hasPassGenItemCardParent(configName: String): SemanticsMatcher =
        hasAnyAncestor(
            hasTestTag("pass_gen_item_card") and
                    hasAnyDescendant(
                        hasText(configName)
                    )
        )

    fun togglePasswordVisibility(
        configName: String,
        isVisible: Boolean,
        rule: ComposeContentTestRule
    ) {
        val button = rule.onAllNodesWithContentDescription(
            if (isVisible) "Show" else "Hide",
            useUnmergedTree = true
        ).filter(hasPassGenItemCardParent(configName))

        if (button.fetchSemanticsNodes().isNotEmpty()) {
            button.onFirst().performClick()
        }
    }

    suspend fun computeExpectedPassword(
        configId: Int = 1,
        configName: String = "Test Config",
        masterKey: String = MASTER_KEY_1,
        input: String,
        hasher: InputHasher = InputHasher.ARGON2ID,
        encoder: PassEncoder = StringPassEncoder.Z85PassEncoder,
        preprocessConfig: PreprocessConfig = PreprocessConfig(
            trimLeadingAndTrailingSpaces = true,
            collapseMultipleSpaces = true,
            convertToLowercase = true
        ),
        passwordLength: Int?
    ): String {

        val kdfConfig = KdfPassGenConfig(
            id = configId,
            name = configName,
            preprocessConfig = preprocessConfig,
            inputHasher = hasher,
            passEncoder = encoder,
            passwordLength = passwordLength
        )

        val hmacSigner: HmacSigner = GlobalContext.get().get()
        hmacSigner.registerKey(configId.toString(), masterKey.encodeToByteArray())

        val useCase = GenerateKDFPassUseCase(
            configRepo = object : PassGenConfigRepository {
                override fun getAll(): Flow<List<PassGenConfig>> = flowOf(emptyList())
                override suspend fun add(config: PassGenConfig): Int = config.id
                override suspend fun removeById(configId: Int) {}
            },
            hmacSigner = hmacSigner,
        )

        val pass = useCase(kdfConfig, input)
        return pass?.value?.concatToString() ?: ""
    }
}
