# Noor — Quran Arabic Learning (نُور)
## Comprehensive Application Summary & Architectural Documentation

---

### 1. Executive Summary

**Noor (نُور)** is a native Android application designed to teach Quranic Arabic to Urdu-speaking users through word-by-word morphological breakdown, grammatical analysis, contextual translations, and audio recitation. The application focuses on **Surah Aal-e-Imran (سورة آل عمران), verses 1–92**, which constitutes the portion of the Surah included in **Para 3**.

The primary objective of Noor is to bridge the divide between passive Quran recitation and active Arabic language comprehension. Rather than simply displaying verses and static translations, every single Arabic word is tokenized and interactively analyzed to display its root letters (جذر), grammatical category (اسم، فعل، حرف), morphological pattern (وزن), sentence role (فاعل، مفعول به، مبتدا، خبر، etc.), and contextual Urdu meaning.

---

### 2. Key App Details & Metadata

| Attribute | Specification |
| :--- | :--- |
| **App Name** | Noor — Quran Arabic Learning (نُور) |
| **Launcher Label** | Noor |
| **Target Platform** | Android 7.0+ (API 24 to 36) |
| **Primary Language** | Urdu (اردو) with Right-to-Left (RTL) text rendering |
| **Quran Content** | Surah Aal-e-Imran (Verses 1 to 92 • Para 3) |
| **Arabic Script** | Authentic Indo-Pak Quranic Script with full diacritics (`رموز اوقاف`) |
| **Urdu Translation** | Maulana Fateh Muhammad Jalandhari (فاتح محمد جالندھری) |
| **Quran Reciter** | Sheikh Abdul Basit Abdul Samad (شیخ عبد الباسط عبد الصمد - مرتل) |
| **Design Theme** | Islamic Emerald Green (`#071910`, `#0D281C`) & Royal Gold (`#D4AF37`) |
| **Local Persistence**| Android Room Database (SQLite) + Reactive Kotlin Flow |
| **Audio Solution** | Android MediaPlayer with live CDN streaming and buffering controls |

---

### 3. Core Architectural Modules

