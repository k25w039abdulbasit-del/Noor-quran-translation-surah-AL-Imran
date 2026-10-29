package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.QuizType
import com.example.ui.Screen
import com.example.ui.components.BismillahHeader
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.QuranArabicFontFamily
import com.example.ui.theme.UrduFontFamily

@Composable
fun HomeScreen(
    completedCount: Int,
    bookmarksCount: Int,
    savedWordsCount: Int,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Bar & Islamic Greeting
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, GoldPrimary, CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.noor_launcher_foreground_1792993643296),
                            contentDescription = "نور لوگو",
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "السلام علیکم",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = GoldLight
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "قرآن کو لفظ بہ لفظ سیکھیں",
                            fontFamily = UrduFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { onNavigate(Screen.Settings) },
                    modifier = Modifier.testTag("home_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "ترتیبات",
                        tint = GoldPrimary
                    )
                }
            }
        }

        // Bismillah Header
        item {
            BismillahHeader()
        }

        // Surah Aal-e-Imran Progress Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(Screen.Reader(1)) }
                    .testTag("surah_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Surface(
                                color = GoldPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, GoldPrimary)
                            ) {
                                Text(
                                    text = "پارہ ۳ • مدنی سورۃ",
                                    fontFamily = UrduFontFamily,
                                    fontSize = 11.sp,
                                    color = GoldLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "سُورَةُ آلِ عِمْرَان",
                                fontFamily = QuranArabicFontFamily,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                            Text(
                                text = "آیات ۱ تا ۹۲ (مفصل لفظی و نحوی تجزیہ)",
                                fontFamily = UrduFontFamily,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Circular Progress Indicator
                        val progress = if (completedCount > 0) completedCount / 92f else 0f
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(64.dp),
                                color = GoldPrimary,
                                trackColor = GoldDark.copy(alpha = 0.25f),
                                strokeWidth = 5.dp
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    fontFamily = UrduFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = GoldLight
                                )
                                Text(
                                    text = "$completedCount/92",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onNavigate(Screen.Reader(1)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("continue_learning_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "مطالعہ شروع کریں • Continue Learning",
                            fontFamily = UrduFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Quick Access Modules Title
        item {
            Text(
                text = "تعلیمی شعبہ جات (Learning Modules)",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GoldLight
            )
        }

        // 2x2 Grid of Modules
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleCard(
                        title = "لفظ بہ لفظ مطالعہ",
                        subtitle = "۹۲ آیات کا لغوی ترجمہ",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        accentColor = GoldPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Reader(1)) }
                    )
                    ModuleCard(
                        title = "قواعد و نحو سنٹر",
                        subtitle = "۱۲ تفصیلی اسباق مع امثلہ",
                        icon = Icons.Default.Translate,
                        accentColor = IslamicGreenAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.GrammarCenter) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleCard(
                        title = "الفاظ و مادہ تلاش",
                        subtitle = "عربی، اردو، اور روٹ سرچ",
                        icon = Icons.Default.Search,
                        accentColor = Color(0xFF48CAE4),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Search) }
                    )
                    ModuleCard(
                        title = "محفوظ شدہ کلمات",
                        subtitle = "$bookmarksCount آیات • $savedWordsCount کلمات",
                        icon = Icons.Default.Bookmark,
                        accentColor = Color(0xFFE9C46A),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Bookmarks) }
                    )
                }
            }
        }

        // Revision Quizzes Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(Screen.Quiz(QuizType.VOCABULARY)) }
                    .testTag("quiz_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGreenAccent.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(IslamicGreenAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = IslamicGreenAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "دہرائی و امتحانی کوئز (Quizzes)",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "الفاظ کے معانی اور نحوی قواعد کا ٹیسٹ دیں",
                                fontFamily = UrduFontFamily,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = IslamicGreenAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Daily Wisdom from Surah Aal-e-Imran
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "آج کی قرآنی دعا (آیت ۸)",
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = GoldLight
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "رَبَّنَا لَا تُزِغۡ قُلُوۡبَنَا بَعۡدَ اِذۡ هَدَيۡتَنَا وَهَبۡ لَنَا مِنۡ لَّدُنۡكَ رَحۡمَةً‌ ۚ اِنَّكَ اَنۡتَ الۡوَهَّابُ",
                        fontFamily = QuranArabicFontFamily,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اے ہمارے رب! ہدایت دینے کے بعد ہمارے دلوں کو ٹیڑھا نہ فرما اور ہمیں اپنے پاس سے رحمت عطا کر، بیشک تو ہی بہت عطا فرمانے والا ہے۔",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("module_${title.hashCode()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontFamily = UrduFontFamily,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
