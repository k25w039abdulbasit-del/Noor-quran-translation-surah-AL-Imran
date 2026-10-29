package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.local.SavedWordEntity
import com.example.data.model.QuranWord
import com.example.data.model.Verse
import com.example.ui.components.OrnateVerseNumberBadge
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.QuranArabicFontFamily
import com.example.ui.theme.UrduFontFamily

@Composable
fun BookmarksScreen(
    bookmarks: List<BookmarkEntity>,
    savedWords: List<SavedWordEntity>,
    verses: List<Verse>,
    onSelectVerse: (Int) -> Unit,
    onSelectWord: (Verse, QuranWord) -> Unit,
    onRemoveBookmark: (Int) -> Unit,
    onRemoveSavedWord: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("محفوظ آیات (${bookmarks.size})", "مشکل الفاظ (${savedWords.size})")

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // App Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("bookmarks_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "واپس",
                        tint = GoldPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "محفوظ شدہ مواد و دہرائی (Bookmarks)",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = GoldLight
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = GoldPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = GoldPrimary
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontFamily = UrduFontFamily,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Tab Content
        if (selectedTabIndex == 0) {
            // Bookmarked Verses Tab
            if (bookmarks.isEmpty()) {
                EmptyStateView(
                    message = "آپ نے ابھی تک کوئی آیت بک مارک نہیں کی۔ مطالعہ کے دوران بک مارک آئیکون پر ٹیپ کر کے آیت کو محفوظ کریں۔"
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(bookmarks, key = { it.verseNumber }) { b ->
                        val verse = verses.find { it.verseNumber == b.verseNumber }
                        if (verse != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectVerse(verse.verseNumber) }
                                    .testTag("bookmark_item_${verse.verseNumber}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OrnateVerseNumberBadge(verseNumber = verse.verseNumber)
                                        IconButton(
                                            onClick = { onRemoveBookmark(verse.verseNumber) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "بک مارک ختم کریں",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = verse.arabicText,
                                        fontFamily = QuranArabicFontFamily,
                                        fontSize = 18.sp,
                                        color = GoldLight,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = verse.urduTranslation,
                                        fontFamily = UrduFontFamily,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Saved Difficult Words Tab
            if (savedWords.isEmpty()) {
                EmptyStateView(
                    message = "آپ نے ابھی تک کوئی مشکل لفظ محفوظ نہیں کیا۔ کسی بھی لفظ پر ٹیپ کر کے 'مشکل الفاظ میں شامل کریں' کا بٹن دبائیں۔"
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(savedWords, key = { it.wordId }) { w ->
                        val verse = verses.find { it.verseNumber == w.verseNumber }
                        val fullWord = verse?.words?.find { it.wordId == w.wordId }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (verse != null && fullWord != null) {
                                        onSelectWord(verse, fullWord)
                                    }
                                }
                                .testTag("saved_word_${w.wordId}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, GoldPrimary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = w.arabic,
                                        fontFamily = QuranArabicFontFamily,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                    Text(
                                        text = "معنی: ${w.urdu} • روٹ: ${w.root}",
                                        fontFamily = UrduFontFamily,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "آیت نمبر ${w.verseNumber}",
                                        fontFamily = UrduFontFamily,
                                        fontSize = 11.sp,
                                        color = GoldPrimary
                                    )
                                }

                                IconButton(
                                    onClick = { onRemoveSavedWord(w.wordId) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "فہرست سے ہٹائیں",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Bookmark,
                contentDescription = null,
                tint = GoldPrimary.copy(alpha = 0.4f),
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = message,
                fontFamily = UrduFontFamily,
                fontSize = 13.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
