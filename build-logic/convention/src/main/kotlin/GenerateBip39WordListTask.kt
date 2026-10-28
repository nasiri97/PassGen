import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.security.MessageDigest

@CacheableTask
abstract class GenerateBip39WordListTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val wordListFile: RegularFileProperty

    @get:Input
    abstract val expectedSha256: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val input = wordListFile.get().asFile
        val bytes = input.readBytes()
        val content = bytes.decodeToString()

        // 0. Normalize line endings (CRLF and CR -> LF).
        val normalizedContent = content
            .replace("\r\n", "\n")
            .replace("\r", "\n")

        val normalizedBytes = normalizedContent.toByteArray(Charsets.UTF_8)


        // 1. Verify the normalized official file.
        val actualSha256 = MessageDigest.getInstance("SHA-256")
            .digest(normalizedBytes)
            .joinToString("") { "%02x".format(it.toInt() and 0xFF) }


        require(actualSha256.equals(expectedSha256.get(), ignoreCase = true)) {
            """
            BIP-39 English word list checksum mismatch!
            Expected: ${expectedSha256.get()}
            Actual:   $actualSha256
            File:     ${input.absolutePath}
            """.trimIndent()
        }

        // 2. Parse and validate
        val words = normalizedContent
            .lines()
            .map(String::trim)
            .filter(String::isNotEmpty)

        require(words.size == WORD_COUNT) {
            "BIP-39 word list must contain exactly $WORD_COUNT words. Found: ${words.size}"
        }
        require(words.distinct().size == words.size) {
            "BIP-39 word list contains duplicate words."
        }
        require(words.all { w -> w.all { it in 'a'..'z' } }) {
            "BIP-39 word list must contain lowercase ASCII words only."
        }

        // 3. Generate Kotlin source
        val pkg = packageName.get()
        val outFile = outputDir.get().asFile
            .resolve(pkg.replace('.', '/'))
            .resolve("Bip39EnglishWordList.kt")
        outFile.parentFile.mkdirs()

        outFile.writeText(
            buildString {
                appendLine("package $pkg")
                appendLine()
                appendLine("/**")
                appendLine(" * Official BIP-39 English word list.")
                appendLine(" *")
                appendLine(" * SHA-256: $actualSha256")
                appendLine(" * Generated from: ${input.name}")
                appendLine(" *")
                appendLine(" * DO NOT EDIT MANUALLY.")
                appendLine(" */")
                appendLine("internal val BIP39_ENGLISH_WORDS: Array<String> = arrayOf(")
                words.forEach { appendLine("    \"$it\",") }
                appendLine(")")
            }
        )

        logger.lifecycle("Generated BIP-39 word list: ${outFile.absolutePath}")
    }

    private companion object {
        const val WORD_COUNT = 2048
    }
}