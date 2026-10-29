package com.example.data.repository

import com.example.data.local.BookmarkDao
import com.example.data.local.BookmarkEntity
import com.example.data.local.ProgressDao
import com.example.data.local.ProgressEntity
import com.example.data.local.QuizDao
import com.example.data.local.QuizRecordEntity
import com.example.data.local.SavedWordDao
import com.example.data.local.SavedWordEntity
import com.example.data.model.QuranWord
import kotlinx.coroutines.flow.Flow

class UserLearningRepository(
    private val bookmarkDao: BookmarkDao,
    private val savedWordDao: SavedWordDao,
    private val progressDao: ProgressDao,
    private val quizDao: QuizDao
) {
    val bookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
    val savedWords: Flow<List<SavedWordEntity>> = savedWordDao.getAllSavedWords()
    val completedVerses: Flow<List<ProgressEntity>> = progressDao.getAllCompletedVerses()
    val completedCount: Flow<Int> = progressDao.getCompletedVerseCount()
    val quizRecords: Flow<List<QuizRecordEntity>> = quizDao.getAllQuizRecords()

    fun isVerseBookmarked(verseNumber: Int): Flow<Boolean> = bookmarkDao.isVerseBookmarked(verseNumber)

    suspend fun toggleBookmark(verseNumber: Int, isCurrentlyBookmarked: Boolean, note: String = "") {
        if (isCurrentlyBookmarked) {
            bookmarkDao.deleteBookmarkByVerse(verseNumber)
        } else {
            bookmarkDao.insertBookmark(BookmarkEntity(verseNumber = verseNumber, note = note))
        }
    }

    fun isWordSaved(wordId: String): Flow<Boolean> = savedWordDao.isWordSaved(wordId)

    suspend fun toggleSaveWord(word: QuranWord, verseNumber: Int, isCurrentlySaved: Boolean) {
        if (isCurrentlySaved) {
            savedWordDao.deleteSavedWord(word.wordId)
        } else {
            savedWordDao.insertSavedWord(
                SavedWordEntity(
                    wordId = word.wordId,
                    arabic = word.arabic,
                    urdu = word.urdu,
                    transliteration = word.transliteration,
                    root = word.root,
                    category = word.category.name,
                    verseNumber = verseNumber
                )
            )
        }
    }

    suspend fun deleteSavedWord(wordId: String) {
        savedWordDao.deleteSavedWord(wordId)
    }

    suspend fun toggleVerseCompleted(verseNumber: Int, isCompleted: Boolean) {
        if (isCompleted) {
            progressDao.unmarkVerseCompleted(verseNumber)
        } else {
            progressDao.markVerseCompleted(ProgressEntity(verseNumber = verseNumber, isCompleted = true))
        }
    }

    fun isVerseCompleted(verseNumber: Int): Flow<Boolean> = progressDao.isVerseCompleted(verseNumber)

    suspend fun saveQuizRecord(quizType: String, score: Int, total: Int) {
        quizDao.insertRecord(
            QuizRecordEntity(
                quizType = quizType,
                score = score,
                totalQuestions = total
            )
        )
    }
}
