package ir.ornix.passgen.core.ui.credentialeditor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.Password.Companion.toPassword
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_add
import ir.ornix.passgen.core.ui.action_cancel
import ir.ornix.passgen.core.ui.action_create
import ir.ornix.passgen.core.ui.create_new_credential_title
import ir.ornix.passgen.core.ui.label_description
import ir.ornix.passgen.core.ui.label_email
import ir.ornix.passgen.core.ui.label_keywords
import ir.ornix.passgen.core.ui.label_package_names
import ir.ornix.passgen.core.ui.label_phone
import ir.ornix.passgen.core.ui.label_title
import ir.ornix.passgen.core.ui.label_username
import ir.ornix.passgen.core.ui.label_web_domains
import ir.ornix.passgen.core.ui.password
import ir.ornix.passgen.core.ui.placeholder_keyword
import ir.ornix.passgen.core.ui.placeholder_package_name
import ir.ornix.passgen.core.ui.placeholder_web_domain
import ir.ornix.passgen.core.ui.section_apps_and_webs_and_keywords
import ir.ornix.passgen.core.ui.section_basic_info
import ir.ornix.passgen.core.ui.section_credential_details
import ir.ornix.passgen.core.ui.section_description
import ir.ornix.passgen.core.ui.security.PasswordDisplay
import ir.ornix.passgen.core.ui.security.PasswordTextField
import org.jetbrains.compose.resources.stringResource

