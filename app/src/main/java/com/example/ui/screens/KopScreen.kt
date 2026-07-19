package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.KopConfig
import com.example.ui.components.*

@Composable
fun KopScreen(
    kop: KopConfig?,
    onSaveKop: (KopConfig) -> Unit
) {
    val context = LocalContext.current
    val activeKop = kop ?: KopConfig()

    // Form states
    var govLine1 by remember(activeKop) { mutableStateOf(activeKop.govLine1) }
    var deptLine2 by remember(activeKop) { mutableStateOf(activeKop.deptLine2) }
    var schoolName by remember(activeKop) { mutableStateOf(activeKop.schoolName) }
    var schoolAddress by remember(activeKop) { mutableStateOf(activeKop.schoolAddress) }
    var schoolContact by remember(activeKop) { mutableStateOf(activeKop.schoolContact) }
    var postalCode by remember(activeKop) { mutableStateOf(activeKop.postalCode) }
    var schoolEmail by remember(activeKop) { mutableStateOf(activeKop.schoolEmail) }
    var nss by remember(activeKop) { mutableStateOf(activeKop.nss) }
    var nsb by remember(activeKop) { mutableStateOf(activeKop.nsb) }
    var npsn by remember(activeKop) { mutableStateOf(activeKop.npsn) }

    var principalName by remember(activeKop) { mutableStateOf(activeKop.principalName) }
    var principalNip by remember(activeKop) { mutableStateOf(activeKop.principalNip) }
    var teacherName by remember(activeKop) { mutableStateOf(activeKop.teacherName) }
    var teacherNip by remember(activeKop) { mutableStateOf(activeKop.teacherNip) }
    var location by remember(activeKop) { mutableStateOf(activeKop.location) }

    var tutWuriUri by remember(activeKop) { mutableStateOf(activeKop.tutWuriLogoUri) }
    var schoolLogoUri by remember(activeKop) { mutableStateOf(activeKop.schoolLogoUri) }

    // Launchers for logos
    val tutWuriLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            tutWuriUri = it.toString()
        }
    }

    val schoolLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            schoolLogoUri = it.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Pengaturan KOP Surat & TTD",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Atur KOP resmi laporan cetak presensi, jurnal, dan daftar nilai",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 1. Logo Upload Row
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0x26FFFFFF)
        ) {
            Text(
                text = "Logo KOP Surat",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tut Wuri Handayani di kiri, Logo Sekolah di kanan",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Logo (Tut Wuri)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(70.dp), contentAlignment = Alignment.Center) {
                        if (!tutWuriUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = tutWuriUri,
                                contentDescription = "Logo Tut Wuri",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("L", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { tutWuriLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = GlassAccentCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Logo Kiri", fontSize = 10.sp)
                    }
                }

                // Right Logo (School)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(70.dp), contentAlignment = Alignment.Center) {
                        if (!schoolLogoUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = schoolLogoUri,
                                contentDescription = "Logo Sekolah",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("R", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { schoolLogoLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = GlassAccentCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Logo Kanan", fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. KOP Text Fields Form
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0x26FFFFFF)
        ) {
            Text(
                text = "Identitas Lembaga",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GlassmorphicTextField(
                value = govLine1,
                onValueChange = { govLine1 = it },
                label = "Baris 1: Pemerintah Kabupaten/Kota",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            GlassmorphicTextField(
                value = deptLine2,
                onValueChange = { deptLine2 = it },
                label = "Baris 2: Dinas Pendidikan",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            GlassmorphicTextField(
                value = schoolName,
                onValueChange = { schoolName = it },
                label = "Baris 3: Nama Sekolah Resmi",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            GlassmorphicTextField(
                value = schoolAddress,
                onValueChange = { schoolAddress = it },
                label = "Alamat Sekolah",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassmorphicTextField(
                    value = schoolContact,
                    onValueChange = { schoolContact = it },
                    label = "Kontak/Telp",
                    modifier = Modifier.weight(1.2f)
                )
                GlassmorphicTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    label = "Kode POS",
                    modifier = Modifier.weight(0.8f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            GlassmorphicTextField(
                value = schoolEmail,
                onValueChange = { schoolEmail = it },
                label = "Email Sekolah",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassmorphicTextField(
                    value = nss,
                    onValueChange = { nss = it },
                    label = "NSS Sekolah",
                    modifier = Modifier.weight(1f)
                )
                GlassmorphicTextField(
                    value = nsb,
                    onValueChange = { nsb = it },
                    label = "NSB Sekolah",
                    modifier = Modifier.weight(1f)
                )
                GlassmorphicTextField(
                    value = npsn,
                    onValueChange = { npsn = it },
                    label = "NPSN Sekolah",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Signature Configuration Form
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0x26FFFFFF)
        ) {
            Text(
                text = "Lokasi & Tanda Tangan Cetak Laporan",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GlassmorphicTextField(
                value = location,
                onValueChange = { location = it },
                label = "Lokasi TTD (e.g. Poso / Palu)",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Kepala Sekolah (Kiri Bawah)
            Text("Konfigurasi Kepala Sekolah (Kiri Bawah):", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            GlassmorphicTextField(
                value = principalName,
                onValueChange = { principalName = it },
                label = "Nama Kepala Sekolah (Lengkap Gelar)",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            GlassmorphicTextField(
                value = principalNip,
                onValueChange = { principalNip = it },
                label = "NIP Kepala Sekolah",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Guru Kelas (Kanan Bawah)
            Text("Konfigurasi Guru Kelas/Mapel (Kanan Bawah):", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            GlassmorphicTextField(
                value = teacherName,
                onValueChange = { teacherName = it },
                label = "Nama Guru Kelas / Mapel",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            GlassmorphicTextField(
                value = teacherNip,
                onValueChange = { teacherNip = it },
                label = "NIP Guru",
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Global KOP Save Button
        GlassmorphicButton(
            onClick = {
                val newConfig = KopConfig(
                    govLine1 = govLine1.trim(),
                    deptLine2 = deptLine2.trim(),
                    schoolName = schoolName.trim(),
                    schoolAddress = schoolAddress.trim(),
                    schoolContact = schoolContact.trim(),
                    postalCode = postalCode.trim(),
                    schoolEmail = schoolEmail.trim(),
                    nss = nss.trim(),
                    nsb = nsb.trim(),
                    npsn = npsn.trim(),
                    principalName = principalName.trim(),
                    principalNip = principalNip.trim(),
                    teacherName = teacherName.trim(),
                    teacherNip = teacherNip.trim(),
                    location = location.trim(),
                    tutWuriLogoUri = tutWuriUri,
                    schoolLogoUri = schoolLogoUri
                )
                onSaveKop(newConfig)
                Toast.makeText(context, "Pengaturan KOP Surat berhasil disimpan!", Toast.LENGTH_SHORT).show()
            },
            containerColor = Color(0xFF2E7D32),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan KOP & TTD", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
