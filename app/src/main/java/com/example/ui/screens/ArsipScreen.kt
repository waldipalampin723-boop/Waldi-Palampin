package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdminArchive
import com.example.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArsipScreen(
    archives: List<AdminArchive>,
    onAddArchive: (String, String, String?, String) -> Unit,
    onUpdateArchive: (Int, String, String, String?, String) -> Unit,
    onDeleteArchive: (AdminArchive) -> Unit
) {
    val context = LocalContext.current
    val sdfDate = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    // Upload form states
    var selectedUriStr by remember { mutableStateOf<String?>(null) }
    var inputFileName by remember { mutableStateOf("") }
    var selectedFileType by remember { mutableStateOf("PDF") }

    // Edit states
    var archiveToEdit by remember { mutableStateOf<AdminArchive?>(null) }
    var editFileNameInput by remember { mutableStateOf("") }
    var editFileTypeInput by remember { mutableStateOf("PDF") }
    var showEditDialog by remember { mutableStateOf(false) }

    // File selection launcher
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            selectedUriStr = selectedUri.toString()
            
            // Auto retrieve filename from content resolver
            var name = "Dokumen Baru"
            try {
                context.contentResolver.query(selectedUri, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIdx != -1) {
                        name = cursor.getString(nameIdx)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            inputFileName = name
            
            // Try to auto-detect file type
            selectedFileType = when {
                name.lowercase().endsWith(".pdf") -> "PDF"
                name.lowercase().endsWith(".doc") || name.lowercase().endsWith(".docx") -> "Word"
                name.lowercase().endsWith(".xls") || name.lowercase().endsWith(".xlsx") -> "Excel"
                else -> "PDF"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Arsip Administrasi Guru",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Penyimpanan berkas administrasi sekolah terpadu",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Upload Form Card
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0x26FFFFFF)
        ) {
            Text(
                text = "Pilih & Unggah Dokumen Baru",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { fileLauncher.launch("*/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = GlassAccentCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pilih Berkas", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = if (selectedUriStr != null) "Berkas siap diunggah" else "Belum ada berkas dipilih",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            if (selectedUriStr != null) {
                Spacer(modifier = Modifier.height(12.dp))
                GlassmorphicTextField(
                    value = inputFileName,
                    onValueChange = { inputFileName = it },
                    label = "Nama Berkas Dokumen",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                // File Type Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Jenis File: ", color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    listOf("PDF", "Word", "Excel").forEach { type ->
                        Row(
                            modifier = Modifier.clickable { selectedFileType = type },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedFileType == type,
                                onClick = { selectedFileType = type },
                                colors = RadioButtonDefaults.colors(selectedColor = Color.White)
                            )
                            Text(type, color = Color.White, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                GlassmorphicButton(
                    onClick = {
                        if (inputFileName.trim().isNotEmpty()) {
                            val uploadDate = sdfDate.format(Date())
                            onAddArchive(inputFileName.trim(), selectedFileType, selectedUriStr, uploadDate)
                            Toast.makeText(context, "Dokumen berhasil diarsipkan!", Toast.LENGTH_SHORT).show()
                            selectedUriStr = null
                            inputFileName = ""
                        } else {
                            Toast.makeText(context, "Nama file tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = Color(0xFF2E7D32),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Upload")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simpan & Arsipkan", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Archives List Header
        Text(
            text = "Daftar Berkas Terarsip",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (archives.isEmpty()) {
            Text(
                text = "Belum ada arsip dokumen yang disimpan.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                modifier = Modifier.padding(10.dp)
            )
        } else {
            archives.forEach { archive ->
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
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Document icon based on type
                            Icon(
                                imageVector = when (archive.fileType) {
                                    "PDF" -> Icons.Default.PictureAsPdf
                                    "Excel" -> Icons.Default.TableChart
                                    else -> Icons.Default.Description
                                },
                                contentDescription = null,
                                tint = when (archive.fileType) {
                                    "PDF" -> Color(0xFFEF5350)
                                    "Excel" -> Color(0xFF66BB6A)
                                    else -> Color(0xFF42A5F5)
                                },
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = archive.fileName,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Format: ${archive.fileType} | Diunggah: ${archive.uploadedDate}",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Actions: Open/Download, Edit, Delete
                        Row {
                            IconButton(onClick = {
                                if (!archive.fileUri.isNullOrEmpty()) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(archive.fileUri)).apply {
                                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Attempt chooser if direct fails
                                        try {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "*/*"
                                                putExtra(Intent.EXTRA_STREAM, Uri.parse(archive.fileUri))
                                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Buka/Kirim Dokumen"))
                                        } catch (err: Exception) {
                                            Toast.makeText(context, "Tidak ada aplikasi untuk membuka file ini.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Sistem file tidak valid.", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(Icons.Default.Download, contentDescription = "Download/Buka", tint = Color.White)
                            }

                            IconButton(onClick = {
                                archiveToEdit = archive
                                editFileNameInput = archive.fileName
                                editFileTypeInput = archive.fileType
                                showEditDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                            }

                            IconButton(onClick = {
                                onDeleteArchive(archive)
                                Toast.makeText(context, "Arsip dihapus!", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Edit Archive Dialog ---
    if (showEditDialog && archiveToEdit != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Detail Arsip") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editFileNameInput,
                        onValueChange = { editFileNameInput = it },
                        label = { Text("Nama Dokumen") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // File Type Selector Dialog
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Format: ", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        listOf("PDF", "Word", "Excel").forEach { type ->
                            Row(modifier = Modifier.clickable { editFileTypeInput = type }, verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = editFileTypeInput == type, onClick = { editFileTypeInput = type })
                                Text(type, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editFileNameInput.trim().isNotEmpty()) {
                            onUpdateArchive(
                                archiveToEdit!!.id,
                                editFileNameInput.trim(),
                                editFileTypeInput,
                                archiveToEdit!!.fileUri,
                                archiveToEdit!!.uploadedDate
                            )
                            Toast.makeText(context, "Detail arsip diperbarui!", Toast.LENGTH_SHORT).show()
                            showEditDialog = false
                            archiveToEdit = null
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
}