@Composable
fun CredentialEditor(
    password: Password? = null,
    isPasswordEditable: Boolean = password == null,
    defaultTitle: String = "",
    existingCredential: Credential? = null,
    onSubmit: (credential: Credential) -> Unit,
    onDismissRequest: () -> Unit,
    onCopyRequested: (Password) -> Unit,
    revealTimeoutMillis: Long,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    onSnackbarAvailabilityChanged: (Boolean) -> Unit = {}
) {
    var uiState by remember {
        mutableStateOf(
            CredentialEditorUiState.fromCredential(
                credential = existingCredential,
                defaultTitle = defaultTitle,
                initialPassword = password
            )
        )
    }

    val dispatch: (CredentialEditorIntent) -> Unit = { intent ->
        uiState = reduce(uiState, intent)
    }

    val isSnackbarAvailableInChildren = true

    LaunchedEffect(isSnackbarAvailableInChildren) {
        onSnackbarAvailabilityChanged(isSnackbarAvailableInChildren)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    // Header
                    DialogHeader(onDismissRequest = onDismissRequest)

                    HorizontalDivider(
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )

                    // Scrollable Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .heightIn(max = 520.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        if (isPasswordEditable) {
                            // Editable Password Section
                            PasswordInputSection(
                                uiState = uiState,
                                dispatch = dispatch,
                                revealTimeoutMillis = revealTimeoutMillis
                            )
                        } else if (password != null) {
                            // Read-only Password Preview Card
                            PasswordDisplay(
                                modifier = Modifier.fillMaxWidth(),
                                password = password,
                                revealTimeoutMillis = revealTimeoutMillis,
                                isClickable = true,
                                onClick = { onCopyRequested(password) }
                            )
                        }

                        // Basic Info Section
                        BasicInfoSection(
                            uiState = uiState,
                            dispatch = dispatch
                        )

                        // Contact & Identity Section
                        CredentialDetailsSection(
                            uiState = uiState,
                            dispatch = dispatch
                        )

                        // Apps & Web Linking Section
                        AssociationSection(
                            uiState = uiState,
                            dispatch = dispatch
                        )

                        // Notes Section
                        DescriptionSection(
                            uiState = uiState,
                            dispatch = dispatch
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )

                    // Footer Actions
                    DialogFooterActions(
                        isSaveEnabled = uiState.isSaveEnabled(),
                        onCancel = onDismissRequest,
                        onSave = {
                            if (uiState.isSaveEnabled()) {
                                uiState.password?.let { password ->
                                    val credential = Credential(
                                        id = existingCredential?.id ?: 0,
                                        title = uiState.title.trim(),
                                        description = uiState.description.trim().ifBlank { null },
                                        password = password,
                                        username = uiState.username.trim().ifBlank { null },
                                        email = uiState.email.trim().ifBlank { null },
                                        phone = uiState.phone.trim().ifBlank { null },
                                        associatedPackageNames = uiState.associatedPackageNames,
                                        associatedWebDomains = uiState.associatedWebDomains,
                                        associatedKeywords = uiState.associatedKeywords
                                    )
                                    onSubmit(credential)
                                }
                            } else {
                                if (!uiState.isTitleValid)
                                    dispatch(CredentialEditorIntent.TitleBlurred)
                                if (isPasswordEditable && !uiState.isPasswordValid)
                                    dispatch(CredentialEditorIntent.PasswordBlurred)
                            }
                        }
                    )
                }

                if (snackbarHostState != null) {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogHeader(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Text(
                text = stringResource(Res.string.create_new_credential_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(onClick = onDismissRequest) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PasswordInputSection(
    uiState: CredentialEditorUiState,
    dispatch: (CredentialEditorIntent) -> Unit,
    revealTimeoutMillis: Long,
    modifier: Modifier = Modifier
) {
    SectionContainer(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && (uiState.password?.value?.isEmpty() == true)) {
                    dispatch(CredentialEditorIntent.PasswordBlurred)
                }
            },
        title = stringResource(Res.string.password)
    ) {
        PasswordTextField(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                dispatch(
                    CredentialEditorIntent.PasswordChanged(
                        it.toPassword(entropyByteSize = null)
                    )
                )
            },
            label = { Text(stringResource(Res.string.password) + " *") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            isError = uiState.showPasswordError,
            revealTimeoutMillis = revealTimeoutMillis,
            maxLength = 256
        )
    }
}

@Composable
private fun BasicInfoSection(
    uiState: CredentialEditorUiState,
    dispatch: (CredentialEditorIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var hasBeenFocused by remember { mutableStateOf(false) }

    SectionContainer(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    hasBeenFocused = true
                } else if (hasBeenFocused && uiState.title.isBlank()) {
                    dispatch(CredentialEditorIntent.TitleBlurred)
                }
            },
        title = stringResource(Res.string.section_basic_info)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.title,
            onValueChange = { dispatch(CredentialEditorIntent.TitleChanged(it)) },
            label = { Text(stringResource(Res.string.label_title) + " *") },
            isError = uiState.showTitleError,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                if (uiState.title.isNotEmpty()) {
                    IconButton(onClick = { dispatch(CredentialEditorIntent.TitleChanged("")) }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            }
        )
    }
}

@Composable
private fun CredentialDetailsSection(
    uiState: CredentialEditorUiState,
    dispatch: (CredentialEditorIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    SectionContainer(
        modifier = modifier,
        title = stringResource(Res.string.section_credential_details)
    ) {
        OutlinedTextField(
            value = uiState.username,
            onValueChange = { dispatch(CredentialEditorIntent.UsernameChanged(it)) },
            label = { Text(stringResource(Res.string.label_username)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { dispatch(CredentialEditorIntent.EmailChanged(it)) },
            label = { Text(stringResource(Res.string.label_email)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = uiState.phone,
            onValueChange = { dispatch(CredentialEditorIntent.PhoneChanged(it)) },
            label = { Text(stringResource(Res.string.label_phone)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AssociationSection(
    uiState: CredentialEditorUiState,
    dispatch: (CredentialEditorIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    SectionContainer(
        modifier = modifier,
        title = stringResource(Res.string.section_apps_and_webs_and_keywords)
    ) {
        // Package Names
        AssociationItem(
            label = { Text(stringResource(Res.string.label_package_names)) },
            placeholder = { Text(stringResource(Res.string.placeholder_package_name)) },
            currentInput = uiState.packageInput,
            inputChanged = { dispatch(CredentialEditorIntent.PackageInputChanged(it)) },
            onAddClicked = { dispatch(CredentialEditorIntent.AddPackageName(uiState.packageInput)) },
            onRemoveItemClicked = { dispatch(CredentialEditorIntent.RemovePackageName(it)) },
            items = uiState.associatedPackageNames
        )

        Spacer(Modifier.height(4.dp))

        // Web Domains
        AssociationItem(
            label = { Text(stringResource(Res.string.label_web_domains)) },
            placeholder = { Text(stringResource(Res.string.placeholder_web_domain)) },
            currentInput = uiState.domainInput,
            inputChanged = { dispatch(CredentialEditorIntent.DomainInputChanged(it)) },
            onAddClicked = { dispatch(CredentialEditorIntent.AddWebDomain(uiState.domainInput)) },
            onRemoveItemClicked = { dispatch(CredentialEditorIntent.RemoveWebDomain(it)) },
            items = uiState.associatedWebDomains
        )

        Spacer(Modifier.height(4.dp))

        // Keywords
        AssociationItem(
            label = { Text(stringResource(Res.string.label_keywords)) },
            placeholder = { Text(stringResource(Res.string.placeholder_keyword)) },
            currentInput = uiState.keywordInput,
            inputChanged = { dispatch(CredentialEditorIntent.KeywordInputChanged(it)) },
            onAddClicked = { dispatch(CredentialEditorIntent.AddKeyword(uiState.keywordInput)) },
            onRemoveItemClicked = { dispatch(CredentialEditorIntent.RemoveKeyword(it)) },
            items = uiState.associatedKeywords
        )
    }
}

@Composable
private fun AssociationItem(
    label: @Composable () -> Unit,
    placeholder: @Composable () -> Unit,
    currentInput: String,
    inputChanged: (String) -> Unit,
    onAddClicked: () -> Unit,
    onRemoveItemClicked: (String) -> Unit,
    items: Set<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = currentInput,
            onValueChange = inputChanged,
            label = label,
            placeholder = placeholder,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onAddClicked() }),
            trailingIcon = {
                IconButton(
                    enabled = currentInput.isNotBlank(),
                    onClick = onAddClicked
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(Res.string.action_add)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        AnimatedVisibility(visible = items.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items.forEach { item ->
                    InputChip(
                        selected = false,
                        onClick = { },
                        label = { Text(item, style = MaterialTheme.typography.labelMedium) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemoveItemClicked(item) }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DescriptionSection(
    uiState: CredentialEditorUiState,
    dispatch: (CredentialEditorIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    SectionContainer(
        modifier = modifier,
        title = stringResource(Res.string.section_description)
    ) {
        OutlinedTextField(
            value = uiState.description,
            onValueChange = { dispatch(CredentialEditorIntent.DescriptionChanged(it)) },
            label = { Text(stringResource(Res.string.label_description)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun SectionContainer(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
private fun DialogFooterActions(
    isSaveEnabled: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel) {
            Text(stringResource(Res.string.action_cancel))
        }

        Spacer(Modifier.width(12.dp))

        Button(
            enabled = isSaveEnabled,
            onClick = onSave,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(Res.string.action_create))
        }
    }
}
