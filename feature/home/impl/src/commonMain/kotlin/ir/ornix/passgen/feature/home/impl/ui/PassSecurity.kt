package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.designsystem.BothPreview
import ir.ornix.passgen.core.designsystem.theme.PassGenTheme
import ir.ornix.passgen.passwordgenerator.model.Password
import ir.ornix.passgen.passwordgenerator.model.Password.Companion.toPassword

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PassSecurity(
    password: Password,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LengthChip(length = password.value.length)

        if (password.hasUpper) {
            TraitChip(
                label = "Uppercase",
                icon = Icons.Rounded.TextFormat
            )
        }
        if (password.hasLower) {
            TraitChip(
                label = "Lowercase",
                icon = Icons.Rounded.TextFormat
            )
        }
        if (password.hasDigit) {
            TraitChip(
                label = "Digits",
                icon = Icons.Rounded.Numbers
            )
        }
        if (password.hasSpecialChar) {
            TraitChip(
                label = "Special",
                icon = Icons.Rounded.Star
            )
        }
    }
}

@Composable
fun LengthChip(length: Int) {
    val (containerColor, contentColor) = when {
        length < 8 -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        length < 12 -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }

    TraitChip(
        label = "$length chars",
        icon = Icons.Rounded.Password,
        containerColor = containerColor,
        contentColor = contentColor
    )
}

@Composable
private fun TraitChip(
    label: String,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        ) { it / 2 } + fadeIn()
    ) {
        Surface(
            shape = CircleShape,
            color = containerColor,
            contentColor = contentColor,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@BothPreview
@Composable
private fun PasswordCardPreview() {
    PassGenTheme {
        PassSecurity(
            password = "1aA$".toPassword()
        )
    }
}
