package ir.ornix.passgen.autofill

import android.util.Log
import ir.ornix.passgen.core.domain.credential.GetAllCredentialsUseCase
import ir.ornix.passgen.core.model.Credential
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URI

object CredentialRepository : KoinComponent {

    private val getAllCredentialsUseCase: GetAllCredentialsUseCase by inject()

    /**
     * Browsers report their own package name, so for web pages match ONLY on domain.
     * For native apps match on package name.
     */
    suspend fun findMatches(
        packageName: String?,
        webDomain: String?,
        keyword: String?
    ): List<Credential> {
        val entries = getAllCredentialsUseCase().first()
        return entries.filter { e ->
            if (webDomain != null) {
                e.associatedWebDomains.any { it.hasDomain(webDomain) }
            } else if (packageName != null) {
                packageName in e.associatedPackageNames
            } else {
                keyword != null && keyword in e.associatedKeywords
            }
        }
    }

    fun String.hasDomain(domain: String): Boolean {
        val normalizedUrl = if ("://" in this) this else "https://$this"

        val host = runCatching { URI(normalizedUrl).host }
            .getOrNull()
            ?.lowercase()
            ?: return false

        val normalizedDomain = domain.lowercase().removePrefix(".")

        return host == normalizedDomain ||
                host.endsWith(".$normalizedDomain")
    }
}