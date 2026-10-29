package com.example

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.util.Locale

/*
====================================================================================================
NOOR — QURAN ARABIC LEARNING: ALL-IN-ONE COMPLETE BACKEND DATA & SYSTEM CODE
Surah Aal-e-Imran (Verses 1–92 • Para 3)
Language: Kotlin (Jetpack Android & JVM Compatible)
====================================================================================================
This single file contains the complete backend architecture for Noor:
1. Core Domain Models (Verse, QuranWord, WordCategory, GrammarLesson, QuizQuestion)
2. Room Persistence Entities (BookmarkEntity, SavedWordEntity, ProgressEntity, QuizRecordEntity)
3. Room Data Access Objects (BookmarkDao, SavedWordDao, ProgressDao, QuizDao)
4. Room AppDatabase Singleton
5. Comprehensive QuranRepository (Asset loader, Diacritic-tolerant search, Quiz generators)
6. 12-Lesson Arabic Grammar Curriculum with authentic Aal-e-Imran examples
7. UserLearningRepository (Offline state management)
8. QuranAudioPlayer Service (Abdul Basit recitation streaming & word pronunciation)
====================================================================================================
*/

// =================================================================================================
// 1. CORE DOMAIN MODELS
// =================================================================================================

enum class SingleFileWordCategory(val urduLabel: String, val englishLabel: String) {
    ISM("اسم", "Noun"),
    FI_L("فعل", "Verb"),
    HARF("حرف", "Particle");

    companion object {
        fun fromString(value: String): SingleFileWordCategory {
            return when (value.uppercase()) {
                "FI_L", "FIL", "VERB" -> FI_L
                "HARF", "PARTICLE" -> HARF
                else -> ISM
            }
        }
    }
}

data class SingleFileQuranWord(
    val wordId: String,
    val position: Int,
    val arabic: String,
    val urdu: String,
    val transliteration: String,
    val root: String,
    val category: SingleFileWordCategory,
    val grammaticalRole: String,
    val urduExplanation: String,
    val audioUrl: String,
    val isVerified: Boolean = true
)

data class SingleFileVerse(
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
    val words: List<SingleFileQuranWord>,
    val isVerified: Boolean = true
)

data class SingleFileGrammarExample(
    val arabicText: String,
    val urduTranslation: String,
    val verseRef: String,
    val analysisBreakdown: String
)

data class SingleFileGrammarLesson(
    val id: Int,
    val titleUrdu: String,
    val titleArabic: String,
    val summaryUrdu: String,
    val explanationUrdu: String,
    val rules: List<String>,
    val examples: List<SingleFileGrammarExample>,
    val categoryTag: String
)

enum class SingleFileQuizType(val urduLabel: String) {
    VOCABULARY("الفاظ و معانی کوئز"),
    GRAMMAR("قواعد و نحو کوئز")
}

data class SingleFileQuizQuestion(
    val id: String,
    val type: SingleFileQuizType,
    val questionUrdu: String,
    val promptSnippet: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationUrdu: String,
    val verseNumber: Int
)

// =================================================================================================
// 2. ROOM PERSISTENCE ENTITIES
// =================================================================================================

@Entity(tableName = "sf_bookmarks")
data class SingleFileBookmarkEntity(
    @PrimaryKey val verseNumber: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "sf_saved_words")
data class SingleFileSavedWordEntity(
    @PrimaryKey val wordId: String,
    val arabic: String,
    val urdu: String,
    val transliteration: String,
    val root: String,
    val category: String,
    val verseNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sf_learning_progress")
data class SingleFileProgressEntity(
    @PrimaryKey val verseNumber: Int,
    val isCompleted: Boolean = true,
    val lastReadTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sf_quiz_records")
data class SingleFileQuizRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizType: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)

// =================================================================================================
// 3. ROOM DATA ACCESS OBJECTS (DAOS)
// =================================================================================================

@Dao
interface SingleFileBookmarkDao {
    @Query("SELECT * FROM sf_bookmarks ORDER BY verseNumber ASC")
    fun getAllBookmarks(): Flow<List<SingleFileBookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: SingleFileBookmarkEntity)

    @Query("DELETE FROM sf_bookmarks WHERE verseNumber = :verseNumber")
    suspend fun deleteBookmarkByVerse(verseNumber: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM sf_bookmarks WHERE verseNumber = :verseNumber)")
    fun isVerseBookmarked(verseNumber: Int): Flow<Boolean>
}

