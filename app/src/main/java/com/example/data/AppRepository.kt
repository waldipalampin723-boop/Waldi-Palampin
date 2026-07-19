package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {

    // Teacher Profile
    val profile: Flow<TeacherProfile?> = db.teacherProfileDao().getProfile()
    suspend fun saveProfile(profile: TeacherProfile) = db.teacherProfileDao().insertOrUpdateProfile(profile)

    // Classes
    val allClasses: Flow<List<ClassEntity>> = db.classDao().getAllClasses()
    suspend fun insertClass(classEntity: ClassEntity): Long = db.classDao().insertClass(classEntity)
    suspend fun updateClass(classEntity: ClassEntity) = db.classDao().updateClass(classEntity)
    suspend fun deleteClass(classEntity: ClassEntity) = db.classDao().deleteClass(classEntity)

    // Students
    val allStudents: Flow<List<StudentEntity>> = db.studentDao().getAllStudents()
    fun getStudentsByClass(classId: Int): Flow<List<StudentEntity>> = db.studentDao().getStudentsByClass(classId)
    suspend fun insertStudent(student: StudentEntity) = db.studentDao().insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = db.studentDao().updateStudent(student)
    suspend fun deleteStudent(student: StudentEntity) {
        db.studentDao().deleteStudent(student)
        db.studentGradeDao().deleteGradeByStudent(student.id)
    }

    // Attendance
    val allAttendance: Flow<List<AttendanceRecord>> = db.attendanceDao().getAllAttendance()
    fun getAttendanceByClassAndDate(classId: Int, date: String): Flow<List<AttendanceRecord>> = 
        db.attendanceDao().getAttendanceByClassAndDate(classId, date)
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>> =
        db.attendanceDao().getAttendanceByDate(date)
    suspend fun insertAttendance(record: AttendanceRecord) = db.attendanceDao().insertAttendance(record)
    suspend fun insertAttendanceList(records: List<AttendanceRecord>) = db.attendanceDao().insertAttendanceList(records)
    suspend fun deleteAttendanceByClassAndDate(classId: Int, date: String) = 
        db.attendanceDao().deleteAttendanceByClassAndDate(classId, date)
    suspend fun deleteAttendanceById(id: Int) = db.attendanceDao().deleteAttendanceById(id)

    // Teaching Journals
    val allJournals: Flow<List<TeachingJournal>> = db.teachingJournalDao().getAllJournals()
    suspend fun insertJournal(journal: TeachingJournal) = db.teachingJournalDao().insertJournal(journal)
    suspend fun updateJournal(journal: TeachingJournal) = db.teachingJournalDao().updateJournal(journal)
    suspend fun deleteJournal(journal: TeachingJournal) = db.teachingJournalDao().deleteJournal(journal)

    // Archives
    val allArchives: Flow<List<AdminArchive>> = db.adminArchiveDao().getAllArchives()
    suspend fun insertArchive(archive: AdminArchive) = db.adminArchiveDao().insertArchive(archive)
    suspend fun updateArchive(archive: AdminArchive) = db.adminArchiveDao().updateArchive(archive)
    suspend fun deleteArchive(archive: AdminArchive) = db.adminArchiveDao().deleteArchive(archive)

    // Grades
    val allGrades: Flow<List<StudentGrade>> = db.studentGradeDao().getAllGrades()
    fun getGradesByClass(classId: Int): Flow<List<StudentGrade>> = db.studentGradeDao().getGradesByClass(classId)
    suspend fun getGradeByStudent(studentId: Int): StudentGrade? = db.studentGradeDao().getGradeByStudent(studentId)
    suspend fun insertGrade(grade: StudentGrade) = db.studentGradeDao().insertGrade(grade)

    // Settings
    val settings: Flow<SystemSettings?> = db.systemSettingsDao().getSettings()
    suspend fun saveSettings(settings: SystemSettings) = db.systemSettingsDao().insertOrUpdateSettings(settings)

    // KOP Config
    val kopConfig: Flow<KopConfig?> = db.kopConfigDao().getKopConfig()
    suspend fun saveKopConfig(config: KopConfig) = db.kopConfigDao().insertOrUpdateKopConfig(config)
}
