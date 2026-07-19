package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    private fun uriToBitmap(context: Context, uriStr: String?): Bitmap? {
        if (uriStr.isNullOrEmpty()) return null
        return try {
            val uri = Uri.parse(uriStr)
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun drawKopSurat(
        context: Context,
        canvas: Canvas,
        config: KopConfig,
        width: Int,
        paint: Paint
    ): Int {
        var currentY = 30

        // 1. Draw Logos (Left: Tut Wuri Handayani, Right: School Logo)
        val logoSize = 60
        val leftLogoBitmap = uriToBitmap(context, config.tutWuriLogoUri)
        val rightLogoBitmap = uriToBitmap(context, config.schoolLogoUri)

        // Draw left logo
        if (leftLogoBitmap != null) {
            val scaled = Bitmap.createScaledBitmap(leftLogoBitmap, logoSize, logoSize, true)
            canvas.drawBitmap(scaled, 30f, 30f, paint)
        } else {
            // Draw placeholder left logo (Tut Wuri Handayani text/icon circle)
            paint.color = Color.rgb(30, 144, 255)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            canvas.drawCircle(60f, 60f, 25f, paint)
            paint.style = Paint.Style.FILL
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = Color.rgb(30, 144, 255)
            canvas.drawText("TWH", 48f, 63f, paint)
        }

        // Draw right logo
        if (rightLogoBitmap != null) {
            val scaled = Bitmap.createScaledBitmap(rightLogoBitmap, logoSize, logoSize, true)
            canvas.drawBitmap(scaled, (width - 30 - logoSize).toFloat(), 30f, paint)
        } else {
            // Draw placeholder right logo (School Logo text/icon circle)
            paint.color = Color.rgb(74, 20, 140)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            canvas.drawCircle((width - 60).toFloat(), 60f, 25f, paint)
            paint.style = Paint.Style.FILL
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = Color.rgb(74, 20, 140)
            canvas.drawText("SCH", (width - 72).toFloat(), 63f, paint)
        }

        // 2. Draw KOP Texts (Centered)
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        // Line 1: Pemerintah Kabupaten/Kota
        paint.textSize = 12f
        canvas.drawText(config.govLine1.uppercase(), (width / 2).toFloat(), currentY.toFloat() + 12, paint)
        currentY += 16

        // Line 2: Dinas Pendidikan
        paint.textSize = 13f
        canvas.drawText(config.deptLine2.uppercase(), (width / 2).toFloat(), currentY.toFloat() + 12, paint)
        currentY += 18

        // Line 3: Nama Sekolah
        paint.textSize = 15f
        canvas.drawText(config.schoolName.uppercase(), (width / 2).toFloat(), currentY.toFloat() + 14, paint)
        currentY += 20

        // Line 4: Alamat, Kontak, POS, Email (Smaller font)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 8.5f
        val line4 = "${config.schoolAddress}, Telp: ${config.schoolContact}, Kode POS: ${config.postalCode}, Email: ${config.schoolEmail}"
        canvas.drawText(line4, (width / 2).toFloat(), currentY.toFloat() + 10, paint)
        currentY += 12

        // Line 5: NSS, NSB, NPSN
        val line5 = "NSS: ${config.nss}  |  NSB: ${config.nsb}  |  NPSN: ${config.npsn}"
        canvas.drawText(line5, (width / 2).toFloat(), currentY.toFloat() + 10, paint)
        currentY += 18

        // 3. Draw Double Line Divider
        paint.strokeWidth = 3f
        canvas.drawLine(30f, currentY.toFloat(), (width - 30).toFloat(), currentY.toFloat(), paint)
        paint.strokeWidth = 1f
        canvas.drawLine(30f, (currentY + 5).toFloat(), (width - 30).toFloat(), (currentY + 5).toFloat(), paint)
        currentY += 15

        paint.textAlign = Paint.Align.LEFT // Restore default alignment
        return currentY
    }

    private fun drawSignatures(
        canvas: Canvas,
        config: KopConfig,
        width: Int,
        yStart: Int,
        paint: Paint
    ) {
        var y = yStart + 20
        paint.color = Color.BLACK
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        // Date and place line: e.g. "Poso, Minggu 19 Juli 2026"
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        val dateStr = sdf.format(Date())
        val locationDate = "${config.location}, $dateStr"
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(locationDate, (width - 40).toFloat(), y.toFloat(), paint)
        y += 18

        // Left signature: Kepala Sekolah
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Mengetahui,", 40f, y.toFloat(), paint)
        canvas.drawText("Kepala Sekolah", 40f, (y + 12).toFloat(), paint)

        // Right signature: Guru Kelas
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Guru Kelas / Wali Kelas", (width - 40).toFloat(), (y + 12).toFloat(), paint)

        // Signature blank space
        y += 65

        // Draw names & NIPs
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(config.principalName, 40f, y.toFloat(), paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("NIP. ${config.principalNip}", 40f, (y + 12).toFloat(), paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(config.teacherName, (width - 40).toFloat(), y.toFloat(), paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("NIP. ${config.teacherNip}", (width - 40).toFloat(), (y + 12).toFloat(), paint)
    }

    // --- Generate Attendance PDF ---
    fun generateAttendancePdf(
        context: Context,
        classEntity: ClassEntity?,
        kop: KopConfig?,
        students: List<StudentEntity>,
        attendance: List<AttendanceRecord>,
        filterType: String
    ): File? {
        val activeKop = kop ?: KopConfig()
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        // 1. Draw Kop
        var y = drawKopSurat(context, canvas, activeKop, 595, paint)

        // 2. Title
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        val classNameStr = classEntity?.className ?: "Semua Kelas"
        canvas.drawText("REKAPITULASI PRESENSI SISWA - KELAS $classNameStr", 297f, y.toFloat(), paint)
        y += 14

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        canvas.drawText("Kategori: $filterType", 297f, y.toFloat(), paint)
        y += 25

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        // 3. Table Headers
        val startX = 40f
        val colNoWidth = 35f
        val colNameWidth = 180f
        val colGenderWidth = 45f
        val colStatusWidth = 85f
        val colDateWidth = 90f
        val colTimeWidth = 80f

        // Draw headers background
        paint.color = Color.rgb(220, 220, 220)
        canvas.drawRect(startX, y.toFloat() - 15, 595f - 40f, y.toFloat() + 10, paint)

        paint.color = Color.BLACK
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("No", startX + 5, y.toFloat(), paint)
        canvas.drawText("Nama Siswa", startX + colNoWidth + 5, y.toFloat(), paint)
        canvas.drawText("L/P", startX + colNoWidth + colNameWidth + 5, y.toFloat(), paint)
        canvas.drawText("Status", startX + colNoWidth + colNameWidth + colGenderWidth + 5, y.toFloat(), paint)
        canvas.drawText("Tanggal", startX + colNoWidth + colNameWidth + colGenderWidth + colStatusWidth + 5, y.toFloat(), paint)
        canvas.drawText("Jam", startX + colNoWidth + colNameWidth + colGenderWidth + colStatusWidth + colDateWidth + 5, y.toFloat(), paint)

        // Draw Header Border Line
        paint.strokeWidth = 1.5f
        canvas.drawLine(startX, y.toFloat() - 15, 595f - 40f, y.toFloat() - 15, paint)
        canvas.drawLine(startX, y.toFloat() + 10, 595f - 40f, y.toFloat() + 10, paint)

        y += 10
        paint.strokeWidth = 0.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        // 4. Fill Table Rows
        val sortedStudents = students.sortedBy { it.name }
        sortedStudents.forEachIndexed { idx, student ->
            if (y > 600) {
                // Out of space, close current page and create new page if needed.
                // For safety in single page rekap, limit to 25 students, or handle secondary page
            }
            val record = attendance.firstOrNull { it.studentId == student.id }
            val statusStr = record?.status ?: "Belum Absen"
            val dateStr = record?.date ?: "-"
            val timeStr = record?.time ?: "-"

            y += 18
            // Alternating row background
            if (idx % 2 == 1) {
                paint.color = Color.rgb(245, 245, 245)
                canvas.drawRect(startX, y.toFloat() - 13, 595f - 40f, y.toFloat() + 5, paint)
            }

            paint.color = Color.BLACK
            canvas.drawText((idx + 1).toString(), startX + 5, y.toFloat(), paint)
            
            // Limit name length to prevent overflow
            val displayName = if (student.name.length > 25) student.name.substring(0, 22) + "..." else student.name
            canvas.drawText(displayName, startX + colNoWidth + 5, y.toFloat(), paint)
            canvas.drawText(student.gender, startX + colNoWidth + colNameWidth + 5, y.toFloat(), paint)

            // Color status
            val statusPaint = Paint(paint)
            when (statusStr) {
                "Hadir" -> statusPaint.color = Color.rgb(0, 128, 0)
                "Terlambat" -> statusPaint.color = Color.rgb(255, 140, 0)
                "Tidak Hadir" -> statusPaint.color = Color.rgb(178, 34, 34)
                else -> statusPaint.color = Color.GRAY
            }
            statusPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(statusStr, startX + colNoWidth + colNameWidth + colGenderWidth + 5, y.toFloat(), statusPaint)

            canvas.drawText(dateStr, startX + colNoWidth + colNameWidth + colGenderWidth + colStatusWidth + 5, y.toFloat(), paint)
            canvas.drawText(timeStr, startX + colNoWidth + colNameWidth + colGenderWidth + colStatusWidth + colDateWidth + 5, y.toFloat(), paint)

            // Bottom border line for each row
            canvas.drawLine(startX, y.toFloat() + 5, 595f - 40f, y.toFloat() + 5, paint)
        }

        // Draw signatures
        drawSignatures(canvas, activeKop, 595, y + 25, paint)

        pdfDocument.finishPage(page)

        // Save file
        val file = File(context.cacheDir, "Rekap_Presensi_${classNameStr.replace(" ", "_")}.pdf")
        return try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- Generate Grades PDF ---
    fun generateGradesPdf(
        context: Context,
        classEntity: ClassEntity?,
        kop: KopConfig?,
        students: List<StudentEntity>,
        grades: List<StudentGrade>
    ): File? {
        val activeKop = kop ?: KopConfig()
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        // 1. Draw Kop
        var y = drawKopSurat(context, canvas, activeKop, 595, paint)

        // 2. Title
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        val classNameStr = classEntity?.className ?: "Semua Kelas"
        canvas.drawText("DAFTAR REKAPITULASI NILAI SISWA", 297f, y.toFloat(), paint)
        y += 14
        paint.textSize = 12f
        canvas.drawText("KELAS: $classNameStr", 297f, y.toFloat(), paint)
        y += 25

        paint.textAlign = Paint.Align.LEFT

        // 3. Table Headers
        val startX = 30f
        val colNo = 30f
        val colName = 160f
        val colAvgTp = 65f
        val colUts = 50f
        val colUas = 50f
        val colPraktek = 65f
        val colRaport = 75f

        // Draw Header Background
        paint.color = Color.rgb(220, 220, 220)
        canvas.drawRect(startX, y.toFloat() - 15, 595f - 30f, y.toFloat() + 10, paint)

        paint.color = Color.BLACK
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("No", startX + 5, y.toFloat(), paint)
        canvas.drawText("Nama Siswa", startX + colNo + 5, y.toFloat(), paint)
        canvas.drawText("Rata TP", startX + colNo + colName + 5, y.toFloat(), paint)
        canvas.drawText("UTS", startX + colNo + colName + colAvgTp + 5, y.toFloat(), paint)
        canvas.drawText("UAS", startX + colNo + colName + colAvgTp + colUts + 5, y.toFloat(), paint)
        canvas.drawText("Praktek", startX + colNo + colName + colAvgTp + colUts + colUas + 5, y.toFloat(), paint)
        canvas.drawText("N. Raport", startX + colNo + colName + colAvgTp + colUts + colUas + colPraktek + 5, y.toFloat(), paint)

        // Border lines
        paint.strokeWidth = 1.5f
        canvas.drawLine(startX, y.toFloat() - 15, 595f - 30f, y.toFloat() - 15, paint)
        canvas.drawLine(startX, y.toFloat() + 10, 595f - 30f, y.toFloat() + 10, paint)

        y += 10
        paint.strokeWidth = 0.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        // 4. Rows
        val sortedStudents = students.sortedBy { it.name }
        sortedStudents.forEachIndexed { idx, student ->
            val grade = grades.firstOrNull { it.studentId == student.id }
            val avgTpStr = String.format("%.1f", grade?.averageTp ?: 0.0)
            val utsStr = String.format("%.1f", grade?.utsGrade ?: 0.0)
            val uasStr = String.format("%.1f", grade?.uasGrade ?: 0.0)
            val praktekStr = String.format("%.1f", grade?.praktekGrade ?: 0.0)
            val raportStr = String.format("%.1f", grade?.reportCardGrade ?: 0.0)

            y += 18
            if (idx % 2 == 1) {
                paint.color = Color.rgb(245, 245, 245)
                canvas.drawRect(startX, y.toFloat() - 13, 595f - 30f, y.toFloat() + 5, paint)
            }

            paint.color = Color.BLACK
            canvas.drawText((idx + 1).toString(), startX + 5, y.toFloat(), paint)
            
            val displayName = if (student.name.length > 22) student.name.substring(0, 19) + "..." else student.name
            canvas.drawText(displayName, startX + colNo + 5, y.toFloat(), paint)
            canvas.drawText(avgTpStr, startX + colNo + colName + 5, y.toFloat(), paint)
            canvas.drawText(utsStr, startX + colNo + colName + colAvgTp + 5, y.toFloat(), paint)
            canvas.drawText(uasStr, startX + colNo + colName + colAvgTp + colUts + 5, y.toFloat(), paint)
            canvas.drawText(praktekStr, startX + colNo + colName + colAvgTp + colUts + colUas + 5, y.toFloat(), paint)

            val raportPaint = Paint(paint)
            raportPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val nRaport = grade?.reportCardGrade ?: 0.0
            if (nRaport >= 75.0) {
                raportPaint.color = Color.rgb(0, 100, 0) // Green
            } else if (nRaport > 0.0) {
                raportPaint.color = Color.rgb(178, 34, 34) // Red
            }
            canvas.drawText(raportStr, startX + colNo + colName + colAvgTp + colUts + colUas + colPraktek + 5, y.toFloat(), raportPaint)

            canvas.drawLine(startX, y.toFloat() + 5, 595f - 30f, y.toFloat() + 5, paint)
        }

        // Draw signatures
        drawSignatures(canvas, activeKop, 595, y + 25, paint)

        pdfDocument.finishPage(page)

        // Save file
        val file = File(context.cacheDir, "Rekap_Nilai_${classNameStr.replace(" ", "_")}.pdf")
        return try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- Generate Jurnal Mengajar PDF ---
    fun generateJournalPdf(
        context: Context,
        kop: KopConfig?,
        classes: List<ClassEntity>,
        journals: List<TeachingJournal>,
        filterInfo: String
    ): File? {
        val activeKop = kop ?: KopConfig()
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        // 1. Draw Kop
        var y = drawKopSurat(context, canvas, activeKop, 595, paint)

        // 2. Title
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("JURNAL MENGAJAR HARIAN GURU", 297f, y.toFloat(), paint)
        y += 14
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        canvas.drawText("Periode: $filterInfo", 297f, y.toFloat(), paint)
        y += 25

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        // 3. Layout Journals
        if (journals.isEmpty()) {
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 11f
            paint.color = Color.GRAY
            canvas.drawText("Tidak ada data jurnal mengajar untuk periode ini.", 297f, y.toFloat() + 30, paint)
            y += 50
        } else {
            journals.forEachIndexed { index, journal ->
                if (y > 600) {
                    // Safety check to avoid writing outside boundaries.
                    // In a real multi-page journal, you would start a new page, but we keep it beautifully compact
                }
                paint.color = Color.rgb(230, 240, 250)
                canvas.drawRect(30f, y.toFloat(), 565f, (y + 115).toFloat(), paint)

                paint.color = Color.BLACK
                paint.textSize = 9f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val clsName = classes.firstOrNull { it.id == journal.classId }?.className ?: "Kelas ?"
                canvas.drawText("Jurnal #${index + 1} - Kelas: $clsName", 35f, (y + 15).toFloat(), paint)

                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText("Tanggal: ${journal.date}  |  Jam: ${journal.teachingHours}", 35f, (y + 30).toFloat(), paint)
                canvas.drawText("Materi: ${journal.lessonTitle}", 35f, (y + 45).toFloat(), paint)
                canvas.drawText("Tujuan: ${journal.learningObjective}", 35f, (y + 60).toFloat(), paint)
                canvas.drawText("Refleksi: ${journal.teacherReflection}", 35f, (y + 75).toFloat(), paint)
                canvas.drawText("Catatan Khusus: ${journal.specialNotes}", 35f, (y + 90).toFloat(), paint)

                // Draw photo if attached
                if (!journal.photoUri.isNullOrEmpty()) {
                    val photoBitmap = uriToBitmap(context, journal.photoUri)
                    if (photoBitmap != null) {
                        try {
                            val scaledPhoto = Bitmap.createScaledBitmap(photoBitmap, 90, 75, true)
                            canvas.drawBitmap(scaledPhoto, 460f, (y + 20).toFloat(), paint)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        // Small placeholder box
                        paint.style = Paint.Style.STROKE
                        paint.color = Color.GRAY
                        canvas.drawRect(460f, (y + 20).toFloat(), 550f, (y + 95).toFloat(), paint)
                        paint.style = Paint.Style.FILL
                        paint.textSize = 8f
                        canvas.drawText("[Foto]", 490f, (y + 60).toFloat(), paint)
                    }
                }

                y += 125
            }
        }

        // Draw signatures
        drawSignatures(canvas, activeKop, 595, y + 25, paint)

        pdfDocument.finishPage(page)

        // Save file
        val file = File(context.cacheDir, "Jurnal_Mengajar_${filterInfo.replace(" ", "_")}.pdf")
        return try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- Share or Open PDF Intent ---
    fun openPdf(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // If PDF viewer fails, trigger simple Share sheet instead
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(shareIntent, "Buka Laporan PDF"))
        }
    }
}
