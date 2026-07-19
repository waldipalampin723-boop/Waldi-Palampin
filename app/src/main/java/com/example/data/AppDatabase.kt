package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TeacherProfile::class,
        ClassEntity::class,
        StudentEntity::class,
        AttendanceRecord::class,
        TeachingJournal::class,
        AdminArchive::class,
        StudentGrade::class,
        SystemSettings::class,
        KopConfig::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun teacherProfileDao(): TeacherProfileDao
    abstract fun classDao(): ClassDao
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun teachingJournalDao(): TeachingJournalDao
    abstract fun adminArchiveDao(): AdminArchiveDao
    abstract fun studentGradeDao(): StudentGradeDao
    abstract fun systemSettingsDao(): SystemSettingsDao
    abstract fun kopConfigDao(): KopConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gurupro_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
