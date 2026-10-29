package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuranWord
import com.example.data.model.Verse
import com.example.data.model.WordCategory
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.QuranArabicFontFamily
import com.example.ui.theme.UrduFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordAnalysisBottomSheet(
    verse: Verse,
    word: QuranWord,
    isSaved: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onPlayWordAudio: (String) -> Unit,
    onToggleSave: () -> Unit,
    onSearchRoot: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(CircleShape),
                color = GoldPrimary.copy(alpha = 0.5f)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Close and Save Word Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_word_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بند کریں",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category Badge
                    val catBadgeColor = when (word.category) {
                        WordCategory.ISM -> GoldPrimary
                        WordCategory.FI_L -> IslamicGreenAccent
                        WordCategory.HARF -> Color(0xFF48CAE4)
                    }
                    Surface(
                        color = catBadgeColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, catBadgeColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = word.category.urduLabel,
                            fontFamily = UrduFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = catBadgeColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier.testTag("save_word_button")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isSaved) "محفوظ شدہ لفظ" else "لفظ محفوظ کریں",
                            tint = if (isSaved) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Word Hero Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = word.arabic,
                        fontFamily = QuranArabicFontFamily,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    if (word.transliteration.isNotBlank()) {
                        Text(
                            text = "[ ${word.transliteration} ]",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Contextual Meaning in Urdu
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "سیاقی معنی: ${word.urdu}",
                            fontFamily = UrduFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    if (word.audioUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { onPlayWordAudio(word.audioUrl) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = GoldPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                            modifier = Modifier.testTag("play_word_audio_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "تلفظ سنیں",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تلفظ سنیں",
                                fontFamily = UrduFontFamily,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Linguistic Analysis Details Grid
            Text(
                text = "صرفی و نحوی تجزیہ (Grammatical Analysis)",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Detail Attributes
            DetailRow(label = "کلمہ کی نوعیت", value = "${word.category.urduLabel} (${word.category.englishLabel})")
            DetailRow(label = "نحوی کردار (Role in Verse)", value = word.grammaticalRole)

            if (word.root.isNotBlank() && word.root != "-") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "حروفِ اصلیہ (جذر / Root):",
                            fontFamily = UrduFontFamily,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = word.root,
                                fontFamily = QuranArabicFontFamily,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onSearchRoot(word.root) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "اس مادہ کے تمام الفاظ تلاش کریں",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Simple Urdu Grammatical Commentary
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "نحوی وضاحت و رہنمائی",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = GoldLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = word.urduExplanation.ifBlank { "اس کلمہ کا بنیادی نحوی کردار اور مفہوم متن کے سیاق و سباق کے عین مطابق ہے۔" },
                        fontFamily = UrduFontFamily,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Verse Context
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "سیاقِ آیت (مکمل آیت ${verse.verseNumber})",
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = IslamicGreenAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "مصدق نحوی متن",
                                fontFamily = UrduFontFamily,
                                fontSize = 11.sp,
                                color = IslamicGreenAccent
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = verse.arabicText,
                        fontFamily = QuranArabicFontFamily,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = verse.urduTranslation,
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save for revision toggle button
            Button(
                onClick = onToggleSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("toggle_save_revision_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSaved) MaterialTheme.colorScheme.surfaceVariant else GoldPrimary,
                    contentColor = if (isSaved) GoldLight else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSaved) "مشکل الفاظ کی فہرست سے نکالیں" else "مشکل الفاظ میں شامل کریں (دہرائی کیلئے)",
                    fontFamily = UrduFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label:",
                fontFamily = UrduFontFamily,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
