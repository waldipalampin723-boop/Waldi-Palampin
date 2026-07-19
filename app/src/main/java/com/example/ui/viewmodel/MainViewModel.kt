package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(database)

    // Exposed States
    val profile = repository.profile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val allClasses = repository.allClasses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allStudents = repository.allStudents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allAttendance = repository.allAttendance.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allJournals = repository.allJournals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allArchives = repository.allArchives.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allGrades = repository.allGrades.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val settings = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val kopConfig = repository.kopConfig.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            // Seed default values if empty on first startup
            launch {
                repository.profile.first() ?: repository.saveProfile(
                    TeacherProfile(
                        name = "Waldi Palampin, S.Pd.",
                        schoolName = "SD Negeri Jaya",
                        subject = "Guru Kelas / Wali Kelas IV",
                        educationHistory = "S1 Pendidikan Guru Sekolah Dasar - Universitas Negeri"
                    )
                )
            }
            launch {
                repository.kopConfig.first() ?: repository.saveKopConfig(
                    KopConfig(
                        govLine1 = "PEMERINTAH KABUPATEN POSO",
                        deptLine2 = "DINAS PENDIDIKAN DAN KEBUDAYAAN",
                        schoolName = "SD NEGERI JAYA",
                        schoolAddress = "Jl. Poros Trans Sulawesi No. 45",
                        schoolContact = "0822-9111-2026",
                        postalCode = "94611",
                        schoolEmail = "sdn.jaya@poso.sch.id",
                        nss = "101180203001",
                        nsb = "202180203002",
                        npsn = "40201234",
                        principalName = "Drs. H. Ahmad Yani, M.Si.",
                        principalNip = "196805121990031005",
                        teacherName = "Waldi Palampin, S.Pd.",
                        teacherNip = "199210042018011003",
                        location = "Poso"
                    )
                )
            }
            launch {
                repository.settings.first() ?: repository.saveSettings(
                    SystemSettings(
                        lateGraceMinutes = 30,
                        lateGraceTimeStr = "07:30",
                        weightTp = 0.3,
                        weightUts = 0.2,
                        weightUas = 0.2,
                        weightPraktek = 0.3
                    )
                )
            }
        }
    }

    // --- Profile Operations ---
    fun updateProfile(
        name: String,
        schoolName: String,
        subject: String,
        educationHistory: String,
        coverUri: String?,
        profileUri: String?,
        sliderUris: String
    ) {
        viewModelScope.launch {
            val current = profile.value ?: TeacherProfile()
            repository.saveProfile(
                current.copy(
                    name = name,
                    schoolName = schoolName,
                    subject = subject,
                    educationHistory = educationHistory,
                    coverUri = coverUri,
                    profileUri = profileUri,
                    sliderImageUris = sliderUris
                )
            )
        }
    }

    // --- Class Operations ---
    fun addClass(className: String) {
        viewModelScope.launch {
            repository.insertClass(ClassEntity(className = className))
        }
    }

    fun updateClass(id: Int, className: String) {
        viewModelScope.launch {
            repository.updateClass(ClassEntity(id = id, className = className))
        }
    }

    fun deleteClass(id: Int, className: String) {
        viewModelScope.launch {
            repository.deleteClass(ClassEntity(id = id, className = className))
        }
    }

    // --- Student Operations ---
    fun addStudent(name: String, gender: String, classId: Int) {
        viewModelScope.launch {
            repository.insertStudent(StudentEntity(name = name, gender = gender, classId = classId))
        }
    }

    fun updateStudent(id: Int, name: String, gender: String, classId: Int) {
        viewModelScope.launch {
            repository.updateStudent(StudentEntity(id = id, name = name, gender = gender, classId = classId))
        }
    }

    fun deleteStudent(id: Int, name: String, gender: String, classId: Int) {
        viewModelScope.launch {
            repository.deleteStudent(StudentEntity(id = id, name = name, gender = gender, classId = classId))
        }
    }

    // --- Attendance Operations ---
    fun saveAttendance(records: List<AttendanceRecord>) {
        viewModelScope.launch {
            if (records.isNotEmpty()) {
                // Delete previous records for the same class and date to overwrite
                repository.deleteAttendanceByClassAndDate(records[0].classId, records[0].date)
                repository.insertAttendanceList(records)
            }
        }
    }

    fun setAllPresent(classId: Int, date: String, time: String, studentIds: List<Int>) {
        viewModelScope.launch {
            repository.deleteAttendanceByClassAndDate(classId, date)
            val records = studentIds.map { sId ->
                AttendanceRecord(
                    studentId = sId,
                    classId = classId,
                    date = date,
                    time = time,
                    status = "Hadir",
                    isLate = false
                )
            }
            repository.insertAttendanceList(records)
        }
    }

    fun resetAttendance(classId: Int, date: String) {
        viewModelScope.launch {
            repository.deleteAttendanceByClassAndDate(classId, date)
        }
    }

    // --- Journal Operations ---
    fun addJournal(
        classId: Int,
        date: String,
        teachingHours: String,
        lessonTitle: String,
        learningObjective: String,
        teacherReflection: String,
        specialNotes: String,
        photoUri: String?
    ) {
        viewModelScope.launch {
            repository.insertJournal(
                TeachingJournal(
                    classId = classId,
                    date = date,
                    teachingHours = teachingHours,
                    lessonTitle = lessonTitle,
                    learningObjective = learningObjective,
                    teacherReflection = teacherReflection,
                    specialNotes = specialNotes,
                    photoUri = photoUri
                )
            )
        }
    }

    fun updateJournal(
        id: Int,
        classId: Int,
        date: String,
        teachingHours: String,
        lessonTitle: String,
        learningObjective: String,
        teacherReflection: String,
        specialNotes: String,
        photoUri: String?
    ) {
        viewModelScope.launch {
            repository.insertJournal(
                TeachingJournal(
                    id = id,
                    classId = classId,
                    date = date,
                    teachingHours = teachingHours,
                    lessonTitle = lessonTitle,
                    learningObjective = learningObjective,
                    teacherReflection = teacherReflection,
                    specialNotes = specialNotes,
                    photoUri = photoUri
                )
            )
        }
    }

    fun deleteJournal(journal: TeachingJournal) {
        viewModelScope.launch {
            repository.deleteJournal(journal)
        }
    }

    // --- Archive Operations ---
    fun addArchive(fileName: String, fileType: String, fileUri: String?, date: String) {
        viewModelScope.launch {
            repository.insertArchive(
                AdminArchive(
                    fileName = fileName,
                    fileType = fileType,
                    fileUri = fileUri,
                    uploadedDate = date
                )
            )
        }
    }

    fun updateArchive(id: Int, fileName: String, fileType: String, fileUri: String?, date: String) {
        viewModelScope.launch {
            repository.updateArchive(
                AdminArchive(
                    id = id,
                    fileName = fileName,
                    fileType = fileType,
                    fileUri = fileUri,
                    uploadedDate = date
                )
            )
        }
    }

    fun deleteArchive(archive: AdminArchive) {
        viewModelScope.launch {
            repository.deleteArchive(archive)
        }
    }

    // --- Grade Operations ---
    fun saveGrade(
        studentId: Int,
        classId: Int,
        tpGrades: List<Double>,
        utsGrade: Double,
        uasGrade: Double,
        praktekGrade: Double
    ) {
        viewModelScope.launch {
            val weights = settings.value ?: SystemSettings()
            val avgTp = if (tpGrades.isNotEmpty()) tpGrades.average() else 0.0

            // Formula: Nilai Rata-rata TP * WeightTP + UTS * WeightUTS + UAS * WeightUAS + Praktek * WeightPraktek
            val reportCardGrade = (avgTp * weights.weightTp) +
                    (utsGrade * weights.weightUts) +
                    (uasGrade * weights.weightUas) +
                    (praktekGrade * weights.weightPraktek)

            val existing = repository.getGradeByStudent(studentId)
            val tpStr = tpGrades.joinToString(",") { it.toString() }

            repository.insertGrade(
                StudentGrade(
                    id = existing?.id ?: 0,
                    studentId = studentId,
                    classId = classId,
                    tpGrades = tpStr,
                    utsGrade = utsGrade,
                    uasGrade = uasGrade,
                    praktekGrade = praktekGrade,
                    averageTp = avgTp,
                    reportCardGrade = reportCardGrade
                )
            )
        }
    }

    // --- Settings & Weights Operations ---
    fun updateSettings(
        lateGraceMinutes: Int,
        lateGraceTimeStr: String,
        weightTp: Double,
        weightUts: Double,
        weightUas: Double,
        weightPraktek: Double
    ) {
        viewModelScope.launch {
            val current = settings.value ?: SystemSettings()
            repository.saveSettings(
                current.copy(
                    lateGraceMinutes = lateGraceMinutes,
                    lateGraceTimeStr = lateGraceTimeStr,
                    weightTp = weightTp,
                    weightUts = weightUts,
                    weightUas = weightUas,
                    weightPraktek = weightPraktek
                )
            )
            
            // Recalculate all grades after weights are modified to ensure consistency
            launch {
                val grades = allGrades.value
                grades.forEach { grade ->
                    val tpList = if (grade.tpGrades.isNotEmpty()) {
                        grade.tpGrades.split(",").mapNotNull { it.toDoubleOrNull() }
                    } else emptyList()
                    val avgTp = if (tpList.isNotEmpty()) tpList.average() else 0.0
                    val reportCardGrade = (avgTp * weightTp) +
                            (grade.utsGrade * weightUts) +
                            (grade.uasGrade * weightUas) +
                            (grade.praktekGrade * weightPraktek)
                    repository.insertGrade(
                        grade.copy(
                            averageTp = avgTp,
                            reportCardGrade = reportCardGrade
                        )
                    )
                }
            }
        }
    }

    // --- KOP Config Operations ---
    fun updateKopConfig(config: KopConfig) {
        viewModelScope.launch {
            repository.saveKopConfig(config)
        }
    }
}
