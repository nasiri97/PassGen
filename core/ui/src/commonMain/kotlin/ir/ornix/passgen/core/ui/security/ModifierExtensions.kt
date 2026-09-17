package ir.ornix.passgen.core.ui.security

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isSensitiveData


/**
 *
 * clearAndSetSemantics clears the semantics of all descendant nodes in the actual UI tree — meaning every composable
 * that gets called (nested) inside Screen, no matter how deeply nested,
 * and regardless of whether you threaded a modifier parameter into those functions.
 * The semantics tree mirrors the composition/layout hierarchy (which composable called which),
 * not the modifier chain arguments.
 *
 * The one real gap is when a composable creates a separate composition root/window rather than being a structural descendant:
 *
 * - Dialog { ... }
 * - Popup { ... }
 * - AndroidView embedding a separate View hierarchy
 * - Another Activity, or a separately hosted ComposeView
 *
 * If you show sensitive content in a Dialog,
 * you'd need to apply clearAndSetSemantics inside that dialog's own content too — the parent's clearing won't reach across the window boundary.
 */
fun Modifier.secureContent() = this.clearAndSetSemantics {
    // Marks this field as sensitive to prevent accessibility extraction
    isSensitiveData = true
    contentDescription = "Secure content"
}