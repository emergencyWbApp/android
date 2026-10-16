package `in`.emergencyWbApp.essentials.data.remote

import `in`.emergencyWbApp.essentials.BuildConfig


object ApiConfig {

    private const val LOCAL_BASE  = "http://100.115.92.197:8000/api/v1/"
    private const val LOCAL_ICON  = "http://100.115.92.197:8000/icons/"

    private const val LIVE_BASE   = "https://emergencywbapp.modernseva.in/api/v1/"
    private const val LIVE_ICON   = "https://emergencywbapp.modernseva.in/icons/"

    val BASE_URL: String      = if (BuildConfig.DEBUG) LOCAL_BASE else LIVE_BASE
    val ICON_BASE_URL: String = if (BuildConfig.DEBUG) LOCAL_ICON else LIVE_ICON


    const val TIMEOUT_SECONDS = 30L
}