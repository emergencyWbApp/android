package `in`.emergencyWbApp.essentials.ui.config



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.emergencyWbApp.essentials.data.remote.EmergencyApi
import `in`.emergencyWbApp.essentials.data.remote.dto.ConfigDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class ConfigUiState {
    object Loading : ConfigUiState()
    data class Success(val config: ConfigDto, val rawJson: String) : ConfigUiState()
    data class Error(val message: String) : ConfigUiState()
}

@HiltViewModel                                          // 🔑 Hilt annotation
class ConfigTestViewModel @Inject constructor(          // 🔑 Inject constructor
    private val api: EmergencyApi                       // 🔑 Hilt inject
) : ViewModel() {

    private val _state = MutableStateFlow<ConfigUiState>(ConfigUiState.Loading)
    val state: StateFlow<ConfigUiState> = _state.asStateFlow()

    init {
        loadConfig()
    }

    fun loadConfig() {
        viewModelScope.launch {
            _state.value = ConfigUiState.Loading
            try {
                val result = withContext(Dispatchers.IO) {
                    api.getConfig()
                }
                val rawJson = buildString {
                    appendLine("{")
                    appendLine("""  "app_name": "${result.appName}",""")
                    appendLine("""  "tagline": "${result.tagline}",""")
                    appendLine("""  "emergency_number": "${result.emergencyNumber}",""")
                    appendLine("""  "content_version": "${result.contentVersion}",""")
                    appendLine("""  "schema_version": "${result.schemaVersion}",""")
                    appendLine("""  "disclaimer": "${result.disclaimer}",""")
                    appendLine("""  "modules": {""")
                    result.modules.forEach { (k, v) ->
                        appendLine("""    "$k": { "enabled": ${v.enabled}, "free": ${v.free} },""")
                    }
                    appendLine("  }")
                    append("}")
                }
                _state.value = ConfigUiState.Success(result, rawJson)
            } catch (e: Exception) {
                _state.value = ConfigUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}