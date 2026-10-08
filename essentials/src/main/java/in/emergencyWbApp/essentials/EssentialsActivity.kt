package `in`.emergencyWbApp.essentials






import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import `in`.emergencyWbApp.essentials.ui.EssentialsPagerScreen
import `in`.emergencyWbApp.essentials.ui.theme.EssentialsTheme

class EssentialsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Back press disable
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Option A: App minimize (WhatsApp style)
                    moveTaskToBack(true)

                    // Option B: Kuchu na korte chan? ei line comment korun
                    // (kuchu likhben na)
                }
            }
        )

        setContent {
            EssentialsTheme {
                EssentialsPagerScreen()
            }
        }
    }
}