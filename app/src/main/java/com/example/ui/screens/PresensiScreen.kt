package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.*
import com.example.utils.PdfGenerator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresensiScreen(
    classes: List<ClassEntity>,
    students: List<StudentEntity>,
    attendance: List<AttendanceRecord>,
    settings: SystemSettings?,
    kop: KopConfig?,
    onSaveAttendance: (List<AttendanceRecord>) -> Unit,
    onSetAllPresent: (Int, String, String, List<Int>) -> Unit,
    onResetAttendance: (Int, String) -> Unit,
    onUpdateGraceTime: (Int, String) -> Unit
) {
    val context = LocalContext.current
    val activeSettings = settings ?: SystemSettings()

    // 1. Date & Time States
    val sdfDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val sdfTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val currentDate = remember { sdfDate.format(Date()) }
    val currentTime = remember { sdfTime.format(Date()) }

    // 2. Class Selection State
    var selectedClassId by remember { mutableStateOf(if (classes.isNotEmpty()) classes[0].id else 0) }
    var classDropdownExpanded by remember { mutableStateOf(false) }

    // 3. Edit Grace Time State
    var isEditingGrace by remember { mutableStateOf(false) }
    var graceMinutesInput by remember(activeSettings) { mutableStateOf(activeSettings.lateGraceMinutes.toString()) }
    var graceTimeStrInput by remember(activeSettings) { mutableStateOf(activeSettings.lateGraceTimeStr) }

    // Filtered students & attendance list
    val currentClassStudents = remember(selectedClassId, students) {
        students.filter { it.classId == selectedClassId }
    }
    val currentClassAttendance = remember(selectedClassId, attendance, currentDate) {
        attendance.filter { it.classId == selectedClassId && it.date == currentDate }
    }

    // Temporary list to draft changes
    val draftAttendance = remember(currentClassStudents, currentClassAttendance) {
        val list = mutableStateListOf<AttendanceRecord>()
        currentClassStudents.forEach { student ->
            val existing = currentClassAttendance.firstOrNull { it.studentId == student.id }
            list.add(
                existing ?: AttendanceRecord(
                    studentId = student.id,
                    classId = selectedClassId,
                    date = currentDate,
                    time = currentTime,
                    status = "Belum Absen",
                    isLate = false
                )
            )
        }
        list
    }

    // Export PDF Dialog
    var showPdfDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Class Selector
        Text(
            text = "Menu Presensi Harian",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tanggal: $currentDate | Jam: $currentTime",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (classes.isEmpty()) {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Silakan tambahkan data kelas terlebih dahulu di menu Data Kelas.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        } else {
            // Class Dropdown Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExposedDropdownMenuBox(
                    expanded = classDropdownExpanded,
                    onExpandedChange = { classDropdownExpanded = !classDropdownExpanded },
                    modifier = Modifier.weight(1.5f)
                ) {
                    val selectedClassName = classes.firstOrNull { it.id == selectedClassId }?.className ?: "Pilih Kelas"
                    OutlinedTextField(
                        value = selectedClassName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Kelas", color = Color.White) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            focusedContainerColor = Color(0x33000000),
                            unfocusedContainerColor = Color(0x14000000)
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = classDropdownExpanded,
                        onDismissRequest = { classDropdownExpanded = false }
                    ) {
                        classes.forEach { classEntity ->
                            DropdownMenuItem(
                                text = { Text(classEntity.className) },
                                onClick = {
                                    selectedClassId = classEntity.id
                                    classDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Configure Grace Cut-off Time button
                IconButton(
                    onClick = { isEditingGrace = !isEditingGrace },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .size(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Batas Waktu",
                        tint = Color.White
                    )
                }
            }

            // Batas Waktu Editor Panel
            if (isEditingGrace) {
                Spacer(modifier = Modifier.height(10.dp))
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color(0xFF1E2A38)
                ) {
                    Text(
                        text = "Setting Batas Waktu Terlambat",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassmorphicTextField(
                            value = graceTimeStrInput,
                            onValueChange = { graceTimeStrInput = it },
                            label = "Jam Masuk (e.g. 07:30)",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        GlassmorphicTextField(
                            value = graceMinutesInput,
                            onValueChange = { graceMinutesInput = it },
                            label = "Toleransi (Menit)",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val mins = graceMinutesInput.toIntOrNull() ?: 30
                            onUpdateGraceTime(mins, graceTimeStrInput)
                            isEditingGrace = false
                            Toast.makeText(context, "Batas waktu diperbarui!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Simpan Batas Waktu")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Actions Block (Hadir Semua, Reset, Print PDF)
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0x26FFFFFF)
            ) {
                Text(
                    text = "Aksi Cepat & Cetak Laporan",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val ids = currentClassStudents.map { it.id }
                            onSetAllPresent(selectedClassId, currentDate, currentTime, ids)
                            Toast.makeText(context, "Semua siswa ditandai Hadir!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hadir Semua", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            onResetAttendance(selectedClassId, currentDate)
                            Toast.makeText(context, "Presensi hari ini di-reset!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { showPdfDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cetak PDF", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Student list with attendance status buttons
            Text(
                text = "Daftar Siswa Kelas",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (currentClassStudents.isEmpty()) {
                Text(
                    text = "Belum ada siswa di kelas ini. Tambahkan siswa di menu 'Data Siswa'.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(20.dp)
                )
            } else {
                currentClassStudents.forEachIndexed { sIdx, student ->
                    val draftRecord = draftAttendance.getOrNull(sIdx)
                    val activeStatus = draftRecord?.status ?: "Belum Absen"

                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        containerColor = Color(0x1AFFFFFF)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = student.name,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Gender: ${student.gender}",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 11.sp
                                    )
                                }

                                // Quick status preview bubble
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (activeStatus) {
                                                "Hadir" -> Color(0xFF2E7D32)
                                                "Terlambat" -> Color(0xFFEF6C00)
                                                "Tidak Hadir" -> Color(0xFFC62828)
                                                "Izin", "Sakit" -> Color(0xFFF9A825)
                                                else -> Color.Gray
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = activeStatus,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Grid of status selector buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val statuses = listOf("Hadir", "Terlambat", "Tidak Hadir", "Izin", "Sakit")
                                statuses.forEach { status ->
                                    val isSelected = activeStatus == status
                                    Box(
                                        modifier = Modifier
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .background(
                                                if (isSelected) {
                                                    when (status) {
                                                        "Hadir" -> Color(0xFF2E7D32)
                                                        "Terlambat" -> Color(0xFFEF6C00)
                                                        "Tidak Hadir" -> Color(0xFFC62828)
                                                        "Izin", "Sakit" -> Color(0xFFF9A825)
                                                        else -> Color.Gray
                                                    }
                                                } else Color.White.copy(alpha = 0.05f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                // Calculate if Late cutoff triggered based on limit settings
                                                var finalStatus = status
                                                var isLateTriggered = false
                                                if (status == "Hadir") {
                                                    try {
                                                        val cutoff = activeSettings.lateGraceTimeStr.split(":")
                                                        val now = currentTime.split(":")
                                                        if (cutoff.size == 2 && now.size == 2) {
                                                            val cutoffMins = cutoff[0].toInt() * 60 + cutoff[1].toInt()
                                                            val nowMins = now[0].toInt() * 60 + now[1].toInt()
                                                            if (nowMins > cutoffMins + activeSettings.lateGraceMinutes) {
                                                                finalStatus = "Terlambat"
                                                                isLateTriggered = true
                                                                Toast.makeText(context, "${student.name} Terlambat!", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }

                                                draftAttendance[sIdx] = draftRecord?.copy(
                                                    status = finalStatus,
                                                    time = currentTime,
                                                    isLate = isLateTriggered || finalStatus == "Terlambat"
                                                ) ?: AttendanceRecord(
                                                    studentId = student.id,
                                                    classId = selectedClassId,
                                                    date = currentDate,
                                                    time = currentTime,
                                                    status = finalStatus,
                                                    isLate = isLateTriggered || finalStatus == "Terlambat"
                                                )
                                            }
                                            .padding(horizontal = 6.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when (status) {
                                                "Tidak Hadir" -> "Alfa"
                                                else -> status
                                            },
                                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                            fontSize = 9.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Save Attendance Button
                GlassmorphicButton(
                    onClick = {
                        onSaveAttendance(draftAttendance.toList())
                        Toast.makeText(context, "Data Presensi Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                    },
                    containerColor = Color(0xFF2E7D32),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Presensi", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // --- PDF Print Options Dialog ---
    if (showPdfDialog) {
        val selectedClassObj = classes.firstOrNull { it.id == selectedClassId }
        AlertDialog(
            onDismissRequest = { showPdfDialog = false },
            title = { Text("Pilih Jangkauan Cetak PDF") },
            text = {
                Column {
                    Text("Pilih tipe format laporan cetak presensi untuk Kelas ${selectedClassObj?.className}:")
                    Spacer(modifier = Modifier.height(12.dp))
                    listOf("Pertemuan Hari Ini", "Per Minggu", "Per Bulan", "Kolektif Kelas").forEach { opt ->
                        Button(
                            onClick = {
                                showPdfDialog = false
                                val reportFile = PdfGenerator.generateAttendancePdf(
                                    context,
                                    selectedClassObj,
                                    kop,
                                    currentClassStudents,
                                    currentClassAttendance,
                                    opt
                                )
                                if (reportFile != null) {
                                    PdfGenerator.openPdf(context, reportFile)
                                } else {
                                    Toast.makeText(context, "Gagal membuat PDF", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x1F0B0F19)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        ) {
                            Text(opt, color = Color.Black)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPdfDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}
