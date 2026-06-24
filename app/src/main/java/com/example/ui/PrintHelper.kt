package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.data.CartItem
import com.example.data.OrderEntity
import java.util.Locale

object PrintHelper {

    fun printSlip(
        context: Context,
        order: OrderEntity,
        itemsList: List<CartItem>,
        formattedDate: String
    ) {
        val activity = context as? Activity ?: return
        activity.runOnUiThread {
            val webView = WebView(context)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                    val jobName = "Daim Pharmacy Slip #${order.id}"
                    val printAdapter = webView.createPrintDocumentAdapter("DaimPharmacyOrder_${order.id}")
                    printManager.print(
                        jobName,
                        printAdapter,
                        PrintAttributes.Builder().build()
                    )
                }
            }

            var grandTotal = 0.0
            val itemsHtml = StringBuilder()
            for (item in itemsList) {
                val itemTotal = item.price * item.quantity
                grandTotal += itemTotal
                itemsHtml.append("""
                    <tr>
                        <td style="padding: 6px 0; border-bottom: 1px solid #eee;">
                            <div style="font-weight: bold; color: #000;">${escapeHtml(item.medicineName)}</div>
                            <div style="font-size: 11px; color: #555;">Formula: ${escapeHtml(item.formula)}</div>
                        </td>
                        <td class="text-right" style="padding: 6px 0; border-bottom: 1px solid #eee;">
                            ${item.quantity} x Rs. ${String.format(Locale.US, "%.2f", item.price)}
                        </td>
                        <td class="text-right" style="padding: 6px 0; border-bottom: 1px solid #eee; font-weight: bold;">
                            Rs. ${String.format(Locale.US, "%.2f", itemTotal)}
                        </td>
                    </tr>
                """.trimIndent())
            }

            val formattedTotal = String.format(Locale.US, "%.2f", grandTotal)

            val html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <title>Daim Pharmacy Slip</title>
                    <style>
                        body {
                            font-family: 'Courier New', Courier, monospace;
                            color: #000;
                            background-color: #fff;
                            padding: 20px;
                            margin: 0;
                            font-size: 14px;
                        }
                        .header {
                            text-align: center;
                            margin-bottom: 20px;
                        }
                        .logo {
                            font-size: 28px;
                            margin-bottom: 5px;
                            display: inline-block;
                            border: 2px solid #000;
                            border-radius: 50%;
                            width: 40px;
                            height: 40px;
                            line-height: 38px;
                            font-weight: bold;
                        }
                        .pharmacy-name {
                            font-size: 20px;
                            font-weight: bold;
                            text-transform: uppercase;
                            letter-spacing: 1px;
                            margin: 2px 0;
                        }
                        .subtitle {
                            font-size: 12px;
                            text-transform: uppercase;
                            letter-spacing: 1px;
                            color: #333;
                        }
                        .divider {
                            border-top: 1px dashed #000;
                            margin: 15px 0;
                        }
                        .info-table {
                            width: 100%;
                            margin-bottom: 15px;
                        }
                        .info-table td {
                            padding: 3px 0;
                            font-size: 13px;
                        }
                        .items-table {
                            width: 100%;
                            border-collapse: collapse;
                            margin: 15px 0;
                        }
                        .items-table th {
                            border-bottom: 1px solid #000;
                            text-align: left;
                            padding: 6px 0;
                            font-size: 13px;
                            text-transform: uppercase;
                        }
                        .items-table td {
                            vertical-align: top;
                            font-size: 13px;
                        }
                        .text-right {
                            text-align: right;
                        }
                        .total-container {
                            margin-top: 15px;
                            text-align: right;
                            font-size: 16px;
                            font-weight: bold;
                        }
                        .footer {
                            text-align: center;
                            margin-top: 30px;
                            font-size: 11px;
                            text-transform: uppercase;
                            letter-spacing: 0.5px;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <div class="logo">&#10010;</div>
                        <div class="pharmacy-name">Daim Pharmacy</div>
                        <div class="subtitle">Order Preparation Slip</div>
                    </div>
                    
                    <table class="info-table">
                        <tr>
                            <td><strong>Order Number:</strong> #${order.id}</td>
                            <td class="text-right"><strong>Status:</strong> ${order.status.uppercase()}</td>
                        </tr>
                        <tr>
                            <td><strong>Doctor Name:</strong> Dr. ${order.doctorName}</td>
                            <td class="text-right"><strong>Date & Time:</strong> $formattedDate</td>
                        </tr>
                    </table>
                    
                    <div class="divider"></div>
                    
                    <table class="items-table">
                        <thead>
                            <tr>
                                <th>Medicine Description</th>
                                <th class="text-right">Qty & Price</th>
                                <th class="text-right">Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            $itemsHtml
                        </tbody>
                    </table>
                    
                    <div class="divider"></div>
                    
                    <div class="total-container">
                        TOTAL AMOUNT: Rs. $formattedTotal
                    </div>
                    
                    <div class="footer">
                        * Daim Pharmacy - Quality Healthcare Services *<br>
                        This slip is verified for medication preparation
                    </div>
                </body>
                </html>
            """.trimIndent()

            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }
    }

    fun shareOrderAsText(
        context: Context,
        order: OrderEntity,
        itemsList: List<CartItem>,
        formattedDate: String
    ) {
        val sb = StringBuilder()
        sb.append("===============================\n")
        sb.append("        DAIM PHARMACY          \n")
        sb.append("    ORDER PREPARATION SLIP     \n")
        sb.append("===============================\n")
        sb.append("Order Number: #${order.id}\n")
        sb.append("Doctor Name:  Dr. ${order.doctorName}\n")
        sb.append("Date & Time:  $formattedDate\n")
        sb.append("Status:       ${order.status.uppercase()}\n")
        sb.append("-------------------------------\n\n")
        
        var grandTotal = 0.0
        for ((index, item) in itemsList.withIndex()) {
            val itemTotal = item.price * item.quantity
            grandTotal += itemTotal
            sb.append("${index + 1}. ${item.medicineName}\n")
            sb.append("   Formula: ${item.formula}\n")
            sb.append("   Qty & Price: ${item.quantity} x Rs. ${String.format(Locale.US, "%.2f", item.price)}\n")
            sb.append("   Subtotal: Rs. ${String.format(Locale.US, "%.2f", itemTotal)}\n\n")
        }
        
        sb.append("-------------------------------\n")
        sb.append("TOTAL AMOUNT: Rs. ${String.format(Locale.US, "%.2f", grandTotal)}\n")
        sb.append("===============================\n")
        sb.append("* Daim Pharmacy - Quality Healthcare Services *\n")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share Order Slip via")
        context.startActivity(shareIntent)
    }

    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}
