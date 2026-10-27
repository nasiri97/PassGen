package ir.ornix.passgen.core.ui.passgen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.model.PasswordStrengthLevel
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.password_strength
import ir.ornix.passgen.core.ui.util.label
import ir.ornix.passgen.core.ui.util.strengthColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun PasswordStrengthDisplay(
    strengthLevel: PasswordStrengthLevel,
    modifier: Modifier = Modifier
) {
    val strengthColor = strengthLevel.strengthColor()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Label Row: Strength Header + Color Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.password_strength),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = strengthColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = strengthLevel.label(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = strengthColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        PasswordStrengthProgressIndicator(strengthLevel)
    }
}

@Composable
fun PasswordStrengthProgressIndicator(
    strengthLevel: PasswordStrengthLevel,
    modifier: Modifier = Modifier
) {
    val levels = PasswordStrengthLevel.entries
    val currentLevelIndex = levels.indexOf(strengthLevel)
    val strengthColor = strengthLevel.strengthColor()

    // Segmented Step Progress Bar
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        levels.forEachIndexed { index, _ ->
            val isActive = index <= currentLevelIndex
            val targetColor = if (isActive) {
                strengthColor
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            }

            val animatedColor by animateColorAsState(
                targetValue = targetColor,
                animationSpec = tween(durationMillis = 300),
                label = "strength_step_color"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(animatedColor)
            )
        }
    }
}