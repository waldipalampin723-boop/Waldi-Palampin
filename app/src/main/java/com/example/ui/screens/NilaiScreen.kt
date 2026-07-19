package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.*
import com.example.utils.PdfGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NilaiScreen(
    classes: List<ClassEntity>,
    students: List<StudentEntity>,
    grades: List<StudentGrade>,
    settings: SystemSettings?,
    kop: KopConfig?,
    onSaveGrade: (Int, Int, List<Double>, Double, Double, Double) -> Unit,
    onUpdateWeights: (Int, String, Double, Double, Double, Double) -> Unit
) {
    val context = LocalContext.current
    val activeSettings = settings ?: SystemSettings()

    // Selection States
    var selectedClassId by remember { mutableStateOf(if (classes.isNotEmpty()) classes[0].id else 0) }
    var classDropdownExpanded by remember { mutableStateOf(false) }

    // Active grading student (Null means none)
    var activeGradingStudentId by remember { mutableStateOf<Int?>(null) }

    // Number of dynamic TP columns
    var activeTpCount by remember { mutableStateOf(3) }

    // Grades Input form state
    val tpInputs = remember { mutableStateListOf<String>() }
    var utsInput by remember { mutableStateOf("") }
    var uasInput by remember { mutableStateOf("") }
    var praktekInput by remember { mutableStateOf("") }

    // Percentage Editing State
    var isEditingWeights by remember { mutableStateOf(false) }
    var weightTpInput by remember(activeSettings) { mutableStateOf((activeSettings.weightTp * 100).toInt().toString()) }
    var weightUtsInput by remember(activeSettings) { mutableStateOf((activeSettings.weightUts * 100).toInt().toString()) }
    var weightUasInput by remember(activeSettings) { mutableStateOf((activeSettings.weightUas * 100).toInt().toString()) }
    var weightPraktekInput by remember(activeSettings) { mutableStateOf((activeSettings.weightPraktek * 100).toInt().toString()) }

    // Search query for Individual Recap
    var searchQuery by remember { mutableStateOf("") }

    // Initialize inputs when a student is selected
    val currentClassStudents = remember(selectedClassId, students) {
        students.filter { it.classId == selectedClassId }
    }
    val currentClassGrades = remember(selectedClassId, grades) {
        grades.filter { it.classId == selectedClassId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Menu Daftar Nilai Siswa",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Kelola penilaian siswa & persentase raport secara akurat",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp)
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
            // Dropdown selection & Percentage Panel triggers
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
                    val activeClassName = classes.firstOrNull { it.id == selectedClassId }?.className ?: "Pilih Kelas"
                    OutlinedTextField(
                        value = activeClassName,
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
                                    activeGradingStudentId = null // Close editor on change class
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Percentage edit button
                IconButton(
                    onClick = { isEditingWeights = !isEditingWeights },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .size(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Percent,
                        contentDescription = "Edit Bobot",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Download Grades Report PDF Button
                IconButton(
                    onClick = {
                        val activeClassObj = classes.firstOrNull { it.id == selectedClassId }
                        val file = PdfGenerator.generateGradesPdf(
                            context,
                            activeClassObj,
                            kop,
                            currentClassStudents,
                            currentClassGrades
                        )
                        if (file != null) {
                            PdfGenerator.openPdf(context, file)
                        } else {
                            Toast.makeText(context, "Gagal membuat PDF Nilai", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .background(Color(0xFFEF6C00), RoundedCornerShape(12.dp))
                        .size(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "Cetak PDF Nilai",
                        tint = Color.White
                    )
                }
            }

            // Edit Weights Panel
            if (isEditingWeights) {
                Spacer(modifier = Modifier.height(10.dp))
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color(0xFF1E2A38)
                ) {
                    Text(
                        text = "Edit Persentase Nilai Raport (%)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total persentase harus bernilai 100%",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GlassmorphicTextField(
                            value = weightTpInput,
                            onValueChange = { weightTpInput = it },
                            label = "Rata TP",
                            modifier = Modifier.weight(1f)
                        )
                        GlassmorphicTextField(
                            value = weightUtsInput,
                            onValueChange = { weightUtsInput = it },
                            label = "UTS",
                            modifier = Modifier.weight(1f)
                        )
                        GlassmorphicTextField(
                            value = weightUasInput,
                            onValueChange = { weightUasInput = it },
                            label = "UAS",
                            modifier = Modifier.weight(1f)
                        )
                        GlassmorphicTextField(
                            value = weightPraktekInput,
                            onValueChange = { weightPraktekInput = it },
                            label = "Praktek",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val tp = weightTpInput.toDoubleOrNull() ?: 30.0
                            val uts = weightUtsInput.toDoubleOrNull() ?: 20.0
                            val uas = weightUasInput.toDoubleOrNull() ?: 20.0
                            val prk = weightPraktekInput.toDoubleOrNull() ?: 30.0

                            if (tp + uts + uas + prk == 100.0) {
                                onUpdateWeights(
                                    activeSettings.lateGraceMinutes,
                                    activeSettings.lateGraceTimeStr,
                                    tp / 100.0,
                                    uts / 100.0,
                                    uas / 100.0,
                                    prk / 100.0
                                )
                                isEditingWeights = false
                                Toast.makeText(context, "Bobot nilai berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Akumulasi bobot harus tepat 100% (Saat ini: ${tp + uts + uas + prk}%)", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Simpan Bobot")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Student grading editor (Visible only when a student is chosen)
            if (activeGradingStudentId != null) {
                val gradingStudent = students.firstOrNull { it.id == activeGradingStudentId }
                if (gradingStudent != null) {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color(0x33000000)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Form Nilai: ${gradingStudent.name}",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { activeGradingStudentId = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // Dynamic TP Input Columns
                        Text(
                            text = "Nilai TP (Tujuan Pembelajaran):",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Column count adjusters
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Jumlah TP: $activeTpCount", color = Color.White, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(
                                onClick = {
                                    if (activeTpCount > 1) {
                                        activeTpCount--
                                        if (tpInputs.size > activeTpCount) tpInputs.removeAt(tpInputs.size - 1)
                                    }
                                },
                                modifier = Modifier.size(24.dp).background(Color.White.copy(alpha = 0.15f), CircleShape)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Kurang", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    activeTpCount++
                                },
                                modifier = Modifier.size(24.dp).background(Color.White.copy(alpha = 0.15f), CircleShape)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }

                        // Ensure dynamic list matches count
                        while (tpInputs.size < activeTpCount) {
                            tpInputs.add("")
                        }
                        while (tpInputs.size > activeTpCount) {
                            tpInputs.removeAt(tpInputs.size - 1)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // TP Grid inputs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (i in 0 until activeTpCount) {
                                GlassmorphicTextField(
                                    value = tpInputs[i],
                                    onValueChange = { tpInputs[i] = it },
                                    label = "TP ${i + 1}",
                                    modifier = Modifier.width(70.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // UTS, UAS, Practical Inputs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GlassmorphicTextField(
                                value = utsInput,
                                onValueChange = { utsInput = it },
                                label = "Nilai UTS",
                                modifier = Modifier.weight(1f)
                            )
                            GlassmorphicTextField(
                                value = uasInput,
                                onValueChange = { uasInput = it },
                                label = "Nilai UAS",
                                modifier = Modifier.weight(1f)
                            )
                            GlassmorphicTextField(
                                value = praktekInput,
                                onValueChange = { praktekInput = it },
                                label = "Nilai Praktek",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Simpan Nilai Button
                        GlassmorphicButton(
                            onClick = {
                                val tpList = tpInputs.map { it.toDoubleOrNull() ?: 0.0 }
                                val uts = utsInput.toDoubleOrNull() ?: 0.0
                                val uas = uasInput.toDoubleOrNull() ?: 0.0
                                val praktek = praktekInput.toDoubleOrNull() ?: 0.0

                                onSaveGrade(gradingStudent.id, selectedClassId, tpList, uts, uas, praktek)
                                Toast.makeText(context, "Nilai ${gradingStudent.name} berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                activeGradingStudentId = null
                            },
                            containerColor = Color(0xFF2E7D32),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan Nilai", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Students Grades Recap Directory & Search Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Rekapitulasi Nilai Kelas",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            // Search input
            GlassmorphicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Cari Nama Siswa...",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Roster Table / Cards
            val searchedStudents = remember(searchQuery, currentClassStudents) {
                if (searchQuery.trim().isEmpty()) currentClassStudents
                else currentClassStudents.filter { it.name.lowercase().contains(searchQuery.lowercase()) }
            }

            if (searchedStudents.isEmpty()) {
                Text(
                    text = "Tidak ada siswa ditemukan.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(10.dp)
                )
            } else {
                searchedStudents.forEach { student ->
                    val sGrade = currentClassGrades.firstOrNull { it.studentId == student.id }
                    val avgTp = String.format("%.1f", sGrade?.averageTp ?: 0.0)
                    val nRaport = String.format("%.1f", sGrade?.reportCardGrade ?: 0.0)

                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        containerColor = Color(0x13FFFFFF)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text(
                                    text = student.name,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Rata TP: $avgTp | UTS: ${sGrade?.utsGrade ?: 0.0} | UAS: ${sGrade?.uasGrade ?: 0.0} | Praktek: ${sGrade?.praktekGrade ?: 0.0}",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                            }

                            // Final Report Grade & Click-to-edit
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Raport", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp)
                                    Text(
                                        text = nRaport,
                                        color = if ((sGrade?.reportCardGrade ?: 0.0) >= 75.0) Color(0xFF81C784) else Color(0xFFE57373),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        activeGradingStudentId = student.id
                                        val existing = currentClassGrades.firstOrNull { it.studentId == student.id }
                                        if (existing != null) {
                                            utsInput = existing.utsGrade.toString()
                                            uasInput = existing.uasGrade.toString()
                                            praktekInput = existing.praktekGrade.toString()
                                            
                                            tpInputs.clear()
                                            if (existing.tpGrades.isNotEmpty()) {
                                                val splitGrades = existing.tpGrades.split(",")
                                                activeTpCount = splitGrades.size
                                                tpInputs.addAll(splitGrades)
                                            } else {
                                                activeTpCount = 3
                                            }
                                        } else {
                                            utsInput = ""
                                            uasInput = ""
                                            praktekInput = ""
                                            tpInputs.clear()
                                            activeTpCount = 3
                                        }
                                    },
                                    modifier = Modifier.size(36.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Nilai", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
