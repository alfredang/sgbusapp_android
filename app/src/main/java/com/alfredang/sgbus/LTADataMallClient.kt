package com.alfredang.sgbus

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class LTADataMallClient(
    private val accountKey: String = BuildConfig.LTA_ACCOUNT_KEY,
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    private val baseUrl = "https://datamall2.mytransport.sg/ltaodataservice/v3/BusArrival".toHttpUrl()

    suspend fun fetchArrivals(busStopCode: String): List<BusService> = withContext(Dispatchers.IO) {
        val url = baseUrl.newBuilder()
            .addQueryParameter("BusStopCode", busStopCode)
            .build()
        val request = Request.Builder()
            .url(url)
            .header("AccountKey", accountKey)
            .header("accept", "application/json")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("LTA DataMall returned HTTP ${response.code}")
            }
            val body = response.body?.string() ?: throw IOException("LTA DataMall returned an empty body")
            json.decodeFromString<BusArrivalResponse>(body).services
        }
    }
}
