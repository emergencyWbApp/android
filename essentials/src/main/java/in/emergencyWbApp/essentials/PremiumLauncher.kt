package `in`.emergencyWbApp.essentials

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

object PremiumLauncher {

    private const val PREMIUM_MODULE = "premium"
    private const val PREMIUM_ACTIVITY = "in.emergencyWbApp.premium.PremiumActivity"
    private const val TAG = "PremiumLauncher"

    fun openPremium(context: Context) {
        val manager = SplitInstallManagerFactory.create(context)

        // ইতিমধ্যে ইনস্টল করা আছে?
        if (manager.installedModules.contains(PREMIUM_MODULE)) {
            launchPremiumActivity(context)
            return
        }

        Toast.makeText(context, "প্রিমিয়াম ডাউনলোড হচ্ছে...", Toast.LENGTH_SHORT).show()

        // ✅ SAM conversion — SplitInstallState import করা লাগবে না
        // কিন্তু listener-কে variable-এ রাখতে হবে যাতে unregister করা যায়
        val listenerHolder = arrayOfNulls<SplitInstallStateUpdatedListener>(1)

        val listener = SplitInstallStateUpdatedListener { state ->
            if (!state.moduleNames().contains(PREMIUM_MODULE)) return@SplitInstallStateUpdatedListener

            when (state.status()) {
                SplitInstallSessionStatus.INSTALLED -> {
                    listenerHolder[0]?.let { manager.unregisterListener(it) }
                    launchPremiumActivity(context)
                }
                SplitInstallSessionStatus.FAILED -> {
                    listenerHolder[0]?.let { manager.unregisterListener(it) }
                    Toast.makeText(context, "ডাউনলোড ব্যর্থ", Toast.LENGTH_LONG).show()
                }
                SplitInstallSessionStatus.CANCELED -> {
                    listenerHolder[0]?.let { manager.unregisterListener(it) }
                    Toast.makeText(context, "ডাউনলোড বাতিল", Toast.LENGTH_SHORT).show()
                }
            }
        }

        listenerHolder[0] = listener

        val request = SplitInstallRequest.newBuilder()
            .addModule(PREMIUM_MODULE)
            .build()

        manager.startInstall(request)
            .addOnSuccessListener { sessionId ->
                Log.d(TAG, "Install started. Session: $sessionId")
                manager.registerListener(listener)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Install failed", e)
                Toast.makeText(context, "ডাউনলোড শুরু করা যায়নি: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun launchPremiumActivity(context: Context) {
        try {
            val activityClass = Class.forName(PREMIUM_ACTIVITY)
            val intent = Intent(context, activityClass)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "PremiumActivity not found", e)
            Toast.makeText(context, "প্রিমিয়াম মডিউল ইনস্টল হয়নি", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Launch error", e)
            Toast.makeText(context, "লঞ্চ এরর: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}