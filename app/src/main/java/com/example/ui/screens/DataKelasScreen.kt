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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ClassEntity
import com.example.ui.components.*

@Composable
fun DataKelasScreen(
    classes: List<ClassEntity>,
    onAddClass: (String) -> Unit,
    onUpdateClass: (Int, String) -> Unit,
    onDeleteClass: (Int, String) -> Unit
) {
    val context = LocalContext.current
    var newClassName by remember { mutableStateOf("") }

    // Dialog state for editing
    var classToEdit by remember { mutableStateOf<ClassEntity?>(null) }
    var editClassNameInput by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }

    // Dialog state for deleting
    var classToDelete by remember { mutableStateOf<ClassEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Menu Data Kelas",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Kelola nama kelas yang diajar",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Add New Class Form Card
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0x26FFFFFF)
        ) {
            Text(
                text = "Tambah Kelas Baru",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassmorphicTextField(
                    value = newClassName,
                    onValueChange = { newClassName = it },
                    label = "Nama Kelas (e.g. Kelas IV-A)",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = {
                        if (newClassName.trim().isNotEmpty()) {
                            onAddClass(newClassName.trim())
                            Toast.makeText(context, "Kelas $newClassName ditambahkan!", Toast.LENGTH_SHORT).show()
                            newClassName = ""
                        } else {
                            Toast.makeText(context, "Nama kelas tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .background(GlassAccentCyan, RoundedCornerShape(12.dp))
                        .size(52.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Class Lists Header
        Text(
            text = "Daftar Kelas Tersimpan",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (classes.isEmpty()) {
            Text(
                text = "Belum ada kelas terdaftar. Silakan tambahkan kelas di atas.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                modifier = Modifier.padding(10.dp)
            )
        } else {
            classes.forEach { classEntity ->
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
                                imageVector = Icons.Default.Class,
                                contentDescription = null,
                                tint = GlassAccentCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = classEntity.className,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Edit / Delete Action Buttons
                        Row {
                            IconButton(onClick = {
                                classToEdit = classEntity
                                editClassNameInput = classEntity.className
                                showEditDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                            }
                            IconButton(onClick = {
                                classToDelete = classEntity
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

    // --- Edit Dialog ---
    if (showEditDialog && classToEdit != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Nama Kelas") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editClassNameInput,
                        onValueChange = { editClassNameInput = it },
                        label = { Text("Nama Kelas") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editClassNameInput.trim().isNotEmpty()) {
                            onUpdateClass(classToEdit!!.id, editClassNameInput.trim())
                            Toast.makeText(context, "Kelas diperbarui!", Toast.LENGTH_SHORT).show()
                            showEditDialog = false
                            classToEdit = null
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

    // --- Delete Confirmation Dialog ---
    if (showDeleteDialog && classToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Kelas") },
            text = {
                Text("Apakah Anda yakin ingin menghapus kelas '${classToDelete!!.className}'? Semua data presensi, nilai, dan data siswa yang berhubungan dengan kelas ini akan terpengaruh.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClass(classToDelete!!.id, classToDelete!!.className)
                        Toast.makeText(context, "Kelas berhasil dihapus!", Toast.LENGTH_SHORT).show()
                        showDeleteDialog = false
                        classToDelete = null
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
