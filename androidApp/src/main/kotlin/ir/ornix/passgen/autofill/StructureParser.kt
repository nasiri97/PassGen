package ir.ornix.passgen.autofill

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.text.InputType
import android.view.View
import android.view.autofill.AutofillId

enum class FieldType { USERNAME, EMAIL, PASSWORD, PHONE }

class ParsedStructure(
    val usernameId: AutofillId?,
    val emailId: AutofillId?,
    val passwordId: AutofillId?,
    val phoneId: AutofillId?,
    val packageName: String?,
    val webDomain: String?
) {
    // Only offer autofill on screens that look like a login form.
    val isLoginForm: Boolean
        get() = passwordId != null || usernameId != null || emailId != null
}

object StructureParser {

    private class Acc {
        var username: AutofillId? = null
        var email: AutofillId? = null
        var password: AutofillId? = null
        var phone: AutofillId? = null
        var webDomain: String? = null
    }

    fun parse(structure: AssistStructure): ParsedStructure {
        val acc = Acc()
        for (i in 0 until structure.windowNodeCount) {
            walk(structure.getWindowNodeAt(i).rootViewNode, acc)
        }
        return ParsedStructure(
            usernameId = acc.username,
            emailId = acc.email,
            passwordId = acc.password,
            phoneId = acc.phone,
            packageName = structure.activityComponent?.packageName,
            webDomain = acc.webDomain,
        )
    }

    private fun walk(node: ViewNode, acc: Acc) {
        if (acc.webDomain == null) {
            acc.webDomain = node.webDomain?.takeIf { it.isNotBlank() }
        }

        val id = node.autofillId
        if (id != null &&
            node.autofillType == View.AUTOFILL_TYPE_TEXT &&
            node.visibility == View.VISIBLE
        ) {
            when (classify(node)) {
                FieldType.USERNAME -> if (acc.username == null) acc.username = id
                FieldType.EMAIL -> if (acc.email == null) acc.email = id
                FieldType.PASSWORD -> if (acc.password == null) acc.password = id
                FieldType.PHONE -> if (acc.phone == null) acc.phone = id
                null -> Unit
            }
        }

        for (i in 0 until node.childCount) walk(node.getChildAt(i), acc)
    }

    private fun classify(node: ViewNode): FieldType? {
        // 1) Explicit hints (android:autofillHints) - most reliable
        node.autofillHints?.forEach { hint ->
            when (hint) {
                View.AUTOFILL_HINT_USERNAME -> return FieldType.USERNAME
                View.AUTOFILL_HINT_PASSWORD, "newPassword" -> return FieldType.PASSWORD
                View.AUTOFILL_HINT_EMAIL_ADDRESS -> return FieldType.EMAIL
                View.AUTOFILL_HINT_PHONE -> return FieldType.PHONE
            }
        }

        // 2) HTML attributes (web pages in browsers)
        val htmlType = node.htmlInfo?.attributes?.firstOrNull { it.first == "type" }?.second
        when (htmlType) {
            "password" -> return FieldType.PASSWORD
            "email" -> return FieldType.EMAIL
            "tel" -> return FieldType.PHONE
        }

        // 3) inputType
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

        // 4) Last resort: heuristics on resource id / hint text
        val text = listOfNotNull(node.idEntry, node.hint).joinToString(" ").lowercase()
        return when {
            "password" in text || "passwd" in text || "pwd" in text -> FieldType.PASSWORD
            "email" in text -> FieldType.EMAIL
            "user" in text || "login" in text -> FieldType.USERNAME
            "phone" in text || "mobile" in text -> FieldType.PHONE
            else -> null
        }
    }
}