package `in`.emergencyWbApp.essentials.data.remote.dto


import com.google.gson.annotations.SerializedName

data class ConfigDto(
    @SerializedName("app_name")         val appName: String,
    @SerializedName("tagline")          val tagline: String,
    @SerializedName("emergency_number") val emergencyNumber: String,
    @SerializedName("content_version")  val contentVersion: String,
    @SerializedName("schema_version")   val schemaVersion: String,
    @SerializedName("disclaimer")       val disclaimer: String,
    @SerializedName("modules")          val modules: Map<String, ModuleDto>,
    @SerializedName("theme")            val theme: ThemeDto? = null   // 🆕
)

data class ModuleDto(
    @SerializedName("enabled") val enabled: Boolean,
    @SerializedName("free")    val free: Boolean
)