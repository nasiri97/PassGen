package ir.ornix.passgen.core.logging

import kotlin.test.Test
import kotlin.test.assertTrue

class LoggerTest {

    @Test
    fun testDebugLogging() {
        Logger.d("Test debug message")
        Logger.d("Test debug with tag", tag = "CustomTag")
        assertTrue(true)
    }

    @Test
    fun testInfoLogging() {
        Logger.i("Test info message")
        Logger.i("Test info with tag", tag = "CustomTag")
        assertTrue(true)
    }

    @Test
    fun testWarnLogging() {
        Logger.w("Test warning message")
        Logger.w("Test warning with tag", tag = "CustomTag")
        assertTrue(true)
    }

    @Test
    fun testErrorLogging() {
        Logger.e("Test error message")
        Logger.e("Test error message with exception", Throwable("Test Exception"))
        Logger.e("Test error with tag", Throwable("Test Exception"), tag = "CustomTag")
        assertTrue(true)
    }
}
