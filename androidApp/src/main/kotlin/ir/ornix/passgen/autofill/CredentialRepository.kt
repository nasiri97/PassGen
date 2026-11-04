package ir.ornix.passgen.autofill

import ir.ornix.passgen.core.domain.credential.GetAllCredentialsUseCase
import ir.ornix.passgen.core.model.Credential
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URI

object CredentialRepository : KoinComponent {

    private val getAllCredentialsUseCase: GetAllCredentialsUseCase by inject()

    suspend fun findMatches(
        packageName: String?,
        webDomain: String?,
        extractedKeywords: Set<String> = emptySet()
    ): List<Credential> {
        val entries = getAllCredentialsUseCase().first()
        if (entries.isEmpty()) return emptyList()

        val scoredEntries = entries.mapNotNull { credential ->
            var score = 0

            // 1) Web domain matching
            if (webDomain != null && credential.associatedWebDomains.any { it.hasDomain(webDomain) }) {
                score += 10
            }

            // 2) Native App Package Name matching
            if (packageName != null) {
                if (packageName in credential.associatedPackageNames) {
                    score += 10
                } else if (credential.associatedPackageNames.any { pkg ->
                        pkg.contains(packageName, ignoreCase = true) || packageName.contains(
                            pkg,
                            ignoreCase = true
                        )
                    }) {
                    score += 8
                }
            }

            // 3) Keyword matching against associatedKeywords
            if (credential.associatedKeywords.isNotEmpty()) {
                val keywordMatchCount = credential.associatedKeywords.count { kw ->
                    val cleanKw = kw.trim().lowercase()
                    if (cleanKw.isEmpty()) false
                    else {
                        extractedKeywords.contains(cleanKw) ||
                                extractedKeywords.any { token ->
                                    token.contains(cleanKw) || cleanKw.contains(
                                        token
                                    )
                                } ||
                                (packageName != null && packageName.lowercase()
                                    .contains(cleanKw)) ||
                                (webDomain != null && webDomain.lowercase().contains(cleanKw))
                    }
                }
                if (keywordMatchCount > 0) {
                    score += (keywordMatchCount * 5)
                }
            }

            // 4) Title or description keyword match against extractedKeywords
            val titleLower = credential.title?.lowercase().orEmpty()
            val descLower = credential.description?.lowercase().orEmpty()
            if (titleLower.isNotBlank() || descLower.isNotBlank()) {
                val titleMatch = extractedKeywords.any { token ->
                    token.length >= 3 && (titleLower.contains(token) || token.contains(titleLower))
                }
                if (titleMatch) {
                    score += 3
                }
            }

            if (score > 0) credential to score else null
        }

        return scoredEntries
            .sortedByDescending { it.second }
            .map { it.first }
    }

    private fun String.hasDomain(domain: String): Boolean {
        val normalizedUrl = if ("://" in this) this else "https://$this"

        val host = runCatching { URI(normalizedUrl).host }
            .getOrNull()
            ?.lowercase()
            ?: return false

        val normalizedDomain = domain.lowercase().removePrefix(".")

        return host == normalizedDomain || host.endsWith(".$normalizedDomain")
    }
}