```
┌────────────────────────────────────────────────────────────────────────┐
│                        NOOR ANDROID APPLICATION                        │
│                                                                        │
│  ┌───────────────────────┐  ┌─────────────────┐  ┌──────────────────┐  │
│  │   Home & Dashboard    │  │  Quran Reader   │  │  Word Breakdown  │  │
│  │  (Progress & Streaks) │  │ (Clickable Text)│  │   BottomSheet    │  │
│  └───────────┬───────────┘  └────────┬────────┘  └────────┬─────────┘  │
│              │                       │                    │            │
│  ┌───────────┴───────────┐  ┌────────┴────────┐  ┌────────┴─────────┐  │
│  │  Grammar Center (12)  │  │  Search Engine  │  │  Quiz & Revision │  │
│  │  (Rules & Aal-e-Imran)│  │ (Diacritic-Free)│  │  (Vocab & Grammar)│ │
│  └───────────┬───────────┘  └────────┬────────┘  └────────┬─────────┘  │
│              └───────────────────────┼────────────────────┘            │
│                                      ▼                                 │
│                     ┌──────────────────────────────────┐               │
│                     │       NOOR VIEWMODEL LAYER       │               │
│                     │  • StateFlow UI State Machine    │               │
│                     │  • BackHandler Stack Navigation  │               │
│                     └────────────────┬─────────────────┘               │
└──────────────────────────────────────┼─────────────────────────────────┘
                                       │
                                       ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        DATA & PERSISTENCE LAYER                        │
│                                                                        │
│  ┌────────────────────────┐  ┌──────────────────┐  ┌─────────────────┐ │
│  │    QuranRepository     │  │ Room Database    │  │ Audio Service   │ │
│  │ • 92 Verses JSON Asset │  │ • Bookmarks      │  │ • Abdul Basit   │ │
│  │ • Diacritic Normalizer │  │ • Saved Words    │  │   EveryAyah CDN │ │
│  │ • Quiz Generator       │  │ • Progress Track │  │ • Seek & Repeat │ │
│  └────────────────────────┘  └──────────────────┘  └─────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

---

### 4. Detailed Feature Breakdown

#### 4.1 Home Screen & Dashboard
- **Islamic Greeting**: Displays *"السلام علیکم"* accompanied by the Noor brand badge.
- **Surah Aal-e-Imran Learning Card**:
  - Displays total verses (92) and completed verses count.
  - Interactive circular progress indicator displaying percentage mastered.
  - Quick action button to continue learning from the last reading point.
- **Modular Navigation Grid**: Direct access to Quran Reader, Grammar Center, Word Search, Bookmarks, and Settings.
- **Daily Quranic Wisdom**: Highlighted verse card showcasing daily spiritual reflections from Surah Aal-e-Imran.

#### 4.2 Quran Reader Screen
- **Indo-Pak Calligraphic Script**: Rendered in high-definition using the bundled **Amiri** font with complete diacritical marks (tashkeel, tanween, sukoon, maddah, and waqf marks).
- **Interactive Word-by-Word Chips**:
  - Each word is an individual interactive component wrapped inside an adaptive `FlowRow`.
  - Tapping any word provides subtle gold ripple feedback and opens the detailed analysis bottom sheet.
- **Urdu Translation**: Authentic translation by Fateh Muhammad Jalandhari rendered with the **Noto Nastaliq Urdu** font.
- **In-Screen Controls**:
  - **Jump to Verse Dialog**: Instant jump to any verse between 1 and 92.
  - **Typography Adjuster**: Real-time sliders for resizing Arabic script (20sp–40sp) and Urdu translation (13sp–24sp).
  - **Verse Actions**: Bookmark verse, mark verse as completed, view Tafseer contextual notes, and play audio recitation.

#### 4.3 Word-by-Word Analysis Bottom Sheet
When any word in a verse is clicked, an informative modal sheet appears containing:
1. **Selected Word in Calligraphy**: Large Arabic text with diacritics.
2. **Contextual Urdu Translation**: Meaning of the word in this specific verse.
3. **Pronunciation & Transliteration**: English phonetic guide.
4. **Root Letters (جذر)**: 3-letter Arabic root with a one-tap search to find all occurrences of that root in Surah Aal-e-Imran.
5. **Word Category Badge**: Color-coded badges for **اسم (Noun)**, **فعل (Verb)**, or **حرف (Particle)**.
6. **Grammatical Role in Sentence**: Exact syntax role (مبتدا، خبر، فاعل، مفعول به، صفت، مضاف إليه، جار ومجرور).
7. **Simplified Urdu Commentary**: Pedagogical notes explaining why the word holds this grammatical case and ending.
8. **Pronunciation Audio**: Button to listen to the word pronunciation audio.
9. **Save Difficult Word**: One-tap toggle to add the word to the user's personal revision list.

#### 4.4 Arabic Grammar Learning Center (۱۲ اسباق)
Twelve structured, textbook-grade lessons specifically illustrated with verified Quranic examples from Surah Aal-e-Imran:
1. **کلمہ کی اقسام**: اسم، فعل، اور حرف کی پہچان اور علامات۔
2. **جملہ اسمیہ**: مبتدا اور خبر کے قواعد اور اعراب۔
3. **جملہ فعلیہ**: فعل، فاعل (مرفوع) اور مفعول بہ (منصوب) کی ترتیب۔
4. **اعراب کی اقسام**: رفع (ضمہ)، نصب (فتحہ)، جر (کسرہ)، اور جزم (سکون)۔
5. **فعل کی اقسام**: ماضی، مضارع (مع حروفِ اتین)، اور امر۔
6. **جنس کے احکام**: مذکر اور مؤنث (قیاسی و سماعی)۔
7. **عدد کے احکام**: واحد، تثنیہ (ـانِ / ـَيْنِ) اور جمع (سالم و مکسر)۔
8. **معرفہ اور نکرہ**: 'ال' کی تعریف اور تنوین کا باہمی ربط۔
9. **حروفِ اصلیہ اور اوزان**: ثلاثی مجرد اور ابواب (افعال و تفعیل) کا مفہوم۔
10. **قرآنی حروفِ معانی**: حروفِ جارہ، عاطفہ اور حروفِ نفی (لَا، مَا، لَمْ، لَنْ)۔
11. **مرکب اضافی**: مضاف اور مضاف الیہ کے احکام و اعراب۔
12. **مرکب توصیفی**: موصوف اور صفت کی چاروں امور میں مطابقت۔

#### 4.5 Search & Explorer Engine
- **Diacritic-Tolerant Search**: Strips Arabic diacritics dynamically so searching `الله` matches `اللّٰهُ`, and searching `نزل` matches `نَزَّلَ`.
- **Urdu Translation Search**: Matches phrases inside Urdu translations (e.g. `کتاب`, `زندہ`, `رحمت`).
- **Root-Letter Lookup**: Searches words sharing specific Arabic roots (e.g. `ك ت ب` or `ح ق ق`).
- **Quick Verse Number Jump**: Typing any number 1–92 instantly filters that verse.
- **Direct Navigation**: Clicking any search result opens the verse reader or launches the word analysis sheet directly.

#### 4.6 Floating Audio Recitation Engine
- **Reciter**: Sheikh Abdul Basit Abdul Samad (Murattal pace, ideal for learners).
- **Streaming & Buffering**: Low-latency playback from high-reliability EveryAyah CDN.
- **Controls**:
  - Play / Pause with live buffering indicator.
  - Previous / Next verse skipping.
  - Repeat Verse Toggle (allows listening to difficult verses on loop).
  - Scrubbable progress bar / seek slider with formatted timestamp (`MM:SS`).
  - Auto-advance to the next verse upon completion.

#### 4.7 Bookmarks & Personal Revision
- **Two Dedicated Tabs**:
  - **محفوظ آیات (Bookmarked Verses)**: Lists all bookmarked verses with full Arabic and Urdu translations.
  - **مشکل الفاظ (Saved Difficult Words)**: Lists user-saved words with their roots and Urdu meanings for flashcard-style review.
- **Local Persistence**: Powered by SQLite Room database; stored offline permanently.

#### 4.8 Interactive Revision Quizzes
- **Vocabulary Quiz**: Multiple-choice testing Arabic-to-Urdu meanings with 3 randomized distractors.
- **Grammar Quiz**: Tests identifying word categories (اسم vs فعل vs حرف), root letters, and grammatical roles.
- **Instant Pedagogical Feedback**: Option cards glow green for correct and red for incorrect while displaying the scholarly explanation immediately.
- **Score Card**: End-of-quiz score percentage, performance evaluation (*ماشاء اللہ*), and retry options.

#### 4.9 Settings & Customization
- **Theme Selection**: Toggle between **Emerald Dark** (deep forest green) and **Parchment Light** (warm ivory).
- **Live Typography Scaler**: Sliders with real-time preview text for both Quranic Arabic and Urdu scripts.
- **Content Verification Statement**: Full disclosure of scholarly sources and methodology.

---

### 5. Data Architecture & Database Schemas

#### 5.1 Room Database Entities

```kotlin
// Bookmarks Table
@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val verseNumber: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

// Saved Difficult Words Table
@Entity(tableName = "saved_words")
data class SavedWordEntity(
    @PrimaryKey val wordId: String,
    val arabic: String,
    val urdu: String,
    val transliteration: String,
    val root: String,
    val category: String,
    val verseNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)

// Learning Progress Table
@Entity(tableName = "learning_progress")
data class ProgressEntity(
    @PrimaryKey val verseNumber: Int,
    val isCompleted: Boolean = true,
    val lastReadTimestamp: Long = System.currentTimeMillis()
)

// Quiz Performance Records Table
@Entity(tableName = "quiz_records")
data class QuizRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizType: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)
```

#### 5.2 Tokenized Word JSON Schema

```json
{
  "wordId": "3_2_1",
  "position": 1,
  "arabic": "اللّٰهُ",
  "urdu": "اللہ تعالیٰ",
  "transliteration": "al-lahu",
  "root": "ا ل ه",
  "category": "ISM",
  "grammaticalRole": "مبتدا مرفوع بالضمة",
  "urduExplanation": "اسم جلالت، جملہ اسمیہ کا مبتدا ہے، حالتِ رفع میں ضمہ کے ساتھ۔",
  "audioUrl": "https://audio.qurancdn.com/wbw/003_002_001.mp3",
  "isVerified": true
}
```

---

### 6. Design System & Typography Tokens

- **Palette**:
  - `EmeraldDarkBackground` = `#071910`
  - `EmeraldDarkSurface` = `#0D281C`
  - `EmeraldDarkSurfaceVariant` = `#143B2A`
  - `GoldPrimary` = `#D4AF37`
  - `GoldLight` = `#F3D267`
  - `IslamicGreenAccent` = `#38B272`
  - `ParchmentLightBackground` = `#FBF8F0`
- **Typography**:
  - Quranic Script: `amiri.ttf` (Local Google Font asset)
  - Urdu Script: `noto_nastaliq_urdu.ttf` (Local Google Font asset)
- **Component Highlights**:
  - Custom adaptive launcher icon with concentric gold crescent and 8-point Noor star.
  - Islamic calligraphic header cards and ornate circular verse number badges.

---

### 7. Standalone Backend Server (`noor_standalone_backend_server.py`)

In addition to the native Android app, a single-file zero-dependency Python backend server is included in the project root:
- Runs via standard Python 3: `python3 noor_standalone_backend_server.py`
- Serves complete REST endpoints on port 8000:
  - `GET /api/verses`: Full list of 92 verses with word-by-word tokenized items.
  - `GET /api/verse/<n>`: Single verse detail.
  - `GET /api/search?q=<query>`: Live Arabic/Urdu/Root search.
  - `GET /api/grammar/lessons`: 12 comprehensive grammar lessons.
  - `GET /api/quiz?type=<type>`: 10-question dynamic quiz generator.
  - `GET|POST /api/bookmarks`: Bookmark management backed by SQLite.
  - `GET|POST /api/saved-words`: Saved difficult words backed by SQLite.
  - `GET|POST /api/progress`: Verse completion tracking.

---

### 8. Installation & Verification

1. **Compiled Debug APK Location**:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```
2. **Install via ADB**:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
3. **Build Release APK**:
   ```bash
   gradle :app:assembleRelease
   ```
4. **Run Local Unit Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```
   *(All tests pass with 100% success rate)*
