package ir.ornix.passgen.autofill

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.content.Context
import android.text.InputType
import android.view.View
import android.view.autofill.AutofillId

enum class FieldType {
    USERNAME,
    EMAIL,
    PASSWORD,
    PHONE,
    GENERIC_TEXT
}

object StructureParser {

    private class Acc {
        var username: AutofillId? = null
        var email: AutofillId? = null
        var password: AutofillId? = null
        var phone: AutofillId? = null
        val genericIds = mutableListOf<AutofillId>()
        var webDomain: String? = null
        val extractedKeywords = mutableSetOf<String>()
        var primaryFieldWidthPx: Int? = null
    }

    fun parse(structure: AssistStructure, context: Context? = null): ParsedStructure {
        val density = context?.resources?.displayMetrics?.density ?: 2.0f
        val acc = Acc()

        structure.activityComponent?.packageName?.let { pkg ->
            extractTokens(pkg, acc.extractedKeywords)
        }

        for (i in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(i)
            windowNode.title?.let { extractTokens(it.toString(), acc.extractedKeywords) }
            walk(windowNode.rootViewNode, acc)
        }

        val targetWidthDp = acc.primaryFieldWidthPx?.let { px ->
            (px / density).toInt().coerceIn(180, 420)
        } ?: 280

        return ParsedStructure(
            usernameId = acc.username,
            emailId = acc.email,
            passwordId = acc.password,
            phoneId = acc.phone,
            genericIds = acc.genericIds,
            packageName = structure.activityComponent?.packageName,
            webDomain = acc.webDomain,
            extractedKeywords = acc.extractedKeywords,
            targetFieldWidthDp = targetWidthDp
        )
    }

    private fun walk(node: ViewNode, acc: Acc) {
        if (acc.webDomain == null) {
            node.webDomain?.takeIf { it.isNotBlank() }?.let { domain ->
                acc.webDomain = domain
                extractTokens(domain, acc.extractedKeywords)
            }
        }

        // Collect text tokens from node metadata for keyword matching
        listOfNotNull(
            node.idEntry,
            node.hint,
            node.contentDescription,
            node.text?.takeIf { it.length <= 60 }
        ).forEach { text ->
            extractTokens(text.toString(), acc.extractedKeywords)
        }

        node.htmlInfo?.attributes?.forEach { pair ->
            pair.second?.let { extractTokens(it, acc.extractedKeywords) }
        }

        val id = node.autofillId
        if (id != null && node.visibility == View.VISIBLE && (node.autofillType == View.AUTOFILL_TYPE_TEXT || node.isFocused)) {
            val classified = classify(node)
            when (classified) {
                FieldType.USERNAME -> if (acc.username == null) acc.username = id
                FieldType.EMAIL -> if (acc.email == null) acc.email = id
                FieldType.PASSWORD -> if (acc.password == null) acc.password = id
                FieldType.PHONE -> if (acc.phone == null) acc.phone = id
                FieldType.GENERIC_TEXT -> if (acc.genericIds.size < 5) acc.genericIds.add(id)
                null -> Unit
            }

            // Capture target width for primary input field
            if (classified != null && (acc.primaryFieldWidthPx == null || node.isFocused)) {
                val nodeWidth = node.width.takeIf { it > 0 } ?: 0
                if (nodeWidth > 0) {
                    acc.primaryFieldWidthPx = nodeWidth
                }
            }
        }

        for (i in 0 until node.childCount) {
            walk(node.getChildAt(i), acc)
        }
    }

    private fun classify(node: ViewNode): FieldType? {
        // 1) Explicit hints (android:autofillHints) - highest precedence
        node.autofillHints?.forEach { hint ->
            val lowerHint = hint.lowercase()
            when {
                View.AUTOFILL_HINT_USERNAME in lowerHint || "user" in lowerHint || "login" in lowerHint -> return FieldType.USERNAME
                View.AUTOFILL_HINT_PASSWORD in lowerHint || "password" in lowerHint || "pass" in lowerHint -> return FieldType.PASSWORD
                View.AUTOFILL_HINT_EMAIL_ADDRESS in lowerHint || "email" in lowerHint -> return FieldType.EMAIL
                View.AUTOFILL_HINT_PHONE in lowerHint || "phone" in lowerHint || "mobile" in lowerHint -> return FieldType.PHONE
            }
        }

        // 2) HTML attributes (web forms in Chrome/Brave/Edge/WebViews)
        node.htmlInfo?.attributes?.forEach { pair ->
            val key = pair.first
            val valStr = pair.second
            if (valStr != null) {
                val v = valStr.lowercase()
                if (key.equals("type", ignoreCase = true)) {
                    when {
                        "password" in v -> return FieldType.PASSWORD
                        "email" in v -> return FieldType.EMAIL
                        "tel" in v || "phone" in v -> return FieldType.PHONE
                    }
                }
                if (key.equals("autocomplete", ignoreCase = true)) {
                    when {
                        "username" in v -> return FieldType.USERNAME
                        "password" in v -> return FieldType.PASSWORD
                        "email" in v -> return FieldType.EMAIL
                        "tel" in v -> return FieldType.PHONE
                    }
                }
            }
        }

        // 3) inputType flags
        val cls = node.inputType and InputType.TYPE_MASK_CLASS
        val variation = node.inputType and InputType.TYPE_MASK_VARIATION
        if (cls == InputType.TYPE_CLASS_TEXT) {
            when (variation) {
                InputType.TYPE_TEXT_VARIATION_PASSWORD,
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
                InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD -> return FieldType.PASSWORD

                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS -> return FieldType.EMAIL
            }
        }
        if (cls == InputType.TYPE_CLASS_PHONE) return FieldType.PHONE

        // 4) Text heuristics on resource ID, hint text, or content description
        val combined =
            listOfNotNull(node.idEntry, node.hint, node.contentDescription).joinToString(" ")
                .lowercase()
        if (combined.isNotBlank()) {
            when {
                "password" in combined || "passwd" in combined || "pwd" in combined || "رمز" in combined || "عبور" in combined -> return FieldType.PASSWORD
                "email" in combined || "mail" in combined || "ایمیل" in combined -> return FieldType.EMAIL
                "user" in combined || "login" in combined || "username" in combined || "کاربری" in combined || "شناسه" in combined -> return FieldType.USERNAME
                "phone" in combined || "mobile" in combined || "tel" in combined || "موبایل" in combined || "تلفن" in combined -> return FieldType.PHONE
            }
        }

        // 5) Fallback for generic text fields (focused or editable text input)
        if (node.autofillType == View.AUTOFILL_TYPE_TEXT || node.isFocused) {
            return FieldType.GENERIC_TEXT
        }

        return null
    }

    private fun extractTokens(text: String, set: MutableSet<String>) {
        if (text.isBlank()) return
        text.lowercase()
            .split(Regex("[^a-zA-Z0-9\\u0600-\\u06FF]+"))
            .filter { token ->
                (token.length >= 3) && (token !in IGNORED_TOKENS)
            }
            .forEach { set.add(it) }
    }

    private val IGNORED_TOKENS = setOf(
        "com",
        "org",
        "net",
        "app",
        "android",
        "https",
        "http",
        "www",
        "activity",
        "view",
        "edittext",
        "input",
        "widget",
    )
}
