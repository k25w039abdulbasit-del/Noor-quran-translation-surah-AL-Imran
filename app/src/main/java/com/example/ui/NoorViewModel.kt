package com.example.ui

import android.app.Application
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookmarkEntity
import com.example.data.local.NoorDatabase
import com.example.data.local.SavedWordEntity
import com.example.data.model.GrammarLesson
import com.example.data.model.QuranWord
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizType
import com.example.data.model.Verse
import com.example.data.repository.QuranRepository
import com.example.data.repository.UserLearningRepository
import com.example.service.PlaybackState
import com.example.service.QuranAudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    data class Reader(val initialVerse: Int = 1) : Screen()
    object GrammarCenter : Screen()
    data class GrammarDetail(val lessonId: Int) : Screen()
    object Search : Screen()
    object Bookmarks : Screen()
    data class Quiz(val type: QuizType) : Screen()
    object Settings : Screen()
}

data class QuizState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isSubmitted: Boolean = false,
    val score: Int = 0,
    val isFinished: Boolean = false
)

class NoorViewModel(application: Application) : AndroidViewModel(application) {

    val quranRepository = QuranRepository(application)
    private val db = NoorDatabase.getDatabase(application)
    val learningRepository = UserLearningRepository(
        bookmarkDao = db.bookmarkDao(),
        savedWordDao = db.savedWordDao(),
        progressDao = db.progressDao(),
        quizDao = db.quizDao()
    )

    val audioPlayer = QuranAudioPlayer(application)

    // Navigation Stack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Splash))
    val currentScreen: StateFlow<Screen> = _screenStack.map { it.lastOrNull() ?: Screen.Home }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Splash)

    // Verses
    private val _verses = MutableStateFlow<List<Verse>>(emptyList())
    val verses: StateFlow<List<Verse>> = _verses.asStateFlow()

    // Selected Word for BottomSheet
    private val _selectedWord = MutableStateFlow<Pair<Verse, QuranWord>?>(null)
    val selectedWord: StateFlow<Pair<Verse, QuranWord>?> = _selectedWord.asStateFlow()

    // Tafseer / Context Dialog
    private val _selectedTafseerVerse = MutableStateFlow<Verse?>(null)
    val selectedTafseerVerse: StateFlow<Verse?> = _selectedTafseerVerse.asStateFlow()

    // Bookmarks and Saved Words
    val bookmarks: StateFlow<List<BookmarkEntity>> = learningRepository.bookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedWords: StateFlow<List<SavedWordEntity>> = learningRepository.savedWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedVersesCount: StateFlow<Int> = learningRepository.completedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedVerses = learningRepository.completedVerses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow(QuranRepository.SearchResults())
    val searchResults: StateFlow<QuranRepository.SearchResults> = _searchResults.asStateFlow()

    // Quizzes
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    // Settings
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _arabicFontSize = MutableStateFlow(28.sp)
    val arabicFontSize: StateFlow<TextUnit> = _arabicFontSize.asStateFlow()

    private val _urduFontSize = MutableStateFlow(17.sp)
    val urduFontSize: StateFlow<TextUnit> = _urduFontSize.asStateFlow()

    init {
        loadData()
        audioPlayer.setOnAutoAdvanceListener { nextVerse ->
            // Mark completed when finished
            viewModelScope.launch {
                learningRepository.toggleVerseCompleted(nextVerse - 1, isCompleted = false)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _verses.value = quranRepository.getAllVerses()
        }
    }

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        val current = _screenStack.value.toMutableList()
        current.add(screen)
        _screenStack.value = current
    }

    fun replaceScreen(screen: Screen) {
        _screenStack.value = listOf(screen)
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value.toMutableList()
        if (current.size > 1) {
            current.removeAt(current.size - 1)
            _screenStack.value = current
            return true
        }
        return false
    }

    // Word Bottom Sheet
    fun selectWord(verse: Verse, word: QuranWord) {
        _selectedWord.value = verse to word
    }

    fun dismissWordBottomSheet() {
        _selectedWord.value = null
    }

    // Tafseer Dialog
    fun showTafseer(verse: Verse) {
        _selectedTafseerVerse.value = verse
    }

    fun dismissTafseer() {
        _selectedTafseerVerse.value = null
    }

    // Bookmark & Progress Actions
    fun toggleBookmark(verseNumber: Int) {
        viewModelScope.launch {
            val isBookmarked = bookmarks.value.any { it.verseNumber == verseNumber }
            learningRepository.toggleBookmark(verseNumber, isBookmarked)
        }
    }

    fun toggleSaveWord(word: QuranWord, verseNumber: Int) {
        viewModelScope.launch {
            val isSaved = savedWords.value.any { it.wordId == word.wordId }
            learningRepository.toggleSaveWord(word, verseNumber, isSaved)
        }
    }

    fun removeSavedWord(wordId: String) {
        viewModelScope.launch {
            learningRepository.deleteSavedWord(wordId)
        }
    }

    fun toggleVerseCompleted(verseNumber: Int) {
        viewModelScope.launch {
            val isDone = completedVerses.value.any { it.verseNumber == verseNumber }
            learningRepository.toggleVerseCompleted(verseNumber, isDone)
        }
    }

    // Search Actions
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _searchResults.value = quranRepository.search(query)
    }

    // Quiz Actions
    fun startQuiz(type: QuizType) {
        val questions = if (type == QuizType.VOCABULARY) {
            quranRepository.generateVocabularyQuiz(10)
        } else {
            quranRepository.generateGrammarQuiz(10)
        }
        _quizState.value = QuizState(
            questions = questions,
            currentIndex = 0,
            selectedOptionIndex = null,
            isSubmitted = false,
            score = 0,
            isFinished = false
        )
        navigateTo(Screen.Quiz(type))
    }

    fun selectQuizOption(index: Int) {
        if (!_quizState.value.isSubmitted && !_quizState.value.isFinished) {
            _quizState.value = _quizState.value.copy(selectedOptionIndex = index)
        }
    }

    fun submitQuizAnswer() {
        val state = _quizState.value
        val currentQ = state.questions.getOrNull(state.currentIndex) ?: return
        if (state.selectedOptionIndex == null || state.isSubmitted) return

        val isCorrect = state.selectedOptionIndex == currentQ.correctIndex
        val newScore = if (isCorrect) state.score + 1 else state.score

        _quizState.value = state.copy(
            isSubmitted = true,
            score = newScore
        )
    }

    fun nextQuizQuestion() {
        val state = _quizState.value
        if (state.currentIndex + 1 < state.questions.size) {
            _quizState.value = state.copy(
                currentIndex = state.currentIndex + 1,
                selectedOptionIndex = null,
                isSubmitted = false
            )
        } else {
            _quizState.value = state.copy(isFinished = true)
            // Persist quiz score
            viewModelScope.launch {
                learningRepository.saveQuizRecord(
                    quizType = state.questions.firstOrNull()?.type?.name ?: "GENERAL",
                    score = state.score,
                    total = state.questions.size
                )
            }
        }
    }

    // Settings
    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setArabicFontSize(size: TextUnit) {
        _arabicFontSize.value = size
    }

    fun setUrduFontSize(size: TextUnit) {
        _urduFontSize.value = size
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
