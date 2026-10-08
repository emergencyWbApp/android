package `in`.emergencyWbApp




import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class MainActivity : Activity() {

    private lateinit var splitInstallManager: SplitInstallManager

    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar

    private val dashboardModule = "essentials"

    private var downloadStarted = false

    private val splitInstallListener =
        SplitInstallStateUpdatedListener { state ->

            if (!state.moduleNames().contains(dashboardModule)) {
                return@SplitInstallStateUpdatedListener
            }

            when (state.status()) {

                SplitInstallSessionStatus.PENDING -> {
                    statusText.text = "Preparing Dashboard..."
                    progressBar.visibility = View.VISIBLE
                }

                SplitInstallSessionStatus.DOWNLOADING -> {

                    progressBar.visibility = View.VISIBLE

                    val downloaded =
                        state.bytesDownloaded()

                    val total =
                        state.totalBytesToDownload()

                    if (total > 0) {

                        val progress =
                            ((downloaded * 100L) / total)
                                .toInt()

                        progressBar.progress = progress

                        statusText.text =
                            "Downloading Dashboard... $progress%"
                    } else {

                        statusText.text =
                            "Downloading Dashboard..."
                    }
                }

                SplitInstallSessionStatus.DOWNLOADED -> {
                    statusText.text =
                        "Dashboard downloaded. Installing..."

                    progressBar.visibility =
                        View.VISIBLE
                }

                SplitInstallSessionStatus.INSTALLING -> {
                    statusText.text =
                        "Installing Dashboard..."

                    progressBar.visibility =
                        View.VISIBLE
                }

                SplitInstallSessionStatus.INSTALLED -> {

                    progressBar.progress = 100

                    progressBar.visibility =
                        View.GONE

                    statusText.text =
                        "Dashboard ready"

                    launchDashboard()
                }

                SplitInstallSessionStatus.FAILED -> {

                    progressBar.visibility =
                        View.GONE

                    statusText.text =
                        "Dashboard download failed"

                    Toast.makeText(
                        this,
                        "Dashboard download failed. Please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                SplitInstallSessionStatus.CANCELED -> {

                    progressBar.visibility =
                        View.GONE

                    statusText.text =
                        "Dashboard download cancelled"
                }

                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {

                    statusText.text =
                        "User confirmation required"
                }
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        createBaseScreen()

        splitInstallManager =
            SplitInstallManagerFactory.create(this)

        checkDashboard()
    }

    private fun createBaseScreen() {

        val root =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                gravity =
                    Gravity.CENTER

                setPadding(
                    32,
                    32,
                    32,
                    32
                )
            }

        val title =
            TextView(this).apply {

                text =
                    "Emergency WB App"

                textSize =
                    26f

                gravity =
                    Gravity.CENTER
            }

        statusText =
            TextView(this).apply {

                text =
                    "Starting..."

                textSize =
                    17f

                gravity =
                    Gravity.CENTER

                setPadding(
                    0,
                    24,
                    0,
                    24
                )
            }

        progressBar =
            ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
            ).apply {

                max = 100

                progress = 0

                visibility =
                    View.VISIBLE

                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        12
                    )
            }

        root.addView(title)

        root.addView(statusText)

        root.addView(progressBar)

        setContentView(root)
    }

    private fun checkDashboard() {

        val installed =
            splitInstallManager
                .installedModules
                .contains(dashboardModule)

        if (installed) {

            statusText.text =
                "Dashboard ready"

            progressBar.visibility =
                View.GONE

            launchDashboard()

        } else {

            downloadDashboard()
        }
    }

    private fun downloadDashboard() {

        if (downloadStarted) {
            return
        }

        downloadStarted = true

        statusText.text =
            "Starting Dashboard download..."

        progressBar.visibility =
            View.VISIBLE

        progressBar.progress = 0

        val request =
            SplitInstallRequest
                .newBuilder()
                .addModule(dashboardModule)
                .build()

        splitInstallManager
            .startInstall(request)
            .addOnSuccessListener {

                statusText.text =
                    "Dashboard download started..."
            }
            .addOnFailureListener { exception ->

                downloadStarted = false

                progressBar.visibility =
                    View.GONE

                statusText.text =
                    "Dashboard download failed"

                Toast.makeText(
                    this,
                    exception.message
                        ?: "Unknown download error",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun launchDashboard() {
        try {
            val activityClass = Class.forName("in.emergencyWbApp.essentials.EssentialsActivity")
            val intent = Intent(this, activityClass)
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            Toast.makeText(this, "Dashboard module not installed yet", Toast.LENGTH_LONG).show()
            // Optionally trigger download again
            downloadDashboard()
        } catch (e: Exception) {
            Toast.makeText(this, "Dashboard launch error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }




    override fun onStart() {

        super.onStart()

        splitInstallManager.registerListener(
            splitInstallListener
        )
    }

    override fun onStop() {

        splitInstallManager.unregisterListener(
            splitInstallListener
        )

        super.onStop()
    }
}