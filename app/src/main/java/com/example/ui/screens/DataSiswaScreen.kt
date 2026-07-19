package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataSiswaScreen(
    classes: List<ClassEntity>,
    students: List<StudentEntity>,
    onAddStudent: (String, String, Int) -> Unit,
    onUpdateStudent: (Int, String, String, Int) -> Unit,
    onDeleteStudent: (Int, String, String, Int) -> Unit
) {
    val context = LocalContext.current

    // Selection States
    var selectedClassIdFilter by remember { mutableStateOf(if (classes.isNotEmpty()) classes[0].id else 0) }
    var filterDropdownExpanded by remember { mutableStateOf(false) }

    // Add Student States
    var newStudentName by remember { mutableStateOf("") }
    var newStudentGender by remember { mutableStateOf("Laki-laki") }
    var addClassDropdownExpanded by remember { mutableStateOf(false) }
    var targetClassIdForNewStudent by remember { mutableStateOf(if (classes.isNotEmpty()) classes[0].id else 0) }

    // Edit Student Dialog States
    var studentToEdit by remember { mutableStateOf<StudentEntity?>(null) }
    var editStudentNameInput by remember { mutableStateOf("") }
    var editStudentGenderInput by remember { mutableStateOf("Laki-laki") }
    var editStudentClassIdInput by remember { mutableStateOf(0) }
    var editClassDropdownExpanded by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Delete Student Dialog States
    var studentToDelete by remember { mutableStateOf<StudentEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val filteredStudents = remember(selectedClassIdFilter, students) {
        students.filter { it.classId == selectedClassIdFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Menu Data Siswa",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Kelola biodata siswa berdasarkan kelas",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (classes.isEmpty()) {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Silakan tambahkan data kelas terlebih dahulu di menu Data Kelas sebelum mengelola data siswa.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        } else {
            // Add Student Card
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0x26FFFFFF)
            ) {
                Text(
                    text = "Tambah Siswa Baru",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                GlassmorphicTextField(
                    value = newStudentName,
                    onValueChange = { newStudentName = it },
                    label = "Nama Lengkap Siswa",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Gender Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gender: ", color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Row(
                        modifier = Modifier.clickable { newStudentGender = "Laki-laki" },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = newStudentGender == "Laki-laki",
                            onClick = { newStudentGender = "Laki-laki" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color.White)
                        )
                        Text("Laki-laki (L)", color = Color.White, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        modifier = Modifier.clickable { newStudentGender = "Perempuan" },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = newStudentGender == "Perempuan",
                            onClick = { newStudentGender = "Perempuan" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color.White)
                        )
                        Text("Perempuan (P)", color = Color.White, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Class Dropdown for new student
                ExposedDropdownMenuBox(
                    expanded = addClassDropdownExpanded,
                    onExpandedChange = { addClassDropdownExpanded = !addClassDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val targetClassName = classes.firstOrNull { it.id == targetClassIdForNewStudent }?.className ?: "Pilih Kelas"
                    OutlinedTextField(
                        value = targetClassName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Kelas Siswa", color = Color.White) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = addClassDropdownExpanded) },
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
                        expanded = addClassDropdownExpanded,
                        onDismissRequest = { addClassDropdownExpanded = false }
                    ) {
                        classes.forEach { classEntity ->
                            DropdownMenuItem(
                                text = { Text(classEntity.className) },
                                onClick = {
                                    targetClassIdForNewStudent = classEntity.id
                                    addClassDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlassmorphicButton(
                    onClick = {
                        if (newStudentName.trim().isNotEmpty()) {
                            onAddStudent(newStudentName.trim(), newStudentGender, targetClassIdForNewStudent)
                            Toast.makeText(context, "Siswa $newStudentName berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                            newStudentName = ""
                        } else {
                            Toast.makeText(context, "Nama siswa tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = GlassAccentBlue,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tambah Siswa", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Filter Class Dropdown Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Daftar Siswa Kelas",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                ExposedDropdownMenuBox(
                    expanded = filterDropdownExpanded,
                    onExpandedChange = { filterDropdownExpanded = !filterDropdownExpanded },
                    modifier = Modifier.width(160.dp)
                ) {
                    val activeClassName = classes.firstOrNull { it.id == selectedClassIdFilter }?.className ?: "Pilih Kelas"
                    OutlinedTextField(
                        value = activeClassName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterDropdownExpanded) },
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
                        expanded = filterDropdownExpanded,
                        onDismissRequest = { filterDropdownExpanded = false }
                    ) {
                        classes.forEach { classEntity ->
                            DropdownMenuItem(
                                text = { Text(classEntity.className) },
                                onClick = {
                                    selectedClassIdFilter = classEntity.id
                                    filterDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Student Cards
            if (filteredStudents.isEmpty()) {
                Text(
                    text = "Belum ada siswa di kelas ini.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(10.dp)
                )
            } else {
                filteredStudents.forEach { student ->
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        containerColor = Color(0x1AFFFFFF)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (student.gender == "Laki-laki") Icons.Default.Male else Icons.Default.Female,
                                    contentDescription = null,
                                    tint = if (student.gender == "Laki-laki") Color(0xFF00ACC1) else Color(0xFFE91E63),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
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
                            }

                            // Actions
                            Row {
                                IconButton(onClick = {
                                    studentToEdit = student
                                    editStudentNameInput = student.name
                                    editStudentGenderInput = student.gender
                                    editStudentClassIdInput = student.classId
                                    showEditDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                                }
                                IconButton(onClick = {
                                    studentToDelete = student
                                    showDeleteDialog = true
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Edit Dialog ---
    if (showEditDialog && studentToEdit != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Nama & Data Siswa") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editStudentNameInput,
                        onValueChange = { editStudentNameInput = it },
                        label = { Text("Nama Siswa") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Gender selection
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Gender: ", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(modifier = Modifier.clickable { editStudentGenderInput = "Laki-laki" }, verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = editStudentGenderInput == "Laki-laki", onClick = { editStudentGenderInput = "Laki-laki" })
                            Text("L", fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(modifier = Modifier.clickable { editStudentGenderInput = "Perempuan" }, verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = editStudentGenderInput == "Perempuan", onClick = { editStudentGenderInput = "Perempuan" })
                            Text("P", fontSize = 13.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Class Dropdown
                    ExposedDropdownMenuBox(
                        expanded = editClassDropdownExpanded,
                        onExpandedChange = { editClassDropdownExpanded = !editClassDropdownExpanded }
                    ) {
                        val activeClassName = classes.firstOrNull { it.id == editStudentClassIdInput }?.className ?: "Pilih Kelas"
                        OutlinedTextField(
                            value = activeClassName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kelas") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = editClassDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = editClassDropdownExpanded,
                            onDismissRequest = { editClassDropdownExpanded = false }
                        ) {
                            classes.forEach { classEntity ->
                                DropdownMenuItem(
                                    text = { Text(classEntity.className) },
                                    onClick = {
                                        editStudentClassIdInput = classEntity.id
                                        editClassDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editStudentNameInput.trim().isNotEmpty()) {
                            onUpdateStudent(
                                studentToEdit!!.id,
                                editStudentNameInput.trim(),
                                editStudentGenderInput,
                                editStudentClassIdInput
                            )
                            Toast.makeText(context, "Data siswa berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                            showEditDialog = false
                            studentToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GlassAccentBlue)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // --- Delete Confirmation ---
    if (showDeleteDialog && studentToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Siswa") },
            text = {
                Text("Apakah Anda yakin ingin menghapus siswa '${studentToDelete!!.name}'? Semua riwayat nilai dan kehadiran siswa ini akan dihapus permanen.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteStudent(
                            studentToDelete!!.id,
                            studentToDelete!!.name,
                            studentToDelete!!.gender,
                            studentToDelete!!.classId
                        )
                        Toast.makeText(context, "Siswa berhasil dihapus!", Toast.LENGTH_SHORT).show()
                        showDeleteDialog = false
                        studentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
