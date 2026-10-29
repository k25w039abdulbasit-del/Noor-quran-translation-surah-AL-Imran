package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.PlaybackState
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.UrduFontFamily
import java.util.Locale

@Composable
fun FloatingAudioBar(
    playbackState: PlaybackState,
    currentPosition: Int,
    duration: Int,
    isRepeatEnabled: Boolean,
    onPlayPause: () -> Unit,
    onNextVerse: () -> Unit,
    onPrevVerse: () -> Unit,
    onToggleRepeat: () -> Unit,
    onSeek: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeVerseNumber = when (playbackState) {
        is PlaybackState.Playing -> playbackState.verseNumber
        is PlaybackState.Paused -> playbackState.verseNumber
        else -> 1
    }

    val isPlaying = playbackState is PlaybackState.Playing
    val isBuffering = playbackState is PlaybackState.Buffering

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary),
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Top Row: Reciter attribution & Verse number & Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "آیت نمبر $activeVerseNumber • سورۃ آل عمران",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = GoldLight
                    )
                    Text(
                        text = "قاری عبد الباسط عبد الصمد (مرتل)",
                        fontFamily = UrduFontFamily,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleRepeat,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("audio_repeat_button")
                    ) {
                        Icon(
                            imageVector = if (isRepeatEnabled) Icons.Default.RepeatOne else Icons.Default.Repeat,
                            contentDescription = "آیت کی تکرار",
                            tint = if (isRepeatEnabled) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("close_audio_bar")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بند کریں",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Seekbar
            val sliderValue = if (duration > 0) (currentPosition.toFloat() / duration).coerceIn(0f, 1f) else 0f
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatMs(currentPosition),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = sliderValue,
                    onValueChange = { frac ->
                        if (duration > 0) {
                            onSeek((frac * duration).toInt())
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .height(28.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = GoldPrimary,
                        activeTrackColor = GoldPrimary,
                        inactiveTrackColor = GoldDark.copy(alpha = 0.3f)
                    )
                )
                Text(
                    text = formatMs(duration),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPrevVerse,
                    enabled = activeVerseNumber > 1,
                    modifier = Modifier.testTag("prev_verse_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "پچھلی آیت",
                        tint = if (activeVerseNumber > 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                        .clickable { onPlayPause() }
                        .testTag("play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isBuffering) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "روکیں" else "چلائیں",
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = onNextVerse,
                    enabled = activeVerseNumber < 92,
                    modifier = Modifier.testTag("next_verse_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "اگلی آیت",
                        tint = if (activeVerseNumber < 92) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

private fun formatMs(ms: Int): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSec / 60
    val seconds = totalSec % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
