package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val verseNumber: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "saved_words")
data class SavedWordEntity(
    @PrimaryKey val wordId: String,
    val arabic: String,
    val urdu: String,
    val transliteration: String,
    val root: String,
    val category: String,
    val verseNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "learning_progress")
data class ProgressEntity(
    @PrimaryKey val verseNumber: Int,
    val isCompleted: Boolean = true,
    val lastReadTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_records")
data class QuizRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizType: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)
