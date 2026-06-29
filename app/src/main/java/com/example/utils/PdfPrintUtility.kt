package com.example.utils

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DetailedReportRow(
    val date: String,
    val day: String,
    val amount: Double,
    val description: String
)

data class PeriodReportRow(
    val personName: String,
    val date: String,
    val day: String,
    val description: String,
    val amount: Double
)

object PdfPrintUtility {

    fun generateDetailedReportHtml(
        personName: String,
        expensesList: List<DetailedReportRow>,
        totalAmount: Double
    ): String {
        val todayStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("ar")).format(Date())
        
        val rowsHtml = StringBuilder()
        expensesList.forEachIndexed { index, item ->
            val desc = item.description.ifEmpty { "—" }
            rowsHtml.append("""
                <tr>
                    <td style="text-align: center;">${index + 1}</td>
                    <td>${item.date}</td>
                    <td>${item.day}</td>
                    <td>$desc</td>
                    <td style="text-align: left; font-weight: bold;">${String.format(Locale.US, "%,.2f", item.amount)}</td>
                </tr>
            """.trimIndent())
        }

        return """
            <!DOCTYPE html>
            <html dir="rtl" lang="ar">
            <head>
                <meta charset="utf-8">
                <title>تقرير تفصيلي - $personName</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        direction: rtl;
                        text-align: right;
                        padding: 15px;
                        color: #2c3e50;
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 25px;
                        border-bottom: 3px solid #2e7d32;
                        padding-bottom: 12px;
                    }
                    .header h1 {
                        margin: 0;
                        color: #2e7d32;
                        font-size: 24px;
                    }
                    .header p {
                        margin: 5px 0 0 0;
                        color: #7f8c8d;
                        font-size: 13px;
                    }
                    .meta-box {
                        background-color: #f1f8e9;
                        border-right: 5px solid #2e7d32;
                        padding: 12px 18px;
                        border-radius: 4px;
                        margin-bottom: 20px;
                        font-size: 15px;
                        display: flex;
                        justify-content: space-between;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 15px;
                    }
                    th, td {
                        border: 1px solid #bdc3c7;
                        padding: 10px 12px;
                        font-size: 14px;
                    }
                    th {
                        background-color: #2e7d32;
                        color: white;
                        font-weight: bold;
                    }
                    tr:nth-child(even) {
                        background-color: #f9f9f9;
                    }
                    .total-row {
                        background-color: #e8f5e9 !important;
                        font-weight: bold;
                        color: #2e7d32;
                        font-size: 16px;
                    }
                    .footer {
                        margin-top: 40px;
                        text-align: center;
                        font-size: 11px;
                        color: #95a5a6;
                        border-top: 1px solid #ecf0f1;
                        padding-top: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>دفتر المصاريف - تقرير تفصيلي</h1>
                    <p>تاريخ استخراج التقرير: $todayStr</p>
                </div>
                
                <div class="meta-box">
                    <div><strong>الاسم:</strong> $personName</div>
                    <div><strong>عدد العمليات:</strong> ${expensesList.size}</div>
                </div>

                <table>
                    <thead>
                        <tr>
                            <th style="width: 50px; text-align: center;">م</th>
                            <th>التاريخ</th>
                            <th>اليوم</th>
                            <th>البيان / الوصف</th>
                            <th style="text-align: left; width: 120px;">المبلغ</th>
                        </tr>
                    </thead>
                    <tbody>
                        $rowsHtml
                        <tr class="total-row">
                            <td colspan="4" style="text-align: right;">إجمالي العمليات</td>
                            <td style="text-align: left;">${String.format(Locale.US, "%,.2f", totalAmount)}</td>
                        </tr>
                    </tbody>
                </table>

                <div class="footer">
                    تطوير : سامي القادري 777484160
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun generatePeriodReportHtml(
        startDate: String,
        endDate: String,
        expensesList: List<PeriodReportRow>,
        totalAmount: Double
    ): String {
        val todayStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("ar")).format(Date())
        
        val rowsHtml = StringBuilder()
        expensesList.forEachIndexed { index, item ->
            val desc = item.description.ifEmpty { "—" }
            rowsHtml.append("""
                <tr>
                    <td style="text-align: center;">${index + 1}</td>
                    <td>${item.personName}</td>
                    <td>${item.date}</td>
                    <td>${item.day}</td>
                    <td>$desc</td>
                    <td style="text-align: left; font-weight: bold;">${String.format(Locale.US, "%,.2f", item.amount)}</td>
                </tr>
            """.trimIndent())
        }

        return """
            <!DOCTYPE html>
            <html dir="rtl" lang="ar">
            <head>
                <meta charset="utf-8">
                <title>تقرير إجمالي الفترة</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        direction: rtl;
                        text-align: right;
                        padding: 15px;
                        color: #2c3e50;
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 25px;
                        border-bottom: 3px solid #1976d2;
                        padding-bottom: 12px;
                    }
                    .header h1 {
                        margin: 0;
                        color: #1976d2;
                        font-size: 24px;
                    }
                    .header p {
                        margin: 5px 0 0 0;
                        color: #7f8c8d;
                        font-size: 13px;
                    }
                    .meta-box {
                        background-color: #e3f2fd;
                        border-right: 5px solid #1976d2;
                        padding: 12px 18px;
                        border-radius: 4px;
                        margin-bottom: 20px;
                        font-size: 15px;
                        display: flex;
                        justify-content: space-between;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 15px;
                    }
                    th, td {
                        border: 1px solid #bdc3c7;
                        padding: 10px 12px;
                        font-size: 14px;
                    }
                    th {
                        background-color: #1976d2;
                        color: white;
                        font-weight: bold;
                    }
                    tr:nth-child(even) {
                        background-color: #f9f9f9;
                    }
                    .total-row {
                        background-color: #e3f2fd !important;
                        font-weight: bold;
                        color: #1976d2;
                        font-size: 16px;
                    }
                    .footer {
                        margin-top: 40px;
                        text-align: center;
                        font-size: 11px;
                        color: #95a5a6;
                        border-top: 1px solid #ecf0f1;
                        padding-top: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>دفتر المصاريف - تقرير الفترة</h1>
                    <p>تاريخ استخراج التقرير: $todayStr</p>
                </div>
                
                <div class="meta-box">
                    <div><strong>الفترة:</strong> من $startDate إلى $endDate</div>
                    <div><strong>عدد العمليات:</strong> ${expensesList.size}</div>
                </div>

                <table>
                    <thead>
                        <tr>
                            <th style="width: 50px; text-align: center;">م</th>
                            <th>الاسم</th>
                            <th>التاريخ</th>
                            <th>اليوم</th>
                            <th>البيان / الوصف</th>
                            <th style="text-align: left; width: 120px;">المبلغ</th>
                        </tr>
                    </thead>
                    <tbody>
                        $rowsHtml
                        <tr class="total-row">
                            <td colspan="5" style="text-align: right;">إجمالي العمليات في الفترة</td>
                            <td style="text-align: left;">${String.format(Locale.US, "%,.2f", totalAmount)}</td>
                        </tr>
                    </tbody>
                </table>

                <div class="footer">
                    تطوير : سامي القادري 777484160
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun printHtml(context: Context, htmlContent: String, jobName: String) {
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                val printAdapter = webView.createPrintDocumentAdapter(jobName)
                printManager.print(
                    jobName,
                    printAdapter,
                    PrintAttributes.Builder().build()
                )
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
    }
}