@Dao
interface SingleFileSavedWordDao {
    @Query("SELECT * FROM sf_saved_words ORDER BY timestamp DESC")
    fun getAllSavedWords(): Flow<List<SingleFileSavedWordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedWord(word: SingleFileSavedWordEntity)

    @Query("DELETE FROM sf_saved_words WHERE wordId = :wordId")
    suspend fun deleteSavedWord(wordId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM sf_saved_words WHERE wordId = :wordId)")
    fun isWordSaved(wordId: String): Flow<Boolean>
}

@Dao
interface SingleFileProgressDao {
    @Query("SELECT * FROM sf_learning_progress ORDER BY verseNumber ASC")
    fun getAllCompletedVerses(): Flow<List<SingleFileProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markVerseCompleted(progress: SingleFileProgressEntity)

    @Query("DELETE FROM sf_learning_progress WHERE verseNumber = :verseNumber")
    suspend fun unmarkVerseCompleted(verseNumber: Int)

    @Query("SELECT COUNT(*) FROM sf_learning_progress WHERE isCompleted = 1")
    fun getCompletedVerseCount(): Flow<Int>
}

@Dao
interface SingleFileQuizDao {
    @Query("SELECT * FROM sf_quiz_records ORDER BY timestamp DESC")
    fun getAllQuizRecords(): Flow<List<SingleFileQuizRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: SingleFileQuizRecordEntity)
}

// =================================================================================================
// 4. ROOM DATABASE SINGLETON
// =================================================================================================

