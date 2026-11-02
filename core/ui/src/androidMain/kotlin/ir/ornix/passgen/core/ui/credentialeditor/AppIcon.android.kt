package ir.ornix.passgen.core.ui.credentialeditor

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
actual fun AppIcon(
    icon: Any?,
    modifier: Modifier
) {
    val bitmap = remember(icon) { (icon as? Bitmap)?.asImageBitmap() }
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
        )
    } else {
        DefaultAppIcon(modifier = modifier)
    }
}
