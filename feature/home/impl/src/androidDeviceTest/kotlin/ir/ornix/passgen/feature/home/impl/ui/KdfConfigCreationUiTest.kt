package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest

class KdfConfigCreationUiTest : KoinTest {

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
    fun emptyHomeScreen_displaysNoConfigsYet_andAddButton() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            composeTestRule.onNodeWithText("No password configurations yet.").assertIsDisplayed()
            composeTestRule.onNodeWithText("Add your first config").assertIsDisplayed()
        }
    }

    @Test
    fun masterKeyStep_disablesNextButton_whenMasterKeyIsTooShort() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            composeTestRule.onNodeWithText("Add your first config").performClick()

            // Short key (< 24 characters)
            val shortKey = "ShortKey123!"
            composeTestRule.onNodeWithTag("master_key_input").performTextInput(shortKey)
            composeTestRule.onNodeWithTag("confirm_master_key_input").performTextInput(shortKey)

            // Next button must be disabled
            composeTestRule.onNodeWithTag("next_button").assertIsNotEnabled()
        }
    }

    @Test
    fun masterKeyStep_disablesNextButton_whenKeysDoNotMatch() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            composeTestRule.onNodeWithText("Add your first config").performClick()

            val key1 = KdfTestHarness.MASTER_KEY_1
            val key2 = KdfTestHarness.MASTER_KEY_2

            composeTestRule.onNodeWithTag("master_key_input").performTextInput(key1)
            composeTestRule.onNodeWithTag("confirm_master_key_input").performTextInput(key2)

            // Error text displayed
            composeTestRule.onNodeWithText("Master keys do not match").assertIsDisplayed()

            // Next button must be disabled
            composeTestRule.onNodeWithTag("next_button").assertIsNotEnabled()
        }
    }

    @Test
    fun masterKeyStep_enablesNextButton_whenMasterKeyIsValid() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            composeTestRule.onNodeWithText("Add your first config").performClick()

            val validKey = KdfTestHarness.MASTER_KEY_1

            composeTestRule.onNodeWithTag("master_key_input").performTextInput(validKey)
            composeTestRule.onNodeWithTag("confirm_master_key_input").performTextInput(validKey)

            // Next button must be enabled
            composeTestRule.onNodeWithTag("next_button").assertIsEnabled()
        }
    }

    @Test
    fun configDetailsStep_disablesSubmitButton_whenConfigNameIsEmpty() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            composeTestRule.onNodeWithText("Add your first config").performClick()

            val validKey = KdfTestHarness.MASTER_KEY_1
            composeTestRule.onNodeWithTag("master_key_input").performTextInput(validKey)
            composeTestRule.onNodeWithTag("confirm_master_key_input").performTextInput(validKey)
            composeTestRule.onNodeWithTag("next_button").performClick()

            // Step 2 reached: Name is empty initially
            composeTestRule.onNodeWithTag("submit_button").assertIsNotEnabled()

            // Type a name
            composeTestRule.onNodeWithTag("config_name_input").performTextInput("My Config")

            // Submit button becomes enabled
            composeTestRule.onNodeWithTag("submit_button").assertIsEnabled()
        }
    }

    @Test
    fun cancelConfigCreation_navigatesBackToHomeScreen() {
        runBlocking {
            KdfTestHarness.setContent(composeTestRule)

            composeTestRule.onNodeWithText("Add your first config").performClick()

            // Click back button on top app bar
            composeTestRule.onNodeWithContentDescription("Back").performClick()

            // Returns to empty home screen
            composeTestRule.onNodeWithText("No password configurations yet.").assertIsDisplayed()
        }
    }
}
