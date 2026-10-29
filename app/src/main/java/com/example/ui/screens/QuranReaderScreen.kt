package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.local.ProgressEntity
import com.example.data.model.QuranWord
import com.example.data.model.Verse
import com.example.service.PlaybackState
import com.example.ui.components.BismillahHeader
import com.example.ui.components.OrnateVerseNumberBadge
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.LocalArabicFontSize
import com.example.ui.theme.LocalUrduFontSize
import com.example.ui.theme.QuranArabicFontFamily
import com.example.ui.theme.UrduFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    verses: List<Verse>,
    initialVerse: Int,
    bookmarks: List<BookmarkEntity>,
    completedVerses: List<ProgressEntity>,
    playbackState: PlaybackState,
    onBack: () -> Unit,
    onWordClick: (Verse, QuranWord) -> Unit,
    onPlayVerse: (Int) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onToggleCompleted: (Int) -> Unit,
    onShowTafseer: (Verse) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpTargetInput by remember { mutableStateOf("") }
    var showFontDialog by remember { mutableStateOf(false) }

    var currentArabicSize by remember { mutableStateOf(28f) }
    var currentUrduSize by remember { mutableStateOf(17f) }

    val playingVerseNumber = when (playbackState) {
        is PlaybackState.Playing -> playbackState.verseNumber
        is PlaybackState.Paused -> playbackState.verseNumber
        else -> null
    }

    LaunchedEffect(initialVerse) {
        if (initialVerse in 1..verses.size) {
            val targetIndex = (initialVerse - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
        }
    }

    // Scroll to actively playing verse
    LaunchedEffect(playingVerseNumber) {
        playingVerseNumber?.let { vNum ->
            val idx = (vNum - 1).coerceIn(0, verses.size - 1)
            listState.animateScrollToItem(idx)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("reader_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "واپس",
                        tint = GoldPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "سُورَةُ آلِ عِمْرَان",
                        fontFamily = QuranArabicFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    Text(
                        text = "آیات ۱ تا ۹۲ • پارہ ۳",
                        fontFamily = UrduFontFamily,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(
                        onClick = { showFontDialog = true },
                        modifier = Modifier.testTag("font_size_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "فونٹ سائز",
                            tint = GoldPrimary
                        )
                    }

                    IconButton(
                        onClick = { showJumpDialog = true },
                        modifier = Modifier.testTag("jump_verse_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "آیت پر جائیں",
                            tint = GoldPrimary
                        )
                    }
                }
            }
        }

        // Verse List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                BismillahHeader()
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(verses, key = { it.verseNumber }) { verse ->
                val isBookmarked = bookmarks.any { it.verseNumber == verse.verseNumber }
                val isCompleted = completedVerses.any { it.verseNumber == verse.verseNumber }
                val isCurrentPlaying = playingVerseNumber == verse.verseNumber

                VerseCard(
                    verse = verse,
                    isBookmarked = isBookmarked,
                    isCompleted = isCompleted,
                    isPlaying = isCurrentPlaying,
                    arabicFontSize = currentArabicSize.sp,
                    urduFontSize = currentUrduSize.sp,
                    onWordClick = { word -> onWordClick(verse, word) },
                    onPlayAudio = { onPlayVerse(verse.verseNumber) },
                    onToggleBookmark = { onToggleBookmark(verse.verseNumber) },
                    onToggleCompleted = { onToggleCompleted(verse.verseNumber) },
                    onShowTafseer = { onShowTafseer(verse) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // Jump to Verse Dialog
    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = {
                Text(
                    text = "آیت کا انتخاب کریں (۱ تا ۹۲)",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GoldLight
                )
            },
            text = {
                Column {
                    Text(
                        text = "جس آیت پر جانا چاہتے ہیں اس کا نمبر درج کریں:",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = jumpTargetInput,
                        onValueChange = { jumpTargetInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("آیت نمبر") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("jump_verse_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = jumpTargetInput.toIntOrNull()
                        if (target != null && target in 1..92) {
                            coroutineScope.launch {
                                listState.animateScrollToItem(target - 1)
                            }
                            showJumpDialog = false
                            jumpTargetInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("جائیں", fontFamily = UrduFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("منسوخ", fontFamily = UrduFontFamily)
                }
            }
        )
    }

    // Font Size Adjuster Dialog
    if (showFontDialog) {
        AlertDialog(
            onDismissRequest = { showFontDialog = false },
            title = {
                Text(
                    text = "فونٹ سائز ایڈجسٹ کریں",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GoldLight
                )
            },
            text = {
                Column {
                    Text(
                        text = "عربی رسم الخط سائز: ${currentArabicSize.toInt()}sp",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = currentArabicSize,
                        onValueChange = { currentArabicSize = it },
                        valueRange = 20f..40f
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "اردو ترجمہ سائز: ${currentUrduSize.toInt()}sp",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = currentUrduSize,
                        onValueChange = { currentUrduSize = it },
                        valueRange = 13f..24f
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showFontDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("ٹھیک ہے", fontFamily = UrduFontFamily)
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VerseCard(
    verse: Verse,
    isBookmarked: Boolean,
    isCompleted: Boolean,
    isPlaying: Boolean,
    arabicFontSize: androidx.compose.ui.unit.TextUnit,
    urduFontSize: androidx.compose.ui.unit.TextUnit,
    onWordClick: (QuranWord) -> Unit,
    onPlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleCompleted: () -> Unit,
    onShowTafseer: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("verse_card_${verse.verseNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isPlaying) 1.8.dp else 1.dp,
            if (isPlaying) GoldPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Verse Top Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OrnateVerseNumberBadge(verseNumber = verse.verseNumber)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Completed status toggle
                    IconButton(
                        onClick = onToggleCompleted,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = if (isCompleted) "مکمل شدہ آیت" else "مکمل کریں",
                            tint = if (isCompleted) IslamicGreenAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Bookmark toggle
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isBookmarked) "بک مارک ختم کریں" else "بک مارک کریں",
                            tint = if (isBookmarked) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Tafseer / Explanation Note
                    IconButton(
                        onClick = onShowTafseer,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "وضاحت و تفسیر",
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Play Audio
                    IconButton(
                        onClick = onPlayAudio,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "آیت کی تلاوت سنیں",
                            tint = if (isPlaying) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Word-by-Word FlowRow
            // RTL text layout for Arabic words
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    verse.words.forEach { word ->
                        WordChip(
                            word = word,
                            fontSize = arabicFontSize,
                            onClick = { onWordClick(word) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Urdu Translation
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = verse.urduTranslation,
                    fontFamily = UrduFontFamily,
                    fontSize = urduFontSize,
                    lineHeight = 26.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun WordChip(
    word: QuranWord,
    fontSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag("word_${word.wordId}"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        border = androidx.compose.foundation.BorderStroke(0.6.dp, GoldPrimary.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = word.arabic,
                fontFamily = QuranArabicFontFamily,
                fontSize = fontSize,
                color = GoldLight,
                textAlign = TextAlign.Center
            )
            Text(
                text = word.urdu,
                fontFamily = UrduFontFamily,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
