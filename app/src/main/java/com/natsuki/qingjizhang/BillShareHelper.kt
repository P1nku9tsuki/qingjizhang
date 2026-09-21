package com.natsuki.qingjizhang

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BillShareHelper {

    fun generateShareImage(
        context: Context,
        month: String,
        balance: Double,
        income: Double,
        expense: Double,
        bills: List<BillEntity>,
        primaryColor: Int
    ): File {
        val size = 1080
        val padding = 96f
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val displayPrimary = ensureBright(primaryColor, minLuminance = 0.55f)
        val bgColor = blendColor(0xFF0A0A0A.toInt(), displayPrimary, 0.18f)
        canvas.drawColor(bgColor)

        //
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = displayPrimary
        }
        canvas.drawCircle(padding + 12f, padding + 12f, 12f, dotPaint)

        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("轻记账", padding + 40f, padding + 26f, brandPaint)

        val monthPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x88FFFFFF.toInt()
            textSize = 36f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(month, size - padding, padding + 26f, monthPaint)

        //
        val linePaint = Paint().apply {
            color = displayPrimary
            strokeWidth = 3f
            alpha = 150
        }
        canvas.drawLine(padding, padding + 90f, size - padding, padding + 90f, linePaint)

        //
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x88FFFFFF.toInt()
            textSize = 32f
        }
        canvas.drawText("结余", padding, size * 0.42f, labelPaint)

        val balancePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = displayPrimary
            textSize = 140f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val balanceText = "¥ $balance"
        val maxWidth = size - padding * 2
        var balanceSize = 140f
        while (balancePaint.measureText(balanceText) > maxWidth && balanceSize > 60f) {
            balanceSize -= 4f
            balancePaint.textSize = balanceSize
        }
        canvas.drawText(balanceText, padding, size * 0.58f, balancePaint)

        //
        val colTopLabelY = size * 0.78f
        val colAmountY = size * 0.88f

        canvas.drawText("收入", padding, colTopLabelY, labelPaint)
        val incomePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF81C784.toInt()
            textSize = 56f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("+¥$income", padding, colAmountY, incomePaint)

        val rightLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x88FFFFFF.toInt()
            textSize = 32f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("支出", size - padding, colTopLabelY, rightLabelPaint)

        val expensePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFE57373.toInt()
            textSize = 56f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("-¥$expense", size - padding, colAmountY, expensePaint)

        //
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x55FFFFFF.toInt()
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date()),
            size / 2f,
            size - padding / 2f,
            footerPaint
        )

        //
        val cacheDir = File(context.cacheDir, "share")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val file = File(cacheDir, "bill_share_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        bitmap.recycle()
        return file
    }

    private fun ensureBright(color: Int, minLuminance: Float): Int {
        val r = Color.red(color) / 255f
        val g = Color.green(color) / 255f
        val b = Color.blue(color) / 255f
        val lum = 0.299f * r + 0.587f * g + 0.114f * b
        if (lum >= minLuminance) return color
        // 等比放大亮度
        val factor = minLuminance / lum
        val nr = (r * factor).coerceAtMost(1f)
        val ng = (g * factor).coerceAtMost(1f)
        val nb = (b * factor).coerceAtMost(1f)
        return Color.rgb((nr * 255).toInt(), (ng * 255).toInt(), (nb * 255).toInt())
    }

    private fun blendColor(base: Int, overlay: Int, ratio: Float): Int {
        val r = (Color.red(base) * (1 - ratio) + Color.red(overlay) * ratio).toInt()
        val g = (Color.green(base) * (1 - ratio) + Color.green(overlay) * ratio).toInt()
        val b = (Color.blue(base) * (1 - ratio) + Color.blue(overlay) * ratio).toInt()
        return Color.rgb(r, g, b)
    }


    fun shareImage(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "分享账单"))
    }


    fun exportCsv(context: Context, uri: Uri, bills: List<BillEntity>): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                val sb = StringBuilder()
                sb.append("日期,类型,分类,备注,金额\n")
                bills.forEach { bill ->
                    val type = if (bill.amount < 0) "支出" else "收入"
                    val amount = kotlin.math.abs(bill.amount)
                    val safeTitle = bill.title.replace("\"", "\"\"")
                    sb.append("\"${bill.date}\",")
                    sb.append("\"$type\",")
                    sb.append("\"${bill.category}\",")
                    sb.append("\"$safeTitle\",")
                    sb.append("$amount\n")
                }
                output.write(sb.toString().toByteArray(Charsets.UTF_8))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}