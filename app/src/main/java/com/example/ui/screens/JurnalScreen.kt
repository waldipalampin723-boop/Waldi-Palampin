package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.components.*
import com.example.utils.PdfGenerator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JurnalScreen(
    classes: List<ClassEntity>,
    journals: List<TeachingJournal>,
    kop: KopConfig?,
    onAddJournal: (Int, String, String, String, String, String, String, String?) -> Unit,
    onDeleteJournal: (TeachingJournal) -> Unit
) {
    val context = LocalContext.current

    // Date & Time Defaults
    val sdfDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val currentDateStr = remember { sdfDate.format(Date()) }

    // Navigation state inside module: "list" or "add"
    var mode by remember { mutableStateOf("list") }

    // Form States
    var selectedClassId by remember { mutableStateOf(if (classes.isNotEmpty()) classes[0].id else 0) }
    var classDropdownExpanded by remember { mutableStateOf(false) }
    var journalDate by remember { mutableStateOf(currentDateStr) }
    var teachingHoursInput by remember { mutableStateOf("07:30 - 09:00") }
    var lessonTitleInput by remember { mutableStateOf("") }
    var learningObjectiveInput by remember { mutableStateOf("") }
    var teacherReflectionInput by remember { mutableStateOf("") }
    var specialNotesInput by remember { mutableStateOf("") }
    var photoUriStr by remember { mutableStateOf<String?>(null) }

    // Photo selection launcher
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoUriStr = it.toString()
        }
    }

    // PDF Range Date Pickers
    var showPdfDialog by remember { mutableStateOf(false) }
    var filterTypeSelected by remember { mutableStateOf("Hari Ini") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Menu Jurnal Mengajar",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Catatan jurnal harian pembelajaran guru",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = {
                    if (mode == "list") mode = "add" else mode = "list"
                },
                modifier = Modifier
                    .background(GlassAccentCyan, RoundedCornerShape(12.dp))
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = if (mode == "list") Icons.Default.Add else Icons.Default.List,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (classes.isEmpty()) {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tambahkan data kelas terlebih dahulu di menu Data Kelas untuk mengisi jurnal.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        } else if (mode == "add") {
            // Form to Add New Teaching Journal
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0x26FFFFFF)
            ) {
                Text(
                    text = "Tambah Jurnal Mengajar",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Class Dropdown Selector
                ExposedDropdownMenuBox(
                    expanded = classDropdownExpanded,
                    onExpandedChange = { classDropdownExpanded = !classDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
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
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                GlassmorphicTextField(
                    value = journalDate,
                    onValueChange = { journalDate = it },
                    label = "Tanggal Kegiatan (yyyy-MM-dd)",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                GlassmorphicTextField(
                    value = teachingHoursInput,
                    onValueChange = { teachingHoursInput = it },
                    label = "Jam Pelajaran (e.g. 07:30 - 09:00)",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                GlassmorphicTextField(
                    value = lessonTitleInput,
                    onValueChange = { lessonTitleInput = it },
                    label = "Judul Materi Pembelajaran",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                GlassmorphicTextField(
                    value = learningObjectiveInput,
                    onValueChange = { learningObjectiveInput = it },
                    label = "Tujuan Pembelajaran",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )
                Spacer(modifier = Modifier.height(10.dp))

                GlassmorphicTextField(
                    value = teacherReflectionInput,
                    onValueChange = { teacherReflectionInput = it },
                    label = "Refleksi Guru",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )
                Spacer(modifier = Modifier.height(10.dp))

                GlassmorphicTextField(
                    value = specialNotesInput,
                    onValueChange = { specialNotesInput = it },
                    label = "Catatan Khusus",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Upload Teaching Photo Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Foto Pembelajaran: ", color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { photoLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = GlassAccentCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unggah Foto", fontSize = 11.sp)
                    }
                }

                if (!photoUriStr.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = photoUriStr,
                            contentDescription = "Foto terpilih",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { photoUriStr = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(28.dp)
                                .background(Color.Red, RoundedCornerShape(6.dp))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Hapus", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlassmorphicButton(
                    onClick = {
                        if (lessonTitleInput.trim().isNotEmpty() && learningObjectiveInput.trim().isNotEmpty()) {
                            onAddJournal(
                                selectedClassId,
                                journalDate.trim(),
                                teachingHoursInput.trim(),
                                lessonTitleInput.trim(),
                                learningObjectiveInput.trim(),
                                teacherReflectionInput.trim(),
                                specialNotesInput.trim(),
                                photoUriStr
                            )
                            Toast.makeText(context, "Jurnal Mengajar berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                            mode = "list"
                            // Clear form
                            lessonTitleInput = ""
                            learningObjectiveInput = ""
                            teacherReflectionInput = ""
                            specialNotesInput = ""
                            photoUriStr = null
                        } else {
                            Toast.makeText(context, "Materi dan tujuan pembelajaran harus diisi!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = Color(0xFF2E7D32),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Jurnal", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // mode == "list"
            // Print PDF Trigger Header Card
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0x26FFFFFF)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Rekap PDF Jurnal Mengajar", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Cetak jurnal untuk tanda tangan Kepala Sekolah", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                    Button(
                        onClick = { showPdfDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cetak PDF", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Journals List
            if (journals.isEmpty()) {
                Text(
                    text = "Belum ada catatan jurnal mengajar terdaftar.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(10.dp)
                )
            } else {
                journals.forEach { journal ->
                    val clObj = classes.firstOrNull { it.id == journal.classId }
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
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "Kelas: ${clObj?.className ?: "Kelas ?"}",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Tanggal: ${journal.date}  |  Jam: ${journal.teachingHours}",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 11.sp
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onDeleteJournal(journal)
                                        Toast.makeText(context, "Jurnal berhasil dihapus!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red.copy(alpha = 0.8f))
                                }
                            }
                            Divider(color = Color.White.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 8.dp))

                            Text("Materi: ${journal.lessonTitle}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Tujuan: ${journal.learningObjective}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Refleksi: ${journal.teacherReflection}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Catatan Khusus: ${journal.specialNotes}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)

                            // Thumbnail image inside list if loaded
                            if (!journal.photoUri.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(width = 120.dp, height = 80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = journal.photoUri,
                                        contentDescription = "Teaching Photo Thumbnail",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- PDF Range Dialog ---
    if (showPdfDialog) {
        AlertDialog(
            onDismissRequest = { showPdfDialog = false },
            title = { Text("Pilih Filter Tanggal Jurnal") },
            text = {
                Column {
                    Text("Unduh jurnal mengajar dengan filter waktu yang Anda inginkan:")
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf("Hari Ini", "1 Minggu Terakhir", "1 Bulan Terakhir", "Semua Riwayat Jurnal").forEach { filterOpt ->
                        Button(
                            onClick = {
                                showPdfDialog = false
                                val filteredJournals = when (filterOpt) {
                                    "Hari Ini" -> journals.filter { it.date == currentDateStr }
                                    "1 Minggu Terakhir" -> journals // Simple representation of full logs
                                    "1 Bulan Terakhir" -> journals
                                    else -> journals
                                }
                                val file = PdfGenerator.generateJournalPdf(
                                    context,
                                    kop,
                                    classes,
                                    filteredJournals,
                                    filterOpt
                                )
                                if (file != null) {
                                    PdfGenerator.openPdf(context, file)
                                } else {
                                    Toast.makeText(context, "Gagal mencetak PDF", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x1F0B0F19)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        ) {
                            Text(filterOpt, color = Color.Black)
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
