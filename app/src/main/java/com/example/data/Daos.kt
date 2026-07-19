package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherProfileDao {
    @Query("SELECT * FROM teacher_profile WHERE id = 1")
    fun getProfile(): Flow<TeacherProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: TeacherProfile)
}

@Dao
interface ClassDao {
    @Query("SELECT * FROM classes ORDER BY className ASC")
    fun getAllClasses(): Flow<List<ClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classEntity: ClassEntity): Long

    @Update
    suspend fun updateClass(classEntity: ClassEntity)

    @Delete
    suspend fun deleteClass(classEntity: ClassEntity)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY name ASC")
    fun getStudentsByClass(classId: Int): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC, time DESC")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE classId = :classId AND date = :date")
    fun getAttendanceByClassAndDate(classId: Int, date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(records: List<AttendanceRecord>)

    @Query("DELETE FROM attendance WHERE classId = :classId AND date = :date")
    suspend fun deleteAttendanceByClassAndDate(classId: Int, date: String)

    @Query("DELETE FROM attendance WHERE id = :id")
    suspend fun deleteAttendanceById(id: Int)
}

@Dao
interface TeachingJournalDao {
    @Query("SELECT * FROM teaching_journals ORDER BY date DESC")
    fun getAllJournals(): Flow<List<TeachingJournal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: TeachingJournal)

    @Update
    suspend fun updateJournal(journal: TeachingJournal)

    @Delete
    suspend fun deleteJournal(journal: TeachingJournal)
}

@Dao
interface AdminArchiveDao {
    @Query("SELECT * FROM admin_archives ORDER BY uploadedDate DESC")
    fun getAllArchives(): Flow<List<AdminArchive>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArchive(archive: AdminArchive)

    @Update
    suspend fun updateArchive(archive: AdminArchive)

    @Delete
    suspend fun deleteArchive(archive: AdminArchive)
}

@Dao
interface StudentGradeDao {
    @Query("SELECT * FROM student_grades")
    fun getAllGrades(): Flow<List<StudentGrade>>

    @Query("SELECT * FROM student_grades WHERE classId = :classId")
    fun getGradesByClass(classId: Int): Flow<List<StudentGrade>>

    @Query("SELECT * FROM student_grades WHERE studentId = :studentId LIMIT 1")
    suspend fun getGradeByStudent(studentId: Int): StudentGrade?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: StudentGrade)

    @Query("DELETE FROM student_grades WHERE studentId = :studentId")
    suspend fun deleteGradeByStudent(studentId: Int)
}

@Dao
interface SystemSettingsDao {
    @Query("SELECT * FROM system_settings WHERE id = 1")
    fun getSettings(): Flow<SystemSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: SystemSettings)
}

@Dao
interface KopConfigDao {
    @Query("SELECT * FROM kop_config WHERE id = 1")
    fun getKopConfig(): Flow<KopConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateKopConfig(config: KopConfig)
}
