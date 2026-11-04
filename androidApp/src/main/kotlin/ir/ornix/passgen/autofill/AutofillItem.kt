package ir.ornix.passgen.autofill

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import ir.ornix.passgen.R

@Composable
@GlanceComposable
fun AutofillItem(
    title: String,
    subtitle: String,
    widthDp: Int = 280
) {
    GlanceTheme {
        Box(
            modifier = GlanceModifier
                .width(widthDp.dp)
                .height(56.dp)
                .cornerRadius(12.dp)
                .background(GlanceTheme.colors.surface)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App / Key Icon Avatar Box
                Box(
                    modifier = GlanceModifier
                        .size(38.dp)
                        .cornerRadius(10.dp)
                        .background(GlanceTheme.colors.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_autofill_key),
                        contentDescription = "Autofill Key",
                        modifier = GlanceModifier.size(22.dp)
                    )
                }

                // Credential Details (Title & Subtitle)
                Column(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .padding(start = 10.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title.ifBlank { "PassGen" },
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlanceTheme.colors.onSurface
                        ),
                        modifier = GlanceModifier.fillMaxWidth()
                    )
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            maxLines = 1,
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = GlanceTheme.colors.onSurfaceVariant
                            ),
                            modifier = GlanceModifier.fillMaxWidth()
                        )
                    }
                }

                // Biometric Protection Badge Icon
                Box(
                    modifier = GlanceModifier
                        .size(28.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_fingerprint_badge),
                        contentDescription = "Biometric Protected",
                        modifier = GlanceModifier.size(20.dp)
                    )
                }
            }
        }
    }
}
