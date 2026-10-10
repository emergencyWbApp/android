package `in`.emergencyWbApp.premium



import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class PremiumActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "🎉 Premium Module Loaded!"
            textSize = 24f
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "This is the Premium Dashboard.\nDynamic Feature working successfully."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 0)
        }

        root.addView(title)
        root.addView(subtitle)

        setContentView(root)
    }
}