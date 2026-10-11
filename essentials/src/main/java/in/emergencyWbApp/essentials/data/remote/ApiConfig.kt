package `in`.emergencyWbApp.essentials.data.remote


object ApiConfig {

    // ⚠️ Emulator-এর জন্য: 10.0.2.2 = Host machine's localhost
    // Real device-এর জন্য: আপনার PC-র IP ব্যবহার করুন (যেমন 192.168.1.5)
    // Production-এর জন্য: https://emergencywbapp.modernseva.in/

//    const val BASE_URL = "http://127.0.0.1:8000/api/v1/"

    const val BASE_URL = "https://emergencywbapp.modernseva.in/api/v1/"

    const val TIMEOUT_SECONDS = 30L
}