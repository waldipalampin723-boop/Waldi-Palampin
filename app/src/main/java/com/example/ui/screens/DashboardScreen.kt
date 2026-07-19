package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.GlassmorphicCard
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    classes: List<ClassEntity>,
    students: List<StudentEntity>,
    attendance: List<AttendanceRecord>,
    onBackToProfile: () -> Unit
) {
    // Current date for dashboard reference
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val todayDateFormatted = remember { SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")).format(Date()) }

    // Stats calculations
    val totalStudents = students.size
    val totalMale = students.count { it.gender.lowercase().contains("laki") || it.gender.lowercase() == "l" }
    val totalFemale = students.count { it.gender.lowercase().contains("perempuan") || it.gender.lowercase() == "p" }

    // Selected Class Filter for Attendance Preview (0 means "Semua Kelas")
    var selectedClassIdForAttendance by remember { mutableStateOf(0) }

    val filteredStudents = remember(selectedClassIdForAttendance, students) {
        if (selectedClassIdForAttendance == 0) students
        else students.filter { it.classId == selectedClassIdForAttendance }
    }

    val filteredAttendance = remember(selectedClassIdForAttendance, attendance, todayDate) {
        val todayRecords = attendance.filter { it.date == todayDate }
        if (selectedClassIdForAttendance == 0) todayRecords
        else todayRecords.filter { it.classId == selectedClassIdForAttendance }
    }

    val presentCount = filteredAttendance.count { it.status == "Hadir" || it.status == "Terlambat" }
    val absentCount = filteredAttendance.count { it.status == "Tidak Hadir" }
    val izinSakitCount = filteredAttendance.count { it.status == "Izin" || it.status == "Sakit" }
    val totalAbscentOverall = filteredStudents.size - presentCount

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header Card with return to profile button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Selamat datang di GuruPro!",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = todayDateFormatted,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
                
                IconButton(
                    onClick = onBackToProfile,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profil",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Overall Students Counter Grid
            Text(
                text = "Statistik Siswa",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Siswa Card
                GlassmorphicCard(
                    modifier = Modifier.weight(1f),
                    containerColor = Color(0xFF1565C0)
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Siswa", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    Text("$totalStudents", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                // Laki-laki Card
                GlassmorphicCard(
                    modifier = Modifier.weight(1f),
                    containerColor = Color(0xFF00ACC1)
                ) {
                    Icon(Icons.Default.Male, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Laki-laki", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    Text("$totalMale", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                // Perempuan Card
                GlassmorphicCard(
                    modifier = Modifier.weight(1f),
                    containerColor = Color(0xFFAD1457)
                ) {
                    Icon(Icons.Default.Female, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Perempuan", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    Text("$totalFemale", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Class Distribution List
            Text(
                text = "Jumlah Siswa Per Kelas",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (classes.isEmpty()) {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color(0x13FFFFFF)
                ) {
                    Text(
                        text = "Belum ada data kelas. Tambahkan kelas di menu 'Data Kelas'.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(classes) { classObj ->
                        val count = students.count { it.classId == classObj.id }
                        GlassmorphicCard(
                            modifier = Modifier.width(130.dp),
                            containerColor = Color(0x2BFFFFFF)
                        ) {
                            Text(
                                text = classObj.className,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$count Siswa",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Live Attendance Stats (Today)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kehadiran Siswa Hari Ini",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                // Simple Class Filter Dropdown Trigger
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .clickable {
                            // Quick toggle or popup. Let's make a simple scroll horizontal selection
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (selectedClassIdForAttendance == 0) "Semua Kelas" 
                                   else classes.firstOrNull { it.id == selectedClassIdForAttendance }?.className ?: "Kelas ?",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Horizontal Class selectors for attendance
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (selectedClassIdForAttendance == 0) Color(0xFF1E88E5) else Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedClassIdForAttendance = 0 }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Semua", color = Color.White, fontSize = 11.sp)
                }

                classes.forEach { classObj ->
                    Box(
                        modifier = Modifier
                            .background(
                                if (selectedClassIdForAttendance == classObj.id) Color(0xFF1E88E5) else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedClassIdForAttendance = classObj.id }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(classObj.className, color = Color.White, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual Pie representation & statistics cards
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0x1F0B0F19)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Canvas/Chart Simulation
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Circular background track
                            drawCircle(
                                color = Color.White.copy(alpha = 0.1f),
                                radius = size.minDimension / 2
                            )
                            
                            val total = filteredStudents.size.toFloat()
                            if (total > 0f) {
                                val presentSweep = (presentCount.toFloat() / total) * 360f
                                val absentSweep = (absentCount.toFloat() / total) * 360f
                                val otherSweep = (izinSakitCount.toFloat() / total) * 360f

                                // Draw segments
                                drawArc(
                                    color = Color(0xFF4CAF50), // Present - Green
                                    startAngle = -90f,
                                    sweepAngle = presentSweep,
                                    useCenter = true
                                )
                                drawArc(
                                    color = Color(0xFFF44336), // Absent - Red
                                    startAngle = -90f + presentSweep,
                                    sweepAngle = absentSweep,
                                    useCenter = true
                                )
                                drawArc(
                                    color = Color(0xFFFFEB3B), // Izin/Sakit - Yellow
                                    startAngle = -90f + presentSweep + absentSweep,
                                    sweepAngle = otherSweep,
                                    useCenter = true
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right: Detailed numeric readout
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hadir/Terlambat: $presentCount", color = Color.White, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF44336)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Alfa/Absen: $absentCount", color = Color.White, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFEB3B)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Izin/Sakit: $izinSakitCount", color = Color.White, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)))
                            Spacer(modifier = Modifier.width(6.dp))
                            val belumAbsen = filteredStudents.size - filteredAttendance.size
                            Text("Belum Presensi: ${if (belumAbsen > 0) belumAbsen else 0}", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Quick List of Attendance of selected class
            Text(
                text = "Daftar Kehadiran Kelas",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (filteredStudents.isEmpty()) {
                Text(
                    text = "Tidak ada siswa di kelas ini.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                )
            } else {
                filteredStudents.forEach { student ->
                    val record = filteredAttendance.firstOrNull { it.studentId == student.id }
                    val statusStr = record?.status ?: "Belum Presensi"

                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        containerColor = Color(0x12FFFFFF)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(student.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                val cName = classes.firstOrNull { it.id == student.classId }?.className ?: "Tanpa Kelas"
                                Text("Kelas: $cName | Gender: ${student.gender}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (statusStr) {
                                            "Hadir" -> Color(0xFF2E7D32).copy(alpha = 0.8f)
                                            "Terlambat" -> Color(0xFFEF6C00).copy(alpha = 0.8f)
                                            "Tidak Hadir" -> Color(0xFFC62828).copy(alpha = 0.8f)
                                            "Izin", "Sakit" -> Color(0xFFF9A825).copy(alpha = 0.8f)
                                            else -> Color.Gray.copy(alpha = 0.4f)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = statusStr,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
