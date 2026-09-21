package com.natsuki.qingjizhang

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

object BackupManager {

    fun exportToUri(context: Context, uri: Uri, bills: List<BillEntity>): Boolean {
        return try {
            val jsonArray = JSONArray()
            bills.forEach { bill ->
                val obj = JSONObject()
                obj.put("title", bill.title)
                obj.put("amount", bill.amount)
                obj.put("category", bill.category)
                obj.put("date", bill.date)
                obj.put("isExpense", bill.isExpense)
                jsonArray.put(obj)
            }
            val root = JSONObject()
            root.put("version", 1)
            root.put("exportTime", System.currentTimeMillis())
            root.put("bills", jsonArray)

            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(root.toString(2).toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    fun importFromUri(context: Context, uri: Uri): List<BillEntity> {
        val bills = mutableListOf<BillEntity>()
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { input ->
                input.bufferedReader().readText()
            } ?: return emptyList()

            val root = JSONObject(json)
            val array = root.optJSONArray("bills") ?: return emptyList()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                bills.add(
                    BillEntity(
                        title = obj.optString("title", "未命名"),
                        amount = obj.optDouble("amount", 0.0),
                        category = obj.optString("category", "其他"),
                        date = obj.optString("date", ""),
                        isExpense = obj.optBoolean("isExpense", true)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bills
    }
}