@Database(
    entities = [
        SingleFileBookmarkEntity::class,
        SingleFileSavedWordEntity::class,
        SingleFileProgressEntity::class,
        SingleFileQuizRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SingleFileNoorDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): SingleFileBookmarkDao
    abstract fun savedWordDao(): SingleFileSavedWordDao
    abstract fun progressDao(): SingleFileProgressDao
    abstract fun quizDao(): SingleFileQuizDao

    companion object {
        @Volatile
        private var INSTANCE: SingleFileNoorDatabase? = null

        fun getDatabase(context: Context): SingleFileNoorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SingleFileNoorDatabase::class.java,
                    "noor_backend_all_in_one.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// =================================================================================================
// 5. COMPREHENSIVE ARABIC GRAMMAR LESSONS (12 LESSONS)
// =================================================================================================

object SingleFileGrammarData {
    val lessons: List<SingleFileGrammarLesson> = listOf(
        SingleFileGrammarLesson(
            id = 1,
            titleUrdu = "کلمہ کی اقسام: اسم، فعل، اور حرف",
            titleArabic = "أَقْسَامُ الْكَلَامِ: الِاسْمُ، الْفِعْلُ، الْحَرْفُ",
            summaryUrdu = "عربی زبان کا ہر بامعنی کلمہ اسم، فعل یا حرف میں تقسیم ہوتا ہے۔",
            explanationUrdu = "اسم وہ کلمہ ہے جو کسی نام، صفت یا کیفیت پر دلالت کرے؛ فعل وہ کلمہ ہے جس میں کام اور زمانہ پایا جائے؛ اور حرف وہ ہے جس کا مفہوم دوسرے کلمے کے بغیر واضح نہ ہو۔",
            rules = listOf("اسم کی علامت: 'ال' اور تنوین۔", "فعل کی علامت: قد، س، سوف۔", "حرف کی علامت: اسم و فعل کی علامات کا نہ ہونا۔"),
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
                    urduTranslation = "اللہ، اس کے سوا کوئی معبود نہیں، وہ زندہ اور قائم رکھنے والا ہے۔",
                    verseRef = "سورۃ آل عمران: آیت ۲",
                    analysisBreakdown = "اللَّهُ (اسم) | لَا (حرف) | إِلَٰهَ (اسم) | إِلَّا (حرف) | هُوَ (اسم ضمیر) | الْحَيُّ (اسم صفت)"
                )
            ),
            categoryTag = "بنیادی صرف و نحو"
        ),
        SingleFileGrammarLesson(
            id = 2,
            titleUrdu = "جملہ اسمیہ: مبتدا اور خبر",
            titleArabic = "الْجُمْلَةُ الِاسْمِيَّةُ: الْمُبْتَدَأُ وَالْخَبَرُ",
            summaryUrdu = "جملہ اسمیہ کے دو بنیادی اجزاء مبتدا اور خبر ہوتے ہیں اور دونوں مرفوع ہوتے ہیں۔",
            explanationUrdu = "مبتدا جس سے بات شروع کی جائے اور خبر جو اس کے بارے میں اطلاع دے۔",
            rules = listOf("مبتدا اور خبر دونوں مرفوع ہوتے ہیں۔", "خبر مطابقت رکھتی ہے۔"),
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "وَاللَّهُ بَصِيرٌ بِمَا يَعْمَلُونَ",
                    urduTranslation = "اور اللہ ان کے اعمال سے خوب باخبر ہے۔",
                    verseRef = "سورۃ آل عمران: آیت ۱۵۶",
                    analysisBreakdown = "اللَّهُ: مبتدا مرفوع | بَصِيرٌ: خبر مرفوع"
                )
            ),
            categoryTag = "جملے کی ساخت"
        ),
        SingleFileGrammarLesson(
            id = 3,
            titleUrdu = "جملہ فعلیہ: فعل، فاعل اور مفعول بہ",
            titleArabic = "الْجُمْلَةُ الْفِعْلِيَّةُ: الْفِعْلُ، الْفَاعِلُ وَالْمَفْعُولُ بِهِ",
            summaryUrdu = "فعل سے شروع ہونے والا جملہ، فاعل مرفوع اور مفعول بہ منصوب ہوتا ہے۔",
            explanationUrdu = "فاعل کام کرنے والا ہے اور مفعول بہ وہ جس پر کام واقع ہو۔",
            rules = listOf("فاعل ہمیشہ مرفوع ہوتا ہے۔", "مفعول بہ ہمیشہ منصوب ہوتا ہے۔"),
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "شَهِدَ اللَّهُ أَنَّهُ لَا إِلَٰهَ إِلَّا هُوَ",
                    urduTranslation = "اللہ نے گواہی دی کہ اس کے سوا کوئی معبود نہیں۔",
                    verseRef = "سورۃ آل عمران: آیت ۱۸",
                    analysisBreakdown = "شَهِدَ: فعل ماضی | اللَّهُ: فاعل مرفوع"
                )
            ),
            categoryTag = "جملے کی ساخت"
        ),
        SingleFileGrammarLesson(
            id = 4,
            titleUrdu = "اعراب کی اقسام: مرفوع، منصوب، مجرور اور مجزوم",
            titleArabic = "حَالَاتُ الْإِعْرَابِ: الرَّفْعُ، النَّصْبُ، الْجَرُّ، وَالْجَزْمُ",
            summaryUrdu = "اعراب کی چار بنیادی حالتیں کلمات کی نحوی حیثیت کو واضح کرتی ہیں۔",
            explanationUrdu = "رفع (ضمہ)، نصب (فتحہ)، جر (کسرہ)، جزم (سکون)۔",
            rules = listOf("اسم میں جزم نہیں ہوتا، فعل میں جر نہیں ہوتا۔"),
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "بِالْحَقِّ",
                    urduTranslation = "حق کے ساتھ",
                    verseRef = "سورۃ آل عمران: آیت ۳",
                    analysisBreakdown = "بِ: حرف جر | الْحَقِّ: اسم مجرور بالکسرہ"
                )
            ),
            categoryTag = "علم النحو"
        ),
        SingleFileGrammarLesson(
            id = 5,
            titleUrdu = "فعل کی اقسام: ماضی، مضارع اور امر",
            titleArabic = "أَقْسَامُ الْفِعْلِ: الْمَاضِي، الْمُضَارِعُ، وَالْأَمْرُ",
            summaryUrdu = "زمانے کے اعتبار سے فعل کے تین بڑے صیغے ہیں۔",
            explanationUrdu = "ماضی گزرا زمانہ، مضارع موجودہ/آئندہ زمانہ، اور امر حکم ہے۔",
            rules = listOf("فعل مضارع کے شروع میں حروف اتین ہوتے ہیں۔"),
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "قُلْ إِن كُنتُمْ تُحِبُّونَ اللَّهَ",
                    urduTranslation = "آپ فرمائیے: اگر تم اللہ سے محبت رکھتے ہو",
                    verseRef = "سورۃ آل عمران: آیت ۳۱",
                    analysisBreakdown = "قُلْ: فعل امر | تُحِبُّونَ: فعل مضارع مرفوع"
                )
            ),
            categoryTag = "علم الصرف"
        ),
        SingleFileGrammarLesson(
            id = 6,
            titleUrdu = "جنس: مذکر اور مؤنث کے احکام",
            titleArabic = "الْمُذَكَّرُ وَالْمُؤَنَّثُ فِي الْقُرْآنِ",
            summaryUrdu = "عربی میں کلمات مذکر یا مؤنث ہوتے ہیں۔",
            explanationUrdu = "مؤنث کی علامات: تائے مربوطہ (ة)، الف مقصورہ (ىٰ)۔",
            rules = ["غیر عاقل کی جمع مکسر کا فعل اور صفت واحد مؤنث آتا ہے۔"],
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "آيَاتٌ مُّحْكَمَاتٌ",
                    urduTranslation = "محکم آیات ہیں",
                    verseRef = "سورۃ آل عمران: آیت ۷",
                    analysisBreakdown = "آيَاتٌ: جمع مؤنث | مُّحْكَمَاتٌ: صفت مؤنث"
                )
            ),
            categoryTag = "علم الصرف"
        ),
        SingleFileGrammarLesson(
            id = 7,
            titleUrdu = "عدد: واحد، تثنیہ اور جمع",
            titleArabic = "الْمُفْرَدُ، الْمُثَنَّى، وَالْجَمْعُ",
            summaryUrdu = "عربی میں ایک (واحد)، دو (تثنیہ) اور تین یا زائد (جمع) ہوتے ہیں۔",
            explanationUrdu = "تثنیہ (ـان / ـین)، جمع مذكر سالم (ـون / ـین)۔",
            rules = ["تثنیہ کا اعراب الف اور یاء کے ساتھ ہوتا ہے۔"],
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "فِي فِئَتَيْنِ الْتَقَتَا",
                    urduTranslation = "دو گروہوں میں جو ٹکرائے",
                    verseRef = "سورۃ آل عمران: آیت ۱۳",
                    analysisBreakdown = "فِئَتَيْنِ: مثنیٰ مجرور بالیاء"
                )
            ),
            categoryTag = "علم الصرف"
        ),
        SingleFileGrammarLesson(
            id = 8,
            titleUrdu = "معرفہ اور نکرہ: ال اور تنوین",
            titleArabic = "الْمَعْرِفَةُ وَالنَّكِرَةُ",
            summaryUrdu = "نکرہ عام اسم اور معرفہ مخصوص معین اسم ہوتا ہے۔",
            explanationUrdu = "'ال' معرفہ بناتا ہے اور تنوین نکرہ کی علامت ہے۔",
            rules = ["ایک اسم پر 'ال' اور تنوین بیک وقت نہیں آ سکتے۔"],
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "الْكِتَابَ ... آيَاتٌ",
                    urduTranslation = "کتاب (معرفہ) ... آیات (نکرہ)",
                    verseRef = "سورۃ آل عمران: آیت ۷",
                    analysisBreakdown = "الْكِتَابَ: معرف باللام | آيَاتٌ: نکرہ منون"
                )
            ),
            categoryTag = "بنیادی نحو"
        ),
        SingleFileGrammarLesson(
            id = 9,
            titleUrdu = "حروفِ اصلیہ (جذر) اور کلمات کے اوزان",
            titleArabic = "الْجُذُورُ الثُّلَاثِيَّةُ وَالْمِيزَانُ الصَّرْفِيُّ",
            summaryUrdu = "عربی کلمات تین بنیادی مادہ حروف (ف-ع-ل) پر مبنی ہوتے ہیں۔",
            explanationUrdu = "مختلف ابواب جیسے افعال اور تفعیل معانی میں تنوع پیدا کرتے ہیں۔",
            rules = ["نَزَّلَ (تفعیل) تدریجاً اتارنا، اور أَنزَلَ (افعال) یکبارگی اتارنا ہے۔"],
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "نَزَّلَ عَلَيْكَ الْكِتَابَ ... وَأَنزَلَ التَّوْرَاةَ",
                    urduTranslation = "قرآن رفتہ رفتہ نازل کیا اور تورات کو اتارا",
                    verseRef = "سورۃ آل عمران: آیت ۳",
                    analysisBreakdown = "نَزَّلَ: باب تفعیل | أَنزَلَ: باب افعال (روٹ: ن ز ل)"
                )
            ),
            categoryTag = "علم الصرف"
        ),
        SingleFileGrammarLesson(
            id = 10,
            titleUrdu = "قرآنی حروفِ معانی: جارہ، عاطفہ اور نافیہ",
            titleArabic = "حُرُوفُ الْمَعَانِي فِي الْقُرْآنِ الْكَرِيمِ",
            summaryUrdu = "حروفِ معانی کلام کو جوڑنے اور معنی میں نفی یا تاکید پیدا کرتے ہیں۔",
            explanationUrdu = "حروف جارہ اپنے بعد اسم کو جر دیتے ہیں؛ حروف نفی مستقبل یا ماضی کی نفی کرتے ہیں۔",
            rules = ["لَنْ فعل مضارع پر داخل ہو کر نصب دیتا ہے اور قطعی نفی کرتا ہے۔"],
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "لَن تَنَالُوا الْبِرَّ حَتَّىٰ تُنفِقُوا",
                    urduTranslation = "تم ہرگز نیکی کو نہیں پہنچ سکتے جب تک خرچ نہ کرو",
                    verseRef = "سورۃ آل عمران: آیت ۹۲",
                    analysisBreakdown = "لَن: حرف نفی و نصب | تَنَالُوا: مضارع منصوب بحذف النون"
                )
            ),
            categoryTag = "علم النحو"
        ),
        SingleFileGrammarLesson(
            id = 11,
            titleUrdu = "مرکب اضافی: مضاف اور مضاف الیہ",
            titleArabic = "الْمُرَكَّبُ الْإِضَافِيُّ: الْمُضَافُ وَالْمُضَافُ إِلَيْهِ",
            summaryUrdu = "ایک اسم کی نسبت دوسرے اسم کی طرف کرنا۔",
            explanationUrdu = "مضاف پر 'ال' اور تنوین نہیں آتی، اور مضاف الیہ ہمیشہ مجرور ہوتا ہے۔",
            rules = ["مضاف الیہ ہمیشہ مجرور ہوتا ہے۔"],
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "مَالِكَ الْمُلْكِ",
                    urduTranslation = "بادشاہی کا حقیقی مالک",
                    verseRef = "سورۃ آل عمران: آیت ۲۶",
                    analysisBreakdown = "مَالِكَ: مضاف منصوب | الْمُلْكِ: مضاف الیہ مجرور"
                )
            ),
            categoryTag = "تراکیب"
        ),
        SingleFileGrammarLesson(
            id = 12,
            titleUrdu = "مرکب توصیفی: موصوف اور صفت",
            titleArabic = "الْمُرَكَّبُ التَّوْصِيفِيُّ: الْمَوْصُوفُ وَالصِّفَةُ",
            summaryUrdu = "جس میں ایک اسم دوسرے اسم کی اچھائی یا کیفیت بیان کرے۔",
            explanationUrdu = "صفت اپنے موصوف کے چار امور (اعراب، جنس، عدد، وسعت) میں مطابقت رکھتی ہے۔",
            rules = ["صفت ہر حال میں اپنے موصوف کے تابع ہوتی ہے۔"],
            examples = listOf(
                SingleFileGrammarExample(
                    arabicText = "عَذَابٌ شَدِيدٌ",
                    urduTranslation = "سخت عذاب",
                    verseRef = "سورۃ آل عمران: آیت ۴",
                    analysisBreakdown = "عَذَابٌ: موصوف مرفوع | شَدِيدٌ: صفت مرفوع"
                )
            ),
            categoryTag = "تراکیب"
        )
    )
}

