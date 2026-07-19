package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teacher_profile")
data class TeacherProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Nama Guru",
    val schoolName: String = "Nama Sekolah",
    val subject: String = "Mata Pelajaran",
    val educationHistory: String = "Riwayat Pendidikan",
    val coverUri: String? = null,
    val profileUri: String? = null,
    val sliderImageUris: String = "" // Comma-separated URIs
)

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val className: String
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val gender: String, // "Laki-laki" or "Perempuan"
    val classId: Int
)

@Entity(tableName = "attendance")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val classId: Int,
    val date: String, // yyyy-MM-dd
    val time: String, // HH:mm
    val status: String, // "Hadir", "Tidak Hadir", "Terlambat", "Izin", "Sakit"
    val isLate: Boolean = false
)

@Entity(tableName = "teaching_journals")
data class TeachingJournal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val classId: Int,
    val date: String, // yyyy-MM-dd
    val teachingHours: String,
    val lessonTitle: String,
    val learningObjective: String,
    val teacherReflection: String,
    val specialNotes: String,
    val photoUri: String? = null
)

@Entity(tableName = "admin_archives")
data class AdminArchive(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val fileType: String, // "PDF", "Word", "Excel"
    val fileUri: String? = null,
    val uploadedDate: String
)

@Entity(tableName = "student_grades")
data class StudentGrade(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val classId: Int,
    val tpGrades: String = "", // Comma-separated grades, e.g. "80,85,90"
    val utsGrade: Double = 0.0,
    val uasGrade: Double = 0.0,
    val praktekGrade: Double = 0.0,
    val averageTp: Double = 0.0,
    val reportCardGrade: Double = 0.0
)

@Entity(tableName = "system_settings")
data class SystemSettings(
    @PrimaryKey val id: Int = 1,
    val lateGraceMinutes: Int = 30,
    val lateGraceTimeStr: String = "07:30",
    val weightTp: Double = 0.3,
    val weightUts: Double = 0.2,
    val weightUas: Double = 0.2,
    val weightPraktek: Double = 0.3
)

@Entity(tableName = "kop_config")
data class KopConfig(
    @PrimaryKey val id: Int = 1,
    val govLine1: String = "PEMERINTAH KABUPATEN / KOTA",
    val deptLine2: String = "DINAS PENDIDIKAN",
    val schoolName: String = "SD NEGERI JAYA",
    val schoolAddress: String = "Jl. Raya Pendidikan No. 123",
    val schoolContact: String = "0812-3456-7890",
    val postalCode: String = "12345",
    val schoolEmail: String = "info@sdnjaya.sch.id",
    val nss: String = "101020304050",
    val nsb: String = "202030405060",
    val npsn: String = "10203040",
    val principalName: String = "Kepala Sekolah, S.Pd., M.Pd.",
    val principalNip: String = "197508122000031002",
    val teacherName: String = "Guru Kelas, S.Pd.",
    val teacherNip: String = "198804152015042001",
    val location: String = "Jakarta",
    val tutWuriLogoUri: String? = null,
    val schoolLogoUri: String? = null
)
