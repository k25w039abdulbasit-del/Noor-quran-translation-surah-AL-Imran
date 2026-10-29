package com.example.data.repository

import android.content.Context
import com.example.data.GrammarLessonsData
import com.example.data.model.GrammarLesson
import com.example.data.model.QuranWord
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizType
import com.example.data.model.Verse
import com.example.data.model.WordCategory
import org.json.JSONObject
import java.util.Locale

class QuranRepository(private val context: Context) {

    private var cachedVerses: List<Verse>? = null

    @Synchronized
    fun getAllVerses(): List<Verse> {
        cachedVerses?.let { return it }

        val versesList = mutableListOf<Verse>()
        try {
            val jsonString = context.assets.open("quran/surah_3_verses.json").bufferedReader().use { it.readText() }
            val rootObj = JSONObject(jsonString)
            val versesArray = rootObj.getJSONArray("verses")

            for (i in 0 until versesArray.length()) {
                val vObj = versesArray.getJSONObject(i)
                val wordsArray = vObj.getJSONArray("words")
                val wordsList = mutableListOf<QuranWord>()

                for (j in 0 until wordsArray.length()) {
                    val wObj = wordsArray.getJSONObject(j)
                    wordsList.add(
                        QuranWord(
                            wordId = wObj.getString("wordId"),
                            position = wObj.getInt("position"),
                            arabic = wObj.getString("arabic"),
                            urdu = wObj.getString("urdu"),
                            transliteration = wObj.optString("transliteration", ""),
                            root = wObj.optString("root", "-"),
                            category = WordCategory.fromString(wObj.optString("category", "ISM")),
                            grammaticalRole = wObj.optString("grammaticalRole", "اسم"),
                            urduExplanation = wObj.optString("urduExplanation", ""),
                            audioUrl = wObj.optString("audioUrl", ""),
                            isVerified = wObj.optBoolean("isVerified", true)
                        )
                    )
                }

                versesList.add(
                    Verse(
                        verseNumber = vObj.getInt("verseNumber"),
                        surahNumber = vObj.optInt("surahNumber", 3),
                        surahNameArabic = vObj.optString("surahNameArabic", "آل عمران"),
                        surahNameUrdu = vObj.optString("surahNameUrdu", "آل عمران"),
                        arabicText = vObj.getString("arabicText"),
                        urduTranslation = vObj.getString("urduTranslation"),
                        tafseerNote = vObj.optString("tafseerNote", ""),
                        audioUrl = vObj.getString("audioUrl"),
                        reciter = vObj.optString("reciter", "شیخ عبد الباسط عبد الصمد"),
                        paraNumber = vObj.optInt("paraNumber", 3),
                        words = wordsList,
                        isVerified = vObj.optBoolean("isVerified", true)
                    )
                )
            }
            cachedVerses = versesList
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return cachedVerses ?: emptyList()
    }

    fun getVerseByNumber(verseNumber: Int): Verse? {
        return getAllVerses().find { it.verseNumber == verseNumber }
    }

    fun getWordById(wordId: String): QuranWord? {
        for (verse in getAllVerses()) {
            val word = verse.words.find { it.wordId == wordId }
            if (word != null) return word
        }
        return null
    }

    fun getWordsByRoot(root: String): List<Pair<Verse, QuranWord>> {
        val cleanRoot = root.replace(" ", "").trim()
        if (cleanRoot.isEmpty() || cleanRoot == "-") return emptyList()

        val results = mutableListOf<Pair<Verse, QuranWord>>()
        for (verse in getAllVerses()) {
            for (word in verse.words) {
                val wordRoot = word.root.replace(" ", "").trim()
                if (wordRoot.equals(cleanRoot, ignoreCase = true)) {
                    results.add(verse to word)
                }
            }
        }
        return results
    }

    // Normalizes Arabic by stripping diacritics/tashkeel
    fun normalizeArabic(text: String): String {
        return text.replace(Regex("[\u064B-\u0652\u0670\u06D6-\u06ED]"), "")
            .replace("أ", "ا")
            .replace("إ", "ا")
            .replace("آ", "ا")
            .replace("ة", "ه")
            .replace("ى", "ي")
            .trim()
    }

    data class SearchResults(
        val matchedVerses: List<Verse> = emptyList(),
        val matchedWords: List<Pair<Verse, QuranWord>> = emptyList()
    )

    fun search(query: String): SearchResults {
        val q = query.trim()
        if (q.isEmpty()) return SearchResults()

        val all = getAllVerses()
        val num = q.toIntOrNull()
        if (num != null && num in 1..92) {
            val v = all.find { it.verseNumber == num }
            return SearchResults(matchedVerses = listOfNotNull(v))
        }

        val normQ = normalizeArabic(q)
        val matchedVerses = mutableListOf<Verse>()
        val matchedWords = mutableListOf<Pair<Verse, QuranWord>>()

        for (verse in all) {
            val normArabic = normalizeArabic(verse.arabicText)
            val matchesVerseArabic = normArabic.contains(normQ, ignoreCase = true)
            val matchesVerseUrdu = verse.urduTranslation.contains(q, ignoreCase = true)

            if (matchesVerseArabic || matchesVerseUrdu) {
                matchedVerses.add(verse)
            }

            for (w in verse.words) {
                val normWordArabic = normalizeArabic(w.arabic)
                val matchesWordArabic = normWordArabic.contains(normQ, ignoreCase = true)
                val matchesWordUrdu = w.urdu.contains(q, ignoreCase = true)
                val matchesRoot = normalizeArabic(w.root).contains(normQ, ignoreCase = true)
                val matchesRole = w.grammaticalRole.contains(q, ignoreCase = true)

                if (matchesWordArabic || matchesWordUrdu || matchesRoot || matchesRole) {
                    matchedWords.add(verse to w)
                }
            }
        }

        return SearchResults(
            matchedVerses = matchedVerses,
            matchedWords = matchedWords.take(40)
        )
    }

    fun getGrammarLessons(): List<GrammarLesson> = GrammarLessonsData.lessons

    fun getGrammarLessonById(id: Int): GrammarLesson? = GrammarLessonsData.lessons.find { it.id == id }

    // Generates dynamic vocabulary quiz from words in Surah Aal-e-Imran
    fun generateVocabularyQuiz(count: Int = 10): List<QuizQuestion> {
        val allVerses = getAllVerses()
        val pool = allVerses.flatMap { v -> v.words.map { v.verseNumber to it } }
            .filter { it.second.urdu.isNotBlank() && it.second.arabic.length >= 2 }
            .shuffled()

        val questions = mutableListOf<QuizQuestion>()
        var qId = 1

        for ((vNum, targetWord) in pool) {
            if (questions.size >= count) break

            // Pick 3 distractor meanings from pool
            val distractors = pool.map { it.second.urdu }
                .filter { it != targetWord.urdu && it.isNotBlank() }
                .distinct()
                .shuffled()
                .take(3)

            if (distractors.size < 3) continue

            val options = (distractors + targetWord.urdu).shuffled()
            val correctIdx = options.indexOf(targetWord.urdu)

            questions.add(
                QuizQuestion(
                    id = "vocab_$qId",
                    type = QuizType.VOCABULARY,
                    questionUrdu = "قرآنی لفظ '${targetWord.arabic}' کا درست اردو مفہوم کیا ہے؟",
                    promptSnippet = targetWord.arabic,
                    options = options,
                    correctIndex = correctIdx,
                    explanationUrdu = "آیت نمبر $vNum میں '${targetWord.arabic}' کا معنی '${targetWord.urdu}' ہے، جبکہ اس کا روٹ '${targetWord.root}' ہے۔",
                    verseNumber = vNum
                )
            )
            qId++
        }

        return questions
    }

    // Generates grammar quiz from verified words and rules in Surah Aal-e-Imran
    fun generateGrammarQuiz(count: Int = 10): List<QuizQuestion> {
        val allVerses = getAllVerses()
        val questions = mutableListOf<QuizQuestion>()
        var qId = 1

        val pool = allVerses.flatMap { v -> v.words.map { v.verseNumber to it } }.shuffled()

        for ((vNum, targetWord) in pool) {
            if (questions.size >= count) break

            when (qId % 3) {
                0 -> {
                    // Category identification (اسم، فعل، حرف)
                    val options = listOf("اسم (Noun)", "فعل (Verb)", "حرف (Particle)")
                    val correctLabel = when (targetWord.category) {
                        WordCategory.ISM -> "اسم (Noun)"
                        WordCategory.FI_L -> "فعل (Verb)"
                        WordCategory.HARF -> "حرف (Particle)"
                    }
                    val correctIdx = options.indexOf(correctLabel)

                    questions.add(
                        QuizQuestion(
                            id = "grammar_$qId",
                            type = QuizType.GRAMMAR,
                            questionUrdu = "کلمہ کی اقسام میں سے لفظ '${targetWord.arabic}' کس زمرے میں آتا ہے؟",
                            promptSnippet = targetWord.arabic,
                            options = options,
                            correctIndex = correctIdx,
                            explanationUrdu = "${targetWord.arabic} ایک ${targetWord.category.urduLabel} ہے: ${targetWord.urduExplanation}",
                            verseNumber = vNum
                        )
                    )
                }
                1 -> {
                    // Root letters identification
                    if (targetWord.root.length >= 3 && targetWord.root != "-") {
                        val realRoot = targetWord.root
                        val fakeRoots = listOf("ن ز ل", "ك ت ب", "ع ل م", "ح ق ق", "خ ل ق", "ص د ق")
                            .filter { it != realRoot }
                            .shuffled()
                            .take(3)
                        val options = (fakeRoots + realRoot).shuffled()
                        val correctIdx = options.indexOf(realRoot)

                        questions.add(
                            QuizQuestion(
                                id = "grammar_$qId",
                                type = QuizType.GRAMMAR,
                                questionUrdu = "لفظ '${targetWord.arabic}' کے بنیادی حروفِ اصلیہ (جذر / Root) کیا ہیں؟",
                                promptSnippet = targetWord.arabic,
                                options = options,
                                correctIndex = correctIdx,
                                explanationUrdu = "لفظ '${targetWord.arabic}' کا مادہ '${realRoot}' ہے۔",
                                verseNumber = vNum
                            )
                        )
                    }
                }
                2 -> {
                    // Grammatical Role
                    if (targetWord.grammaticalRole.isNotBlank()) {
                        val realRole = targetWord.grammaticalRole
                        val optionsPool = listOf("مبتدا مرفوع", "مفعول به منصوب", "فاعل مرفوع", "حرف جر", "صفت موصوف", "مضاف إليه مجرور")
                            .filter { it != realRole }
                            .shuffled()
                            .take(3)
                        val options = (optionsPool + realRole).shuffled()
                        val correctIdx = options.indexOf(realRole)

                        questions.add(
                            QuizQuestion(
                                id = "grammar_$qId",
                                type = QuizType.GRAMMAR,
                                questionUrdu = "آیت $vNum میں لفظ '${targetWord.arabic}' کا نحوی کردار کیا ہے؟",
                                promptSnippet = targetWord.arabic,
                                options = options,
                                correctIndex = correctIdx,
                                explanationUrdu = "نحوی اعتبار سے یہ '${realRole}' ہے: ${targetWord.urduExplanation}",
                                verseNumber = vNum
                            )
                        )
                    }
                }
            }
            qId++
        }

        return questions
    }
}
