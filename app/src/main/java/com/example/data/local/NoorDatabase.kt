package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BookmarkEntity::class,
        SavedWordEntity::class,
        ProgressEntity::class,
        QuizRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NoorDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun savedWordDao(): SavedWordDao
    abstract fun progressDao(): ProgressDao
    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var INSTANCE: NoorDatabase? = null

        fun getDatabase(context: Context): NoorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoorDatabase::class.java,
                    "noor_quran_learning.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
