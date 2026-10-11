package `in`.emergencyWbApp.essentials.ui



import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * API থেকে আসা icon name → Material ImageVector.
 * নতুন icon দরকার হলে শুধু এখানে একটা case যোগ করবে।
 */
fun iconFor(name: String): ImageVector = when (name.lowercase()) {
    "home"    -> Icons.Default.Home
    "medical" -> Icons.Default.MedicalServices
    "heart"   -> Icons.Default.Favorite
    "person"  -> Icons.Default.Person
    else      -> Icons.Default.Circle
}