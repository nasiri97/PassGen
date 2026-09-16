package ir.ornix.passgen

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isSensitiveData
import androidx.compose.ui.tooling.preview.Preview
import ir.ornix.passgen.composeapp.App
import ir.ornix.passgen.core.data.AppContextProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AppContextProvider.setActivity(this)


        /**
         * Prevent Screenshots & Screen Recording (FLAG_SECURE)
         */
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )


        setContent {
            Surface(
                modifier = Modifier
                    .clearAndSetSemantics {
                        // Marks this field as sensitive to prevent accessibility extraction
                        isSensitiveData = true
                        contentDescription = "Secure app"
                    }
                    .fillMaxSize()
            ) {
                App()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AppContextProvider.setActivity(this)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}