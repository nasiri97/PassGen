package ir.ornix.passgen.core.ui.security

import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.isSensitiveData
import androidx.compose.ui.semantics.password
import androidx.compose.ui.semantics.semantics
import ir.ornix.passgen.core.common.isDebugBuild

/**
 * Display-only secrets (Text, or a Column of Texts).
 * Clears the semantics of everything inside, so don't put buttons or fields in it.
 */
fun Modifier.secureContent(contentDescription: String): Modifier =
    if (isDebugBuild) this.semantics {
        this.contentDescription = contentDescription
    }
    else clearAndSetSemantics {
        isSensitiveData = true
        this.contentDescription = contentDescription
    }

/**
 * Text fields (TextField / OutlinedTextField / BasicTextField).
 * Merges with the field's own semantics, so typing, focus and TalkBack editing still work.
 */
fun Modifier.secureInput(contentDescription: String, isCredential: Boolean = true): Modifier =
    if (isDebugBuild) this.semantics {
        this.contentDescription = contentDescription
    }
    else semantics {
        isSensitiveData = true
        this.contentDescription = contentDescription

        if (isCredential) {
            contentType = ContentType.Password
            password()
        }
    }