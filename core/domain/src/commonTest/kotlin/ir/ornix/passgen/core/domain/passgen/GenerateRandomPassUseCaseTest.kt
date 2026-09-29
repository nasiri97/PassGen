package ir.ornix.passgen.core.domain.passgen

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class GenerateRandomPassUseCaseTest {

    private lateinit var useCase: GenerateRandomPassUseCase

    @BeforeTest
    fun setup() {
        useCase = GenerateRandomPassUseCase()
    }

    // -------------------------------------------------------------------------
    // Valid Password Length Generation Tests
    // -------------------------------------------------------------------------

    @Test
    fun `invoke with valid password length 1 should return password of length 1`() = runTest {
        val password = useCase(passwordLength = 1)
        assertEquals(1, password.length)
    }

    @Test
    fun `invoke with valid password length 16 should return password of length 16`() = runTest {
        val password = useCase(passwordLength = 16)
        assertEquals(16, password.length)
    }

    @Test
    fun `invoke with valid password length 32 should return password of length 32`() = runTest {
        val password = useCase(passwordLength = 32)
        assertEquals(32, password.length)
    }

    @Test
    fun `invoke with maximum valid password length 84 should return password of length 84`() =
        runTest {
            val password = useCase(passwordLength = 84)
            assertEquals(84, password.length)
        }

    // -------------------------------------------------------------------------
    // Invalid Password Length Validation Tests
    // -------------------------------------------------------------------------

    @Test
    fun `invoke with zero password length should throw IllegalArgumentException`() = runTest {
        val exception = assertFailsWith<IllegalArgumentException> {
            useCase(passwordLength = 0)
        }
        assertEquals("Password length must be greater than 0.", exception.message)
    }

    @Test
    fun `invoke with negative password length should throw IllegalArgumentException`() = runTest {
        val exception = assertFailsWith<IllegalArgumentException> {
            useCase(passwordLength = -5)
        }
        assertEquals("Password length must be greater than 0.", exception.message)
    }

    @Test
    fun `invoke with password length exceeding maximum token length 84 should throw IllegalArgumentException`() =
        runTest {
            val exception = assertFailsWith<IllegalArgumentException> {
                useCase(passwordLength = 85)
            }
            assertEquals(
                exception.message?.contains("Requested Password length must not exceed 84"),
                true,
                "Expected exception message to state max length exceed limit, got: ${exception.message}"
            )
        }

    // -------------------------------------------------------------------------
    // Randomness, Uniqueness, and Character Set Tests
    // -------------------------------------------------------------------------

    @Test
    fun `consecutive calls should generate different random passwords`() = runTest {
        val pass1 = useCase(passwordLength = 32)
        val pass2 = useCase(passwordLength = 32)
        val pass3 = useCase(passwordLength = 32)

        assertNotEquals(pass1, pass2, "Consecutive calls should produce distinct passwords")
        assertNotEquals(pass2, pass3, "Consecutive calls should produce distinct passwords")
        assertNotEquals(pass1, pass3, "Consecutive calls should produce distinct passwords")
    }

    @Test
    fun `generated password should only contain valid Base64 characters`() = runTest {
        val base64Regex = Regex("^[A-Za-z0-9+/=]+$")
        val password = useCase(passwordLength = 64)

        assertTrue(
            base64Regex.matches(password),
            "Generated password '$password' should match Base64 character set"
        )
    }
}
