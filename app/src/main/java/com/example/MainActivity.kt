package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.PlaybackState
import com.example.ui.NoorViewModel
import com.example.ui.Screen
import com.example.ui.components.FloatingAudioBar
import com.example.ui.components.WordAnalysisBottomSheet
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.GrammarCenterScreen
import com.example.ui.screens.GrammarDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QuranReaderScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NoorTheme
import com.example.ui.theme.QuranArabicFontFamily
import com.example.ui.theme.UrduFontFamily

class MainActivity : ComponentActivity() {

    private val viewModel: NoorViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val arabicFontSize by viewModel.arabicFontSize.collectAsState()
            val urduFontSize by viewModel.urduFontSize.collectAsState()

            val currentScreen by viewModel.currentScreen.collectAsState()
            val verses by viewModel.verses.collectAsState()
            val bookmarks by viewModel.bookmarks.collectAsState()
            val savedWords by viewModel.savedWords.collectAsState()
            val completedCount by viewModel.completedVersesCount.collectAsState()
            val completedVerses by viewModel.completedVerses.collectAsState()

            val selectedWordPair by viewModel.selectedWord.collectAsState()
            val selectedTafseerVerse by viewModel.selectedTafseerVerse.collectAsState()

            val searchQuery by viewModel.searchQuery.collectAsState()
            val searchResults by viewModel.searchResults.collectAsState()

            val quizState by viewModel.quizState.collectAsState()

