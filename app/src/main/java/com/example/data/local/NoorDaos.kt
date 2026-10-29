package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY verseNumber ASC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE verseNumber = :verseNumber")
    suspend fun deleteBookmarkByVerse(verseNumber: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE verseNumber = :verseNumber)")
    fun isVerseBookmarked(verseNumber: Int): Flow<Boolean>
}

@Dao
interface SavedWordDao {
    @Query("SELECT * FROM saved_words ORDER BY timestamp DESC")
    fun getAllSavedWords(): Flow<List<SavedWordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedWord(word: SavedWordEntity)

    @Query("DELETE FROM saved_words WHERE wordId = :wordId")
    suspend fun deleteSavedWord(wordId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_words WHERE wordId = :wordId)")
    fun isWordSaved(wordId: String): Flow<Boolean>
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM learning_progress ORDER BY verseNumber ASC")
    fun getAllCompletedVerses(): Flow<List<ProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markVerseCompleted(progress: ProgressEntity)

    @Query("DELETE FROM learning_progress WHERE verseNumber = :verseNumber")
    suspend fun unmarkVerseCompleted(verseNumber: Int)

    @Query("SELECT COUNT(*) FROM learning_progress WHERE isCompleted = 1")
    fun getCompletedVerseCount(): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM learning_progress WHERE verseNumber = :verseNumber AND isCompleted = 1)")
    fun isVerseCompleted(verseNumber: Int): Flow<Boolean>
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quiz_records ORDER BY timestamp DESC")
    fun getAllQuizRecords(): Flow<List<QuizRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: QuizRecordEntity)
}
