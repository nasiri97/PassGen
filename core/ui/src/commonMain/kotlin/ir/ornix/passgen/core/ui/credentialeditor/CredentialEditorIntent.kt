package ir.ornix.passgen.core.ui.credentialeditor

import ir.ornix.passgen.core.model.InstalledAppInfo
import ir.ornix.passgen.core.model.Password

internal sealed interface CredentialEditorIntent {
    data class TitleChanged(val title: String) : CredentialEditorIntent
    data class DescriptionChanged(val description: String) : CredentialEditorIntent
    data class UsernameChanged(val username: String) : CredentialEditorIntent
    data class EmailChanged(val email: String) : CredentialEditorIntent
    data class PhoneChanged(val phone: String) : CredentialEditorIntent
    data class PasswordChanged(val password: Password) : CredentialEditorIntent
    data class PackageInputChanged(val packageInput: String) : CredentialEditorIntent
    data class AddPackageName(val packageName: String) : CredentialEditorIntent
    data class RemovePackageName(val packageName: String) : CredentialEditorIntent
    data class DomainInputChanged(val domainInput: String) : CredentialEditorIntent
    data class AddWebDomain(val domain: String) : CredentialEditorIntent
    data class RemoveWebDomain(val domain: String) : CredentialEditorIntent
    data class KeywordInputChanged(val keywordInput: String) : CredentialEditorIntent
    data class AddKeyword(val keyword: String) : CredentialEditorIntent
    data class RemoveKeyword(val keyword: String) : CredentialEditorIntent
    data object TitleBlurred : CredentialEditorIntent
    data object PasswordBlurred : CredentialEditorIntent
    data class ToggleAppPickerExpanded(val expanded: Boolean) : CredentialEditorIntent
    data object LoadingAppsStarted : CredentialEditorIntent
    data class AppsLoaded(val apps: List<InstalledAppInfo>) : CredentialEditorIntent
    data class AppSearchQueryChanged(val query: String) : CredentialEditorIntent
    data class TogglePackageSelection(val packageName: String) : CredentialEditorIntent
    data class CustomPackageInputChanged(val customInput: String) : CredentialEditorIntent
    data class AddCustomPackage(val packageName: String) : CredentialEditorIntent
}
