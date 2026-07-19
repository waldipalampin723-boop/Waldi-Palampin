package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.components.atmosphericBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()

                // State collection
                val profile by viewModel.profile.collectAsState()
                val classes by viewModel.allClasses.collectAsState()
                val students by viewModel.allStudents.collectAsState()
                val attendance by viewModel.allAttendance.collectAsState()
                val journals by viewModel.allJournals.collectAsState()
                val archives by viewModel.allArchives.collectAsState()
                val grades by viewModel.allGrades.collectAsState()
                val settings by viewModel.settings.collectAsState()
                val kopConfig by viewModel.kopConfig.collectAsState()

                // Top level Navigation: "landing" or "app_home"
                var currentScreen by remember { mutableStateOf("landing") }

                // App home active menu tab: "dashboard", "presensi", "data_kelas", "data_siswa", "jurnal", "arsip", "daftar_nilai", "kop_config"
                var activeTab by remember { mutableStateOf("dashboard") }

                Scaffold(
                    modifier = Modifier.fillMaxSize().atmosphericBackground(),
                    containerColor = Color.Transparent,
                    topBar = {
                        if (currentScreen == "app_home") {
                            // Glassmorphic Responsive Top Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F172A).copy(alpha = 0.4f))
                                    .statusBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .border(
                                        width = 1.dp,
                                        color = Color.White.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(0.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // School Logo & App Name left side
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val logoUri = kopConfig?.schoolLogoUri
                                        if (!logoUri.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = logoUri,
                                                contentDescription = "Logo Sekolah",
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .border(1.dp, Color.White, CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.School,
                                                    contentDescription = "Default School Logo",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "GuruPro",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                        )
                                    }

                                    // Right button: return to profile landing screen
                                    Button(
                                        onClick = { currentScreen = "landing" },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Kembali ke Profil",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Profil Guru", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (currentScreen == "landing") {
                            LandingScreen(
                                profile = profile,
                                onSaveProfile = { name, school, mapel, edu, cov, prof, sld ->
                                    viewModel.updateProfile(name, school, mapel, edu, cov, prof, sld)
                                },
                                onEnterApp = {
                                    currentScreen = "app_home"
                                }
                            )
                        } else {
                            // Main App Space: Tab Selection Layout + Active Content
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Scrollable Navigation Row of Pill Tabs (With Icons!)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0F172A).copy(alpha = 0.3f))
                                        .horizontalScroll(rememberScrollState())
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val tabsList = listOf(
                                        Triple("dashboard", "Dashboard", Icons.Default.Dashboard),
                                        Triple("presensi", "Presensi", Icons.Default.CheckCircle),
                                        Triple("data_kelas", "Data Kelas", Icons.Default.Class),
                                        Triple("data_siswa", "Data Siswa", Icons.Default.People),
                                        Triple("jurnal", "Jurnal", Icons.Default.Book),
                                        Triple("arsip", "Arsip", Icons.Default.FolderOpen),
                                        Triple("daftar_nilai", "Daftar Nilai", Icons.Default.Grade),
                                        Triple("kop_config", "KOP & TTD", Icons.Default.ContactMail)
                                    )

                                    tabsList.forEach { (tabId, label, icon) ->
                                        val isActive = activeTab == tabId
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(
                                                    if (isActive) Color(0xFF4F46E5) else Color.White.copy(alpha = 0.08f)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isActive) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                                .clickable { activeTab = tabId }
                                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = label,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = label,
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }

                                // Render Active Screen Content based on selection
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    when (activeTab) {
                                        "dashboard" -> DashboardScreen(
                                            classes = classes,
                                            students = students,
                                            attendance = attendance,
                                            onBackToProfile = { currentScreen = "landing" }
                                        )
                                        "presensi" -> PresensiScreen(
                                            classes = classes,
                                            students = students,
                                            attendance = attendance,
                                            settings = settings,
                                            kop = kopConfig,
                                            onSaveAttendance = { recs -> viewModel.saveAttendance(recs) },
                                            onSetAllPresent = { cId, date, time, ids -> viewModel.setAllPresent(cId, date, time, ids) },
                                            onResetAttendance = { cId, date -> viewModel.resetAttendance(cId, date) },
                                            onUpdateGraceTime = { mins, str -> viewModel.updateSettings(mins, str, settings?.weightTp ?: 0.3, settings?.weightUts ?: 0.2, settings?.weightUas ?: 0.2, settings?.weightPraktek ?: 0.3) }
                                        )
                                        "data_kelas" -> DataKelasScreen(
                                            classes = classes,
                                            onAddClass = { name -> viewModel.addClass(name) },
                                            onUpdateClass = { id, name -> viewModel.updateClass(id, name) },
                                            onDeleteClass = { id, name -> viewModel.deleteClass(id, name) }
                                        )
                                        "data_siswa" -> DataSiswaScreen(
                                            classes = classes,
                                            students = students,
                                            onAddStudent = { name, gender, cId -> viewModel.addStudent(name, gender, cId) },
                                            onUpdateStudent = { id, name, gender, cId -> viewModel.updateStudent(id, name, gender, cId) },
                                            onDeleteStudent = { id, name, gender, cId -> viewModel.deleteStudent(id, name, gender, cId) }
                                        )
                                        "jurnal" -> JurnalScreen(
                                            classes = classes,
                                            journals = journals,
                                            kop = kopConfig,
                                            onAddJournal = { cId, date, hrs, tit, obj, ref, nts, img -> viewModel.addJournal(cId, date, hrs, tit, obj, ref, nts, img) },
                                            onDeleteJournal = { jrn -> viewModel.deleteJournal(jrn) }
                                        )
                                        "arsip" -> ArsipScreen(
                                            archives = archives,
                                            onAddArchive = { name, type, uri, date -> viewModel.addArchive(name, type, uri, date) },
                                            onUpdateArchive = { id, name, type, uri, date -> viewModel.updateArchive(id, name, type, uri, date) },
                                            onDeleteArchive = { arc -> viewModel.deleteArchive(arc) }
                                        )
                                        "daftar_nilai" -> NilaiScreen(
                                            classes = classes,
                                            students = students,
                                            grades = grades,
                                            settings = settings,
                                            kop = kopConfig,
                                            onSaveGrade = { sId, cId, tps, uts, uas, prk -> viewModel.saveGrade(sId, cId, tps, uts, uas, prk) },
                                            onUpdateWeights = { mins, str, wTp, wUts, wUas, wPrk -> viewModel.updateSettings(mins, str, wTp, wUts, wUas, wPrk) }
                                        )
                                        "kop_config" -> KopScreen(
                                            kop = kopConfig,
                                            onSaveKop = { conf -> viewModel.updateKopConfig(conf) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