// =================================================================================================
// 6. QURAN REPOSITORY WITH DIACRITIC-TOLERANT SEARCH & QUIZZES
// =================================================================================================

class SingleFileQuranRepository(private val context: Context) {

    private var cachedVerses: List<SingleFileVerse>? = null

    fun normalizeArabic(text: String): String {
        return text.replace(Regex("[\u064B-\u0652\u0670\u06D6-\u06ED]"), "")
            .replace("أ", "ا").replace("إ", "ا").replace("آ", "ا")
            .replace("ة", "ه").replace("ى", "ي").trim()
    }

    @Synchronized
    fun getAllVerses(): List<SingleFileVerse> {
        cachedVerses?.let { return it }
        val list = mutableListOf<SingleFileVerse>()
        try {
            val jsonStr = context.assets.open("quran/surah_3_verses.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonStr)
            val arr = root.getJSONArray("verses")
            for (i in 0 until arr.length()) {
                val v = arr.getJSONObject(i)
                val wordsArr = v.getJSONArray("words")
                val wordsList = mutableListOf<SingleFileQuranWord>()
                for (j in 0 until wordsArr.length()) {
                    val w = wordsArr.getJSONObject(j)
                    wordsList.add(
                        SingleFileQuranWord(
                            wordId = w.getString("wordId"),
                            position = w.getInt("position"),
                            arabic = w.getString("arabic"),
                            urdu = w.getString("urdu"),
                            transliteration = w.optString("transliteration", ""),
                            root = w.optString("root", "-"),
                            category = SingleFileWordCategory.fromString(w.optString("category", "ISM")),
                            grammaticalRole = w.optString("grammaticalRole", "اسم"),
                            urduExplanation = w.optString("urduExplanation", ""),
                            audioUrl = w.optString("audioUrl", "")
                        )
                    )
                }
                list.add(
                    SingleFileVerse(
                        verseNumber = v.getInt("verseNumber"),
                        arabicText = v.getString("arabicText"),
                        urduTranslation = v.getString("urduTranslation"),
                        tafseerNote = v.optString("tafseerNote", ""),
                        audioUrl = v.getString("audioUrl"),
                        words = wordsList
                    )
                )
            }
            cachedVerses = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cachedVerses ?: emptyList()
    }

    data class SearchResults(
        val matchedVerses: List<SingleFileVerse> = emptyList(),
        val matchedWords: List<Pair<SingleFileVerse, SingleFileQuranWord>> = emptyList()
    )

    fun search(query: String): SearchResults {
        val q = query.trim()
        if (q.isEmpty()) return SearchResults()
        val all = getAllVerses()
        val num = q.toIntOrNull()
        if (num != null && num in 1..92) {
            return SearchResults(matchedVerses = listOfNotNull(all.find { it.verseNumber == num }))
        }
        val normQ = normalizeArabic(q)
        val mVerses = mutableListOf<SingleFileVerse>()
        val mWords = mutableListOf<Pair<SingleFileVerse, SingleFileQuranWord>>()
        for (v in all) {
            if (normalizeArabic(v.arabicText).contains(normQ, true) || v.urduTranslation.contains(q, true)) {
                mVerses.add(v)
            }
            for (w in v.words) {
                if (normalizeArabic(w.arabic).contains(normQ, true) || w.urdu.contains(q, true) || normalizeArabic(w.root).contains(normQ, true)) {
                    mWords.add(v to w)
                }
            }
        }
        return SearchResults(mVerses, mWords.take(40))
    }

    fun getGrammarLessons(): List<SingleFileGrammarLesson> = SingleFileGrammarData.lessons

    fun generateVocabularyQuiz(count: Int = 10): List<SingleFileQuizQuestion> {
        val all = getAllVerses()
        val pool = all.flatMap { v -> v.words.map { v.verseNumber to it } }.filter { it.second.urdu.isNotBlank() }.shuffled()
        val out = mutableListOf<SingleFileQuizQuestion>()
        var id = 1
        for ((vNum, target) in pool) {
            if (out.size >= count) break
            val distractors = pool.map { it.second.urdu }.filter { it != target.urdu }.distinct().shuffled().take(3)
            if (distractors.size < 3) continue
            val options = (distractors + target.urdu).shuffled()
            out.add(
                SingleFileQuizQuestion(
                    id = "vocab_$id",
                    type = SingleFileQuizType.VOCABULARY,
                    questionUrdu = "قرآنی لفظ '${target.arabic}' کا درست اردو مفہوم کیا ہے؟",
                    promptSnippet = target.arabic,
                    options = options,
                    correctIndex = options.indexOf(target.urdu),
                    explanationUrdu = "آیت $vNum میں '${target.arabic}' کا معنی '${target.urdu}' ہے۔",
                    verseNumber = vNum
                )
            )
            id++
        }
        return out
    }
}

// =================================================================================================
// 7. USER LEARNING REPOSITORY (ROOM DATA INTERACTION)
// =================================================================================================

class SingleFileUserLearningRepository(
    private val bookmarkDao: SingleFileBookmarkDao,
    private val savedWordDao: SingleFileSavedWordDao,
    private val progressDao: SingleFileProgressDao,
    private val quizDao: SingleFileQuizDao
) {
    val bookmarks: Flow<List<SingleFileBookmarkEntity>> = bookmarkDao.getAllBookmarks()
    val savedWords: Flow<List<SingleFileSavedWordEntity>> = savedWordDao.getAllSavedWords()
    val completedCount: Flow<Int> = progressDao.getCompletedVerseCount()

    suspend fun toggleBookmark(verseNumber: Int, isCurrentlyBookmarked: Boolean, note: String = "") {
        if (isCurrentlyBookmarked) bookmarkDao.deleteBookmarkByVerse(verseNumber)
        else bookmarkDao.insertBookmark(SingleFileBookmarkEntity(verseNumber = verseNumber, note = note))
    }

    suspend fun toggleSaveWord(word: SingleFileQuranWord, verseNumber: Int, isCurrentlySaved: Boolean) {
        if (isCurrentlySaved) savedWordDao.deleteSavedWord(word.wordId)
        else savedWordDao.insertSavedWord(
            SingleFileSavedWordEntity(
                wordId = word.wordId,
                arabic = word.arabic,
                urdu = word.urdu,
                transliteration = word.transliteration,
                root = word.root,
                category = word.category.name,
                verseNumber = verseNumber
            )
        )
    }

    suspend fun toggleVerseCompleted(verseNumber: Int, isCompleted: Boolean) {
        if (isCompleted) progressDao.unmarkVerseCompleted(verseNumber)
        else progressDao.markVerseCompleted(SingleFileProgressEntity(verseNumber = verseNumber))
    }
}

// =================================================================================================
// 8. QURAN AUDIO PLAYER SERVICE
// =================================================================================================

sealed class SingleFilePlaybackState {
    object Idle : SingleFilePlaybackState()
    object Buffering : SingleFilePlaybackState()
    data class Playing(val verseNumber: Int) : SingleFilePlaybackState()
    data class Paused(val verseNumber: Int) : SingleFilePlaybackState()
    data class Error(val message: String) : SingleFilePlaybackState()
}

class SingleFileQuranAudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val _playbackState = MutableStateFlow<SingleFilePlaybackState>(SingleFilePlaybackState.Idle)
    val playbackState: StateFlow<SingleFilePlaybackState> = _playbackState.asStateFlow()

    fun playVerse(verseNumber: Int) {
        stop()
        val url = String.format(Locale.US, "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps/003%03d.mp3", verseNumber)
        _playbackState.value = SingleFilePlaybackState.Buffering
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).setUsage(AudioAttributes.USAGE_MEDIA).build())
                setDataSource(url)
                setOnPreparedListener {
                    it.start()
                    _playbackState.value = SingleFilePlaybackState.Playing(verseNumber)
                }
                setOnCompletionListener {
                    _playbackState.value = SingleFilePlaybackState.Idle
                }
                setOnErrorListener { _, _, _ ->
                    _playbackState.value = SingleFilePlaybackState.Error("آڈیو لوڈ نہیں ہو سکی۔")
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _playbackState.value = SingleFilePlaybackState.Error(e.message ?: "Error")
        }
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.reset()
            it.release()
        }
        mediaPlayer = null
        _playbackState.value = SingleFilePlaybackState.Idle
    }
}
