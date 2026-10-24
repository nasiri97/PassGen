package ir.ornix.passgen.core.model

import kotlinx.serialization.Serializable

/**
 * One saved login. Properties fall into three groups:
 *  1. Identity      -> [id], [title], [description]
 *  2. Values to fill -> [password], [username], [email], [phone]
 *  3. Matching rules -> [associatedPackageNames] (native apps), [associatedWebDomains] (websites), [associatedKeywords]
 */
@Serializable
data class Credential(
    val id: Long = 0,
    val title: String? = null,
    val description: String? = null,
    val password: Password,
    val username: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val associatedPackageNames: Set<String> = emptySet(),
    val associatedWebDomains: Set<String> = emptySet(),
    val associatedKeywords: Set<String> = emptySet()
) {

    val passwordValue = password.value
    val passwordEntropyByteSize = password.entropyByteSize

}
