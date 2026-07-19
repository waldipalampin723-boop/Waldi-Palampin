package com.example.ui.screens

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.TeacherProfile
import com.example.ui.components.*

@Composable
fun LandingScreen(
    profile: TeacherProfile?,
    onSaveProfile: (String, String, String, String, String?, String?, String) -> Unit,
    onEnterApp: () -> Unit
) {
    val context = LocalContext.current
    val activeProfile = profile ?: TeacherProfile()

    // Screen State
    var isEditMode by remember { mutableStateOf(false) }

    // Form inputs
    var nameInput by remember(activeProfile) { mutableStateOf(activeProfile.name) }
    var schoolInput by remember(activeProfile) { mutableStateOf(activeProfile.schoolName) }
    var subjectInput by remember(activeProfile) { mutableStateOf(activeProfile.subject) }
    var educationInput by remember(activeProfile) { mutableStateOf(activeProfile.educationHistory) }
    
    var coverUriStr by remember(activeProfile) { mutableStateOf(activeProfile.coverUri) }
    var profileUriStr by remember(activeProfile) { mutableStateOf(activeProfile.profileUri) }
    
    // Sliders
    val sliderUris = remember(activeProfile) {
        mutableStateListOf<String>().apply {
            if (activeProfile.sliderImageUris.isNotEmpty()) {
                addAll(activeProfile.sliderImageUris.split(","))
            }
        }
    }

    // Launchers
    val coverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            coverUriStr = it.toString()
        }
    }

    val profileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileUriStr = it.toString()
        }
    }

    val sliderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            sliderUris.add(it.toString())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp)
        ) {
            // 1. Cover Photo with Blur Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                if (!coverUriStr.isNullOrEmpty()) {
                    AsyncImage(
                        model = coverUriStr,
                        contentDescription = "Cover Guru",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(GlassAccentIndigo, GlassAccentPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Landscape,
                            contentDescription = "Cover Default",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                // Blur Gradient bottom cover overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xFF0F172A).copy(alpha = 0.8f))
                            )
                        )
                )

                if (isEditMode) {
                    IconButton(
                        onClick = { coverLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Ganti Sampul",
                            tint = Color.White
                        )
                    }
                }
            }

            // 2. Profile Photo and Quick Stats Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-50).dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box {
                        if (!profileUriStr.isNullOrEmpty()) {
                            AsyncImage(
                                model = profileUriStr,
                                contentDescription = "Foto Profil",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, Color.White, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(GlassAccentCyan)
                                    .border(3.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profil Default",
                                    tint = Color.White,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }

                        if (isEditMode) {
                            IconButton(
                                onClick = { profileLauncher.launch("image/*") },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(32.dp)
                                    .background(GlassAccentCyan, CircleShape)
                                    .border(1.dp, Color.White, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profil",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isEditMode) {
                        Text(
                            text = activeProfile.name,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = activeProfile.subject,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Sekolah",
                                tint = GlassAccentCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = activeProfile.schoolName,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // 3. Main Details and Image Slider
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-30).dp)
            ) {
                if (isEditMode) {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color(0x3D1A237E)
                    ) {
                        Text(
                            text = "Edit Profil Guru",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        GlassmorphicTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = "Nama Lengkap Guru (Gelar)",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        GlassmorphicTextField(
                            value = subjectInput,
                            onValueChange = { subjectInput = it },
                            label = "Mata Pelajaran / Kelas yang diampu",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        GlassmorphicTextField(
                            value = schoolInput,
                            onValueChange = { schoolInput = it },
                            label = "Nama Sekolah",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        GlassmorphicTextField(
                            value = educationInput,
                            onValueChange = { educationInput = it },
                            label = "Riwayat Pendidikan",
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { isEditMode = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.6f))
                            ) {
                                Text("Batal")
                            }
                            Button(
                                onClick = {
                                    onSaveProfile(
                                        nameInput,
                                        schoolInput,
                                        subjectInput,
                                        educationInput,
                                        coverUriStr,
                                        profileUriStr,
                                        sliderUris.joinToString(",")
                                    )
                                    isEditMode = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Icon(Icons.Default.Save, contentDescription = "Simpan")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simpan Perubahan")
                            }
                        }
                    }
                } else {
                    // Profile Info View
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color(0x26FFFFFF)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Biodata & Riwayat Pendidikan",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { isEditMode = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GlassAccentCyan)
                            }
                        }
                        Divider(color = Color.White.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 10.dp))

                        Text(
                            text = "Riwayat Pendidikan:",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activeProfile.educationHistory,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Image Slider Card
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color(0x1AFFFFFF)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dokumentasi & Galeri Guru",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { sliderLauncher.launch("image/*") }) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Tambah Foto", tint = GlassAccentCyan)
                            }
                        }
                        Divider(color = Color.White.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 10.dp))

                        if (sliderUris.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = "No images", tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Belum ada foto galeri.", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                }
                            }
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                itemsIndexed(sliderUris) { index, itemUri ->
                                    Box(
                                        modifier = Modifier
                                            .width(220.dp)
                                            .height(140.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        AsyncImage(
                                            model = itemUri,
                                            contentDescription = "Slider $index",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )

                                        // Delete slider photo button
                                        IconButton(
                                            onClick = {
                                                sliderUris.removeAt(index)
                                                onSaveProfile(
                                                    activeProfile.name,
                                                    activeProfile.schoolName,
                                                    activeProfile.subject,
                                                    activeProfile.educationHistory,
                                                    coverUriStr,
                                                    profileUriStr,
                                                    sliderUris.joinToString(",")
                                                )
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .size(24.dp)
                                                .background(Color.Red.copy(alpha = 0.7f), CircleShape)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Hapus", tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Sticky Absolute Bottom Glass Bar for Navigation Buttons
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.35f))
                .border(1.dp, Color.White.copy(alpha = 0.1f))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Button: Exit
                Button(
                    onClick = {
                        (context as? Activity)?.finish()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Keluar")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Keluar Aplikasi", fontSize = 13.sp)
                }

                // Right Button: Enter App
                GlassmorphicButton(
                    onClick = onEnterApp,
                    containerColor = GlassAccentBlue
                ) {
                    Text("Masuk ke GuruPro", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = "Masuk", tint = Color.White)
                }
            }
        }
    }
}
