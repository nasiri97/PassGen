package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_clear
import ir.ornix.passgen.core.ui.context_input_label
import ir.ornix.passgen.core.ui.context_input_placeholder
import ir.ornix.passgen.core.ui.security.secureInput
import org.jetbrains.compose.resources.stringResource

@Composable
fun InputSection(
    input: String,
    onInputChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier
            .secureInput(stringResource(Res.string.context_input_label))
            .fillMaxWidth(),
        value = input,
        onValueChange = {
            if (!it.contains('\n')) onInputChange(it)
        },
        label = { Text(stringResource(Res.string.context_input_label)) },
        placeholder = {
            Text(
                text = stringResource(Res.string.context_input_placeholder),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible
            )
        },
        leadingIcon = {
            Icon(terminal_2, contentDescription = null)
        },
        trailingIcon = {
            if (input.isNotEmpty()) {
                IconButton(onClick = { onInputChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = stringResource(Res.string.action_clear)
                    )
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() })
    )
}

private var _terminal_2: ImageVector? = null

@Suppress("CheckReturnValue")
private val terminal_2: ImageVector
    get() {
        if (_terminal_2 != null) {
            return _terminal_2!!
        }
        _terminal_2 =
            ImageVector.Builder(
                name = "terminal_2",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Bevel,
                        strokeLineMiter = 1f,
                        pathFillType = PathFillType.Companion.NonZero,
                    ) {
                        moveTo(12f, 20f)
                        verticalLineTo(18f)
                        horizontalLineToRelative(8f)
                        verticalLineToRelative(2f)
                        horizontalLineTo(12f)
                        close()
                        moveTo(5.5f, 16f)
                        lineTo(4.1f, 14.6f)
                        lineTo(8.68f, 10f)
                        lineTo(4.1f, 5.4f)
                        lineTo(5.5f, 4f)
                        lineToRelative(6f, 6f)
                        lineToRelative(-6f, 6f)
                        close()
                    }
                }
                .build()
        return _terminal_2!!
    }
