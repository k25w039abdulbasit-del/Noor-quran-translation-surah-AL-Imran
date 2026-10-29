package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.QuranArabicFontFamily
import com.example.ui.theme.UrduFontFamily

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    arabicFontSize: TextUnit,
    urduFontSize: TextUnit,
    onToggleTheme: () -> Unit,
    onArabicFontSizeChanged: (TextUnit) -> Unit,
    onUrduFontSizeChanged: (TextUnit) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

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
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "واپس",
                        tint = GoldPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ترتیبات و معلومات (Settings)",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = GoldLight
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Theme Setting
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "تھیم (Emerald Theme)",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isDarkTheme) "زمردی گہرا موڈ (Emerald Dark)" else "روشن کاغذی موڈ (Parchment Light)",
                                fontFamily = UrduFontFamily,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GoldPrimary,
                            checkedTrackColor = GoldDark.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("theme_switch")
                    )
                }
            }

            // Typography Setting Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "قرآنی و اردو خطاطی کا حجم",
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GoldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "عربی رسم الخط سائز: ${arabicFontSize.value.toInt()}sp",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = arabicFontSize.value,
                        onValueChange = { onArabicFontSizeChanged(it.sp) },
                        valueRange = 22f..40f,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldPrimary,
                            activeTrackColor = GoldPrimary
                        )
                    )

                    // Preview Arabic Text
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "اللّٰهُ لَاۤ اِلٰهَ اِلَّا هُوَ الۡحَىُّ الۡقَيُّوۡمُ",
                            fontFamily = QuranArabicFontFamily,
                            fontSize = arabicFontSize,
                            color = GoldLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "اردو ترجمہ سائز: ${urduFontSize.value.toInt()}sp",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = urduFontSize.value,
                        onValueChange = { onUrduFontSizeChanged(it.sp) },
                        valueRange = 13f..24f,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldPrimary,
                            activeTrackColor = GoldPrimary
                        )
                    )

                    // Preview Urdu Text
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "اللہ، اس کے سوا کوئی عبادت کے لائق نہیں، وہ ہمیشہ زندہ اور قائم رکھنے والا ہے۔",
                            fontFamily = UrduFontFamily,
                            fontSize = urduFontSize,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }

            // Audio Reciter Attribution
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "قرآنی قاری و آڈیو سروس",
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GoldLight
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "قاری: شیخ عبد الباسط عبد الصمد (مرتل)",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "صوتی ڈیٹا EveryAyah اور Quran.com کے مستند و مصدقہ ذخیرے سے حاصل کیا گیا ہے۔ تلاوت میں تسلسل، تکرار (Repeat)، اور آیات کے درمیان نیویگیشن کی مکمل سہولت میسر ہے۔",
                        fontFamily = UrduFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Content Authenticity and Verification
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGreenAccent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = IslamicGreenAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "صحتِ متن و مصادر (Content Verification)",
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = IslamicGreenAccent
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• قرآنی رسم الخط: مصدقہ انڈو پاک رسم الخط مع مکمل اعراب و رموز اوقاف (Surah 3: Verses 1–92 • Para 3)۔\n• اردو ترجمہ: مولانا فتح محمد جالندھری (معتبر و مستند اردو ترجمہ)۔\n• صرفی و نحوی تجزیہ: مستند عربی لغات، قرآنی معربات اور قواعدِ نحو و صرف کے مسلمہ اصولوں کے مطابق مدون کیا گیا ہے۔",
                        fontFamily = UrduFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // App Identity & Version Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "نُــــور — Quran Arabic Learning",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = GoldLight
                    )
                    Text(
                        text = "Version 1.0 • Designed for Urdu Speakers",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
