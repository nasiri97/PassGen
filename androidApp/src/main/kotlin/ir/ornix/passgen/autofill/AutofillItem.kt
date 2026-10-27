package ir.ornix.passgen.autofill

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

@Composable
@GlanceComposable
fun AutofillItem(title: String, subtitle: String) {
    GlanceTheme {
        Column(modifier = GlanceModifier.fillMaxWidth().padding(12.dp)) {
            Text(
                modifier = GlanceModifier.fillMaxWidth(),
                text = title,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onSurface
                )
            )
            if (subtitle.isNotBlank()) {
                Text(
                    modifier = GlanceModifier.fillMaxWidth(),
                    text = subtitle,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = GlanceTheme.colors.onSurfaceVariant
                    )
                )
            }
        }
    }
}