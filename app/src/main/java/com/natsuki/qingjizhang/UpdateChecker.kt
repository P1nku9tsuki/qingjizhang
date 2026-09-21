package com.natsuki.qingjizhang

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val tagName: String,
    val version: String,
    val htmlUrl: String,
    val releaseNotes: String,
    val publishedAt: String
)

object UpdateChecker {

    fun getCurrentVersion(context: Context): String {
        return try {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

    suspend fun checkForUpdate(
        currentVersion: String,
        repo: String
    ): UpdateInfo? = withContext(Dispatchers.IO) {
        if (repo.isBlank() || !repo.contains("/")) return@withContext null

        try {
            val url = URL("https://api.github.com/repos/$repo/releases/latest")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "QingJiZhang-App")
            conn.setRequestProperty("Accept", "application/vnd.github+json")
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            if (conn.responseCode != 200) return@withContext null

            val text = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()

            val json = JSONObject(text)
            val tag = json.optString("tag_name", "")
            val htmlUrl = json.optString("html_url", "")
            val body = json.optString("body", "")
            val published = json.optString("published_at", "")

            if (tag.isBlank()) return@withContext null

            val remoteVersion = tag.removePrefix("v").removePrefix("V").trim()

            if (isNewer(remoteVersion, currentVersion)) {
                UpdateInfo(
                    tagName = tag,
                    version = remoteVersion,
                    htmlUrl = htmlUrl,
                    releaseNotes = body,
                    publishedAt = published
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun isNewer(remote: String, current: String): Boolean {
        val r = remote.split(".").mapNotNull { it.toIntOrNull() }
        val c = current.split(".").mapNotNull { it.toIntOrNull() }
        if (r.isEmpty()) return false
        val max = maxOf(r.size, c.size)
        for (i in 0 until max) {
            val rv = r.getOrElse(i) { 0 }
            val cv = c.getOrElse(i) { 0 }
            if (rv > cv) return true
            if (rv < cv) return false
        }
        return false
    }

    fun openReleasePage(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