            val playbackState by viewModel.audioPlayer.playbackState.collectAsState()
            val currentPos by viewModel.audioPlayer.currentPosition.collectAsState()
            val duration by viewModel.audioPlayer.duration.collectAsState()
            val isRepeat by viewModel.audioPlayer.isRepeatEnabled.collectAsState()

            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            NoorTheme(
                darkTheme = isDarkTheme,
                arabicFontSize = arabicFontSize,
                urduFontSize = urduFontSize
            ) {
                // Global BackHandler for non-home screens
                BackHandler(enabled = currentScreen != Screen.Home && currentScreen != Screen.Splash) {
                    if (selectedWordPair != null) {
                        viewModel.dismissWordBottomSheet()
                    } else if (selectedTafseerVerse != null) {
                        viewModel.dismissTafseer()
                    } else {
                        viewModel.navigateBack()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Main Screen Flow
                        when (val screen = currentScreen) {
                            is Screen.Splash -> {
                                SplashScreen(
                                    onSplashFinished = {
                                        viewModel.replaceScreen(Screen.Home)
                                    }
                                )
                            }
                            is Screen.Home -> {
                                HomeScreen(
                                    completedCount = completedCount,
                                    bookmarksCount = bookmarks.size,
                                    savedWordsCount = savedWords.size,
                                    onNavigate = { dest -> viewModel.navigateTo(dest) }
                                )
                            }
                            is Screen.Reader -> {
                                QuranReaderScreen(
                                    verses = verses,
                                    initialVerse = screen.initialVerse,
                                    bookmarks = bookmarks,
                                    completedVerses = completedVerses,
                                    playbackState = playbackState,
                                    onBack = { viewModel.navigateBack() },
                                    onWordClick = { verse, word -> viewModel.selectWord(verse, word) },
                                    onPlayVerse = { vNum -> viewModel.audioPlayer.togglePlayPause(vNum) },
                                    onToggleBookmark = { vNum -> viewModel.toggleBookmark(vNum) },
                                    onToggleCompleted = { vNum -> viewModel.toggleVerseCompleted(vNum) },
                                    onShowTafseer = { v -> viewModel.showTafseer(v) }
                                )
                            }
                            is Screen.GrammarCenter -> {
                                GrammarCenterScreen(
                                    onBack = { viewModel.navigateBack() },
                                    onSelectLesson = { id -> viewModel.navigateTo(Screen.GrammarDetail(id)) }
                                )
                            }
                            is Screen.GrammarDetail -> {
                                GrammarDetailScreen(
                                    lessonId = screen.lessonId,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            is Screen.Search -> {
                                SearchScreen(
                                    searchQuery = searchQuery,
                                    searchResults = searchResults,
                                    onQueryChanged = { q -> viewModel.onSearchQueryChanged(q) },
                                    onSelectVerse = { vNum ->
                                        viewModel.navigateTo(Screen.Reader(vNum))
                                    },
                                    onSelectWord = { verse, word ->
                                        viewModel.selectWord(verse, word)
                                    },
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            is Screen.Bookmarks -> {
                                BookmarksScreen(
                                    bookmarks = bookmarks,
                                    savedWords = savedWords,
                                    verses = verses,
                                    onSelectVerse = { vNum -> viewModel.navigateTo(Screen.Reader(vNum)) },
                                    onSelectWord = { verse, word -> viewModel.selectWord(verse, word) },
                                    onRemoveBookmark = { vNum -> viewModel.toggleBookmark(vNum) },
                                    onRemoveSavedWord = { wordId -> viewModel.removeSavedWord(wordId) },
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            is Screen.Quiz -> {
                                QuizScreen(
                                    quizType = screen.type,
                                    quizState = quizState,
                                    onSelectOption = { idx -> viewModel.selectQuizOption(idx) },
                                    onSubmitAnswer = { viewModel.submitQuizAnswer() },
                                    onNextQuestion = { viewModel.nextQuizQuestion() },
                                    onRestartQuiz = { viewModel.startQuiz(screen.type) },
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            is Screen.Settings -> {
                                SettingsScreen(
                                    isDarkTheme = isDarkTheme,
                                    arabicFontSize = arabicFontSize,
                                    urduFontSize = urduFontSize,
                                    onToggleTheme = { viewModel.toggleTheme() },
                                    onArabicFontSizeChanged = { sz -> viewModel.setArabicFontSize(sz) },
                                    onUrduFontSizeChanged = { sz -> viewModel.setUrduFontSize(sz) },
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                        }

                        // Floating Audio Bar (displayed when playback is active or in reader)
                        val showAudioBar = playbackState !is PlaybackState.Idle && currentScreen !is Screen.Splash
                        AnimatedVisibility(
                            visible = showAudioBar,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            val activeVerseNum = when (playbackState) {
                                is PlaybackState.Playing -> (playbackState as PlaybackState.Playing).verseNumber
                                is PlaybackState.Paused -> (playbackState as PlaybackState.Paused).verseNumber
                                else -> 1
                            }

                            FloatingAudioBar(
                                playbackState = playbackState,
                                currentPosition = currentPos,
                                duration = duration,
                                isRepeatEnabled = isRepeat,
                                onPlayPause = {
                                    viewModel.audioPlayer.togglePlayPause(activeVerseNum)
                                },
                                onNextVerse = {
                                    if (activeVerseNum < 92) viewModel.audioPlayer.playVerse(activeVerseNum + 1)
                                },
                                onPrevVerse = {
                                    if (activeVerseNum > 1) viewModel.audioPlayer.playVerse(activeVerseNum - 1)
                                },
                                onToggleRepeat = {
                                    viewModel.audioPlayer.toggleRepeat()
                                },
                                onSeek = { pos ->
                                    viewModel.audioPlayer.seekTo(pos)
                                },
                                onClose = {
                                    viewModel.audioPlayer.stop()
                                }
                            )
                        }

                        // Interactive Word Analysis Bottom Sheet
                        selectedWordPair?.let { (verse, word) ->
                            val isWordSaved = savedWords.any { it.wordId == word.wordId }
                            WordAnalysisBottomSheet(
                                verse = verse,
                                word = word,
                                isSaved = isWordSaved,
                                sheetState = sheetState,
                                onDismiss = { viewModel.dismissWordBottomSheet() },
                                onPlayWordAudio = { url -> viewModel.audioPlayer.playWordAudio(url) },
                                onToggleSave = { viewModel.toggleSaveWord(word, verse.verseNumber) },
                                onSearchRoot = { root ->
                                    viewModel.dismissWordBottomSheet()
                                    viewModel.onSearchQueryChanged(root)
                                    viewModel.navigateTo(Screen.Search)
                                }
                            )
                        }

                        // Tafseer Context Dialog
                        selectedTafseerVerse?.let { v ->
                            AlertDialog(
                                onDismissRequest = { viewModel.dismissTafseer() },
                                title = {
                                    Text(
                                        text = "تفسیر و سیاقی نکات (آیت ${v.verseNumber})",
                                        fontFamily = UrduFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = GoldLight
                                    )
                                },
                                text = {
                                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                        Text(
                                            text = v.arabicText,
                                            fontFamily = QuranArabicFontFamily,
                                            fontSize = 18.sp,
                                            color = GoldLight,
                                            textAlign = TextAlign.Right,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = v.urduTranslation,
                                            fontFamily = UrduFontFamily,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Right,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = v.tafseerNote,
                                                fontFamily = UrduFontFamily,
                                                fontSize = 13.sp,
                                                lineHeight = 22.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = { viewModel.dismissTafseer() },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                                    ) {
                                        Text("ٹھیک ہے", fontFamily = UrduFontFamily, fontWeight = FontWeight.Bold)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
