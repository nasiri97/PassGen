package ir.ornix.passgen.feature.config.impl.kdf.ui.master_key_step

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.master_key_notice_ack
import ir.ornix.passgen.core.ui.master_key_notice_backup
import ir.ornix.passgen.core.ui.master_key_notice_collapse
import ir.ornix.passgen.core.ui.master_key_notice_derived
import ir.ornix.passgen.core.ui.master_key_notice_expand
import ir.ornix.passgen.core.ui.master_key_notice_portable
import ir.ornix.passgen.core.ui.master_key_notice_title
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds


private const val NOTICE_ANIM_MS = 250

@Composable
internal fun MasterKeyNotice(
    acknowledged: Boolean,
    onAcknowledgedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(!acknowledged) }
    val scope = rememberCoroutineScope()
    var collapseJob by remember { mutableStateOf<Job?>(null) }

    // Locked open until the user acknowledges
    val showDetails = expanded || !acknowledged

    val chevronRotation by animateFloatAsState(
        targetValue = if (showDetails) 180f else 0f,
        animationSpec = tween(NOTICE_ANIM_MS, easing = FastOutSlowInEasing),
        label = "notice_chevron"
    )

    val accent by animateColorAsState(
        targetValue = if (acknowledged) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(NOTICE_ANIM_MS),
        label = "notice_accent"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.3f))
    ) {
        Column {
            // Header: only tappable once acknowledged
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = acknowledged,
                        role = Role.Button,
                        onClickLabel = stringResource(
                            if (showDetails) Res.string.master_key_notice_collapse
                            else Res.string.master_key_notice_expand
                        ),
                        onClick = { expanded = !expanded }
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (acknowledged) Icons.Filled.CheckCircle else Icons.Filled.Shield,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(Res.string.master_key_notice_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )

                // Chevron: hidden until acknowledged, but keeps its space (no layout jump)
                AnimatedVisibility(
                    visible = acknowledged,
                    enter = fadeIn(tween(NOTICE_ANIM_MS)) + scaleIn(
                        tween(NOTICE_ANIM_MS),
                        initialScale = 0.6f
                    ),
                    exit = fadeOut(tween(NOTICE_ANIM_MS / 2)) + scaleOut(
                        tween(NOTICE_ANIM_MS / 2),
                        targetScale = 0.6f
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer { rotationZ = chevronRotation }
                    )
                }
                if (!acknowledged) Spacer(Modifier.size(24.dp)) // reserves the chevron's space
            }

            // Collapsible area: details + acknowledgment
            AnimatedVisibility(
                visible = showDetails,
                enter = expandVertically(
                    animationSpec = tween(NOTICE_ANIM_MS, easing = FastOutSlowInEasing),
                    expandFrom = Alignment.Top
                ) + fadeIn(tween(NOTICE_ANIM_MS, delayMillis = 50)),
                exit = shrinkVertically(
                    animationSpec = tween(NOTICE_ANIM_MS, easing = FastOutSlowInEasing),
                    shrinkTowards = Alignment.Top
                ) + fadeOut(tween(NOTICE_ANIM_MS / 2))
            ) {
                Column {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        )
                        NoticeItem(
                            icon = Icons.Filled.Key,
                            text = stringResource(Res.string.master_key_notice_derived)
                        )
                        NoticeItem(
                            icon = Icons.Filled.EditNote,
                            text = stringResource(Res.string.master_key_notice_backup)
                        )
                        NoticeItem(
                            icon = Icons.Filled.Devices,
                            text = stringResource(Res.string.master_key_notice_portable)
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    )
                    AcknowledgeRow(
                        checked = acknowledged,
                        onCheckedChange = { checked ->
                            collapseJob?.cancel()
                            onAcknowledgedChange(checked)
                            if (checked) {
                                // Let the check animation play, then collapse
                                collapseJob = scope.launch {
                                    delay(350.milliseconds)
                                    expanded = false
                                }
                            } else {
                                expanded = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AcknowledgeRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val background by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else Color.Transparent,
        animationSpec = tween(NOTICE_ANIM_MS),
        label = "ack_background"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .toggleable(
                value = checked,
                role = Role.Checkbox,
                onValueChange = onCheckedChange
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("master_key_ack"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null // the whole row handles the toggle
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(Res.string.master_key_notice_ack),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp)
        )
    }
}

@Composable
private fun NoticeItem(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .weight(1f)
                .padding(top = 6.dp)
        )
    }
}
