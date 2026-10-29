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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuranWord
import com.example.data.model.Verse
import com.example.data.repository.QuranRepository
import com.example.ui.components.OrnateVerseNumberBadge
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.QuranArabicFontFamily
import com.example.ui.theme.UrduFontFamily

@Composable
fun SearchScreen(
    searchQuery: String,
    searchResults: QuranRepository.SearchResults,
    onQueryChanged: (String) -> Unit,
    onSelectVerse: (Int) -> Unit,
    onSelectWord: (Verse, QuranWord) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedFilter by remember { mutableStateOf("سب") }
    val filters = listOf("سب", "آیات (${searchResults.matchedVerses.size})", "الفاظ (${searchResults.matchedWords.size})")

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // App Bar & Search Input
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("search_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "واپس",
                            tint = GoldPrimary
                        )
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onQueryChanged,
                        placeholder = {
                            Text(
                                text = "عربی لفظ، اردو معنی، یا روٹ تلاش کریں...",
                                fontFamily = UrduFontFamily,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = GoldPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onQueryChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "صاف کریں"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_input_field")
                    )
                }

                // Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { f ->
                        val isSelected = (f == selectedFilter) || (selectedFilter == "سب" && f == "سب") || (selectedFilter.startsWith("آیات") && f.startsWith("آیات")) || (selectedFilter.startsWith("الفاظ") && f.startsWith("الفاظ"))
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = f },
                            label = { Text(f, fontFamily = UrduFontFamily, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }
        }

        // Search Results List
        if (searchQuery.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = GoldPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "قرآنی الفاظ، اردو تراجم اور مادہ تلاش کریں",
                        fontFamily = UrduFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "مثالیں: الله، نزل، کتاب، زندہ، رحمت، ك ت ب، یا آیت نمبر (۱ تا ۹۲)",
                        fontFamily = UrduFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (searchResults.matchedVerses.isEmpty() && searchResults.matchedWords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "کوئی نتیجہ نہیں ملا۔ برائے مہربانی دیگر الفاظ یا روٹ سے تلاش کیجیے۔",
                    fontFamily = UrduFontFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Show matched verses
                if (selectedFilter.startsWith("سب") || selectedFilter.startsWith("آیات")) {
                    if (searchResults.matchedVerses.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "مطابقت رکھنے والی آیات (${searchResults.matchedVerses.size})",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = GoldLight
                            )
                        }

                        items(searchResults.matchedVerses, key = { "v_${it.verseNumber}" }) { verse ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectVerse(verse.verseNumber) }
                                    .testTag("search_verse_${verse.verseNumber}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, GoldPrimary.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OrnateVerseNumberBadge(verseNumber = verse.verseNumber)
                                        Text(
                                            text = "مطالعہ کیلئے ٹیپ کریں",
                                            fontFamily = UrduFontFamily,
                                            fontSize = 11.sp,
                                            color = GoldPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
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

                // Show matched words
                if (selectedFilter.startsWith("سب") || selectedFilter.startsWith("الفاظ")) {
                    if (searchResults.matchedWords.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "مطابقت رکھنے والے کلمات (${searchResults.matchedWords.size})",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = GoldLight
                            )
                        }

                        items(searchResults.matchedWords, key = { "w_${it.second.wordId}" }) { (verse, word) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectWord(verse, word) }
                                    .testTag("search_word_${word.wordId}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = word.arabic,
                                            fontFamily = QuranArabicFontFamily,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldLight
                                        )
                                        Text(
                                            text = "معنی: ${word.urdu} • روٹ: ${word.root}",
                                            fontFamily = UrduFontFamily,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        color = GoldPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "آیت ${verse.verseNumber}",
                                            fontFamily = UrduFontFamily,
                                            fontSize = 11.sp,
                                            color = GoldLight,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
