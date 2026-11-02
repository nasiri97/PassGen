package ir.ornix.passgen.core.ui.credentialeditor

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
expect fun AppIcon(
    icon: Any?,
    modifier: Modifier = Modifier
)

@Composable
internal fun DefaultAppIcon(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier.size(40.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Android,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(8.dp)
        )
    }
}
