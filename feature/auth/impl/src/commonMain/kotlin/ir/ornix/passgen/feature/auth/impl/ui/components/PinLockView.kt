package ir.ornix.passgen.feature.auth.impl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun PinLockView(
    onPinCompleted: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var pinInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display bullets
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            for (i in 0 until 4) {
                val filled = i < pinInput.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Basic 3x4 Grid Dial-Pad
        val buttons = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "C")
        )

        buttons.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                row.forEach { symbol ->
                    if (symbol.isEmpty()) {
                        Spacer(Modifier.size(64.dp))
                    } else {
                        OutlinedButton(
                            onClick = {
                                if (symbol == "C") {
                                    if (pinInput.isNotEmpty()) pinInput.dropLast(1)
                                } else {
                                    pinInput += symbol
                                    if (pinInput.length == 4) {
                                        onPinCompleted(pinInput)
                                        pinInput = ""
                                    }
                                }
                            },
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape
                        ) {
                            if (symbol == "C") {
                                Icon(Icons.Default.Delete, contentDescription = "Clear")
                            } else {
                                Text(text = symbol, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}