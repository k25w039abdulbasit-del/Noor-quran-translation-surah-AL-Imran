package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class PlaybackState {
    object Idle : PlaybackState()
    object Buffering : PlaybackState()
    data class Playing(val verseNumber: Int) : PlaybackState()
    data class Paused(val verseNumber: Int) : PlaybackState()
    data class Error(val message: String) : PlaybackState()
}

class QuranAudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var wordPlayer: MediaPlayer? = null

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration.asStateFlow()

    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled: StateFlow<Boolean> = _isRepeatEnabled.asStateFlow()

    private var currentVerseNum: Int = 1
    private val handler = Handler(Looper.getMainLooper())
    private var onAutoAdvanceToNextVerse: ((Int) -> Unit)? = null

    private val progressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    _currentPosition.value = player.currentPosition
                    _duration.value = player.duration
                    handler.postDelayed(this, 300)
                }
            }
        }
    }

    fun setOnAutoAdvanceListener(listener: (Int) -> Unit) {
        onAutoAdvanceToNextVerse = listener
    }

    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
    }

    fun playVerse(verseNumber: Int) {
        currentVerseNum = verseNumber
        releasePlayer()

        val audioUrl = String.format(
            Locale.US,
            "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps/003%03d.mp3",
            verseNumber
        )

        _playbackState.value = PlaybackState.Buffering
        _currentPosition.value = 0

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    mp.start()
                    _playbackState.value = PlaybackState.Playing(verseNumber)
                    _duration.value = mp.duration
                    handler.post(progressRunnable)
                }
                setOnCompletionListener {
                    handler.removeCallbacks(progressRunnable)
                    if (_isRepeatEnabled.value) {
                        playVerse(verseNumber)
                    } else if (verseNumber < 92) {
                        onAutoAdvanceToNextVerse?.invoke(verseNumber + 1)
                        playVerse(verseNumber + 1)
                    } else {
                        _playbackState.value = PlaybackState.Idle
                    }
                }
                setOnErrorListener { _, what, extra ->
                    handler.removeCallbacks(progressRunnable)
                    _playbackState.value = PlaybackState.Error("آڈیو لوڈ کرنے میں دشواری پیش آئی۔ برائے مہربانی انٹرنیٹ کنکشن چیک کریں۔")
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            _playbackState.value = PlaybackState.Error("آڈیو شروع نہیں ہو سکی: ${e.localizedMessage}")
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                handler.removeCallbacks(progressRunnable)
                _playbackState.value = PlaybackState.Paused(currentVerseNum)
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            it.start()
            _playbackState.value = PlaybackState.Playing(currentVerseNum)
            handler.post(progressRunnable)
        } ?: run {
            playVerse(currentVerseNum)
        }
    }

    fun togglePlayPause(verseNumber: Int) {
        when (val state = _playbackState.value) {
            is PlaybackState.Playing -> {
                if (state.verseNumber == verseNumber) {
                    pause()
                } else {
                    playVerse(verseNumber)
                }
            }
            is PlaybackState.Paused -> {
                if (state.verseNumber == verseNumber) {
                    resume()
                } else {
                    playVerse(verseNumber)
                }
            }
            else -> playVerse(verseNumber)
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let {
            it.seekTo(positionMs)
            _currentPosition.value = positionMs
        }
    }

    fun playWordAudio(wordAudioUrl: String) {
        if (wordAudioUrl.isBlank()) return
        try {
            wordPlayer?.release()
            wordPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(wordAudioUrl)
                setOnPreparedListener { it.start() }
                setOnCompletionListener { it.release() }
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        releasePlayer()
        _playbackState.value = PlaybackState.Idle
        _currentPosition.value = 0
    }

    private fun releasePlayer() {
        handler.removeCallbacks(progressRunnable)
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
                it.reset()
                it.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer = null
        }
    }

    fun release() {
        releasePlayer()
        wordPlayer?.release()
        wordPlayer = null
    }
}
