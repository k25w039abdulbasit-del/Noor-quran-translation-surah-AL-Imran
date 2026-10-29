package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.QuizType
import com.example.ui.QuizState
import com.example.ui.theme.DeepRubyAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.QuranArabicFontFamily
import com.example.ui.theme.UrduFontFamily

@Composable
fun QuizScreen(
    quizType: QuizType,
    quizState: QuizState,
    onSelectOption: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onRestartQuiz: () -> Unit,
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
                    modifier = Modifier.testTag("quiz_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "واپس",
                        tint = GoldPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = quizType.urduLabel,
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = GoldLight
                    )
                    Text(
                        text = "سورۃ آل عمران • دہرائی و جائزہ",
                        fontFamily = UrduFontFamily,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (quizState.isFinished) {
            // Quiz Completed Result Card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val percentage = if (quizState.questions.isNotEmpty()) {
                            (quizState.score.toFloat() / quizState.questions.size * 100).toInt()
                        } else 0

                        val praiseUrdu = when {
                            percentage >= 80 -> "ماشاء اللہ! شاندار کارکردگی"
                            percentage >= 50 -> "بہت خوب! مطالعہ جاری رکھیں"
                            else -> "محنت جاری رکھیں، اللہ علم میں برکت دے"
                        }

                        Text(
                            text = praiseUrdu,
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = GoldLight,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "آپ کا اسکور: ${quizState.score} از ${quizState.questions.size} ($percentage%)",
                            fontFamily = UrduFontFamily,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onRestartQuiz,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "دوبارہ کوئز دیں • Try Again",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "مرکزی صفحہ پر واپس جائیں",
                                fontFamily = UrduFontFamily,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        } else if (quizState.questions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "کوئز تیار کیا جا رہا ہے...",
                    fontFamily = UrduFontFamily,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val currentQ = quizState.questions[quizState.currentIndex]
            val progress = (quizState.currentIndex + 1).toFloat() / quizState.questions.size

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar & Counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "سوال ${quizState.currentIndex + 1} از ${quizState.questions.size}",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = GoldLight
                    )
                    Text(
                        text = "اسکور: ${quizState.score}",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        color = IslamicGreenAccent
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = GoldPrimary,
                    trackColor = GoldDark.copy(alpha = 0.25f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Question Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = GoldPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "آیت نمبر ${currentQ.verseNumber}",
                                fontFamily = UrduFontFamily,
                                fontSize = 11.sp,
                                color = GoldLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentQ.promptSnippet,
                            fontFamily = QuranArabicFontFamily,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentQ.questionUrdu,
                            fontFamily = UrduFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options
                currentQ.options.forEachIndexed { index, optionText ->
                    val isSelected = quizState.selectedOptionIndex == index
                    val isCorrect = index == currentQ.correctIndex

                    val borderCol = when {
                        quizState.isSubmitted && isCorrect -> IslamicGreenAccent
                        quizState.isSubmitted && isSelected && !isCorrect -> DeepRubyAccent
                        isSelected -> GoldPrimary
                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    }

                    val bgCol = when {
                        quizState.isSubmitted && isCorrect -> IslamicGreenAccent.copy(alpha = 0.15f)
                        quizState.isSubmitted && isSelected && !isCorrect -> DeepRubyAccent.copy(alpha = 0.15f)
                        isSelected -> GoldPrimary.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(enabled = !quizState.isSubmitted) {
                                onSelectOption(index)
                            }
                            .testTag("quiz_option_$index"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = bgCol),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected || (quizState.isSubmitted && isCorrect)) 1.5.dp else 1.dp,
                            borderCol
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = optionText,
                                fontFamily = UrduFontFamily,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            if (quizState.isSubmitted) {
                                if (isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "درست",
                                        tint = IslamicGreenAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "غلط",
                                        tint = DeepRubyAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Explanation on submission
                if (quizState.isSubmitted) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "علمی رہنمائی و وضاحت:",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GoldLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentQ.explanationUrdu,
                                fontFamily = UrduFontFamily,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Action Button
                if (!quizState.isSubmitted) {
                    Button(
                        onClick = onSubmitAnswer,
                        enabled = quizState.selectedOptionIndex != null,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_quiz_answer_btn")
                    ) {
                        Text(
                            text = "جواب جمع کرائیں • Submit",
                            fontFamily = UrduFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                } else {
                    Button(
                        onClick = onNextQuestion,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("next_quiz_question_btn")
                    ) {
                        Text(
                            text = if (quizState.currentIndex + 1 < quizState.questions.size) "اگلا سوال • Next" else "نتیجہ دیکھیں • View Results",
                            fontFamily = UrduFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
