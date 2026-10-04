package ir.ornix.passgen.feature.config.impl.kdf.ui.master_key_step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.master_key_requirements_title
import ir.ornix.passgen.core.ui.req_digit
import ir.ornix.passgen.core.ui.req_lowercase
import ir.ornix.passgen.core.ui.req_min_length
import ir.ornix.passgen.core.ui.req_rec_reached
import ir.ornix.passgen.core.ui.req_rec_target
import ir.ornix.passgen.core.ui.req_special
import ir.ornix.passgen.core.ui.req_uppercase
import ir.ornix.passgen.feature.config.impl.kdf.ui.MIN_MASTER_KEY_LENGTH
import ir.ornix.passgen.feature.config.impl.kdf.ui.MasterKeyValidation
import ir.ornix.passgen.feature.config.impl.kdf.ui.RECOMMENDED_MASTER_KEY_LENGTH
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MasterKeyValidationCard(
    validation: MasterKeyValidation,
    masterKeyLength: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.master_key_requirements_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            ValidationRequirementItem(
                label = stringResource(
                    Res.string.req_min_length,
                    masterKeyLength,
                    MIN_MASTER_KEY_LENGTH
                ),
                isSatisfied = validation.hasMinLength,
                isRecommended = validation.isRecommendedLength,
                recommendationLabel =
                    if (validation.isRecommendedLength)
                        stringResource(Res.string.req_rec_reached, RECOMMENDED_MASTER_KEY_LENGTH)
                    else
                        stringResource(Res.string.req_rec_target, RECOMMENDED_MASTER_KEY_LENGTH)
            )
            ValidationRequirementItem(
                label = stringResource(Res.string.req_lowercase),
                isSatisfied = validation.hasLowercase
            )
            ValidationRequirementItem(
                label = stringResource(Res.string.req_uppercase),
                isSatisfied = validation.hasUppercase
            )
            ValidationRequirementItem(
                label = stringResource(Res.string.req_digit),
                isSatisfied = validation.hasDigit
            )
            ValidationRequirementItem(
                label = stringResource(Res.string.req_special),
                isSatisfied = validation.hasSpecialChar
            )
        }
    }
}

@Composable
private fun ValidationRequirementItem(
    label: String,
    isSatisfied: Boolean,
    isRecommended: Boolean = false,
    recommendationLabel: String = ""
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isSatisfied) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isSatisfied) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            },
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label + recommendationLabel,
            style = MaterialTheme.typography.bodySmall,
            color = when {
                isRecommended -> MaterialTheme.colorScheme.primary
                isSatisfied -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
