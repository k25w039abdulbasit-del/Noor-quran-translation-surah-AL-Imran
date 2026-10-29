package com.example.data.model

enum class WordCategory(val urduLabel: String, val englishLabel: String) {
    ISM("اسم", "Noun"),
    FI_L("فعل", "Verb"),
    HARF("حرف", "Particle");

    companion object {
        fun fromString(value: String): WordCategory {
            return when (value.uppercase()) {
                "FI_L", "FIL", "VERB" -> FI_L
                "HARF", "PARTICLE" -> HARF
                else -> ISM
            }
        }
    }
}

data class QuranWord(
    val wordId: String,
    val position: Int,
    val arabic: String,
    val urdu: String,
    val transliteration: String,
    val root: String,
    val category: WordCategory,
    val grammaticalRole: String,
    val urduExplanation: String,
    val audioUrl: String,
    val isVerified: Boolean = true
)

data class Verse(
    val verseNumber: Int,
    val surahNumber: Int = 3,
    val surahNameArabic: String = "آل عمران",
    val surahNameUrdu: String = "آل عمران",
    val arabicText: String,
    val urduTranslation: String,
    val tafseerNote: String,
    val audioUrl: String,
    val reciter: String = "شیخ عبد الباسط عبد الصمد",
    val paraNumber: Int = 3,
    val words: List<QuranWord>,
    val isVerified: Boolean = true
)

data class GrammarExample(
    val arabicText: String,
    val urduTranslation: String,
    val verseRef: String,
    val analysisBreakdown: String
)

data class GrammarLesson(
    val id: Int,
    val titleUrdu: String,
    val titleArabic: String,
    val summaryUrdu: String,
    val explanationUrdu: String,
    val rules: List<String>,
    val examples: List<GrammarExample>,
    val categoryTag: String
)

enum class QuizType(val urduLabel: String) {
    VOCABULARY("الفاظ و معانی کوئز"),
    GRAMMAR("قواعد و نحو کوئز")
}

data class QuizQuestion(
    val id: String,
    val type: QuizType,
    val questionUrdu: String,
    val promptSnippet: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationUrdu: String,
    val verseNumber: Int
)
