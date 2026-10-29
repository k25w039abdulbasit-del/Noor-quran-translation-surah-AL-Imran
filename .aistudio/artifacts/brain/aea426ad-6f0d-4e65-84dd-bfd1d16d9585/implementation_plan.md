# Noor — Quran Arabic Learning (Surah Aal-e-Imran 1–92)

A comprehensive, mobile-first Android application designed to teach Quranic Arabic through Surah Aal-e-Imran (verses 1–92, Para 3) in Urdu, featuring verified word-by-word morphological analysis, root letter extraction, contextual Urdu translation, Abdul Basit Abdul Samad audio recitation, interactive grammar lessons, revision quizzes, and offline persistence.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following user preferences were confirmed during the planning phase:
> - **Quran Script Style**: Indo-Pak Quranic typography with complete diacritics (tashkeel/harakat) for familiar and effortless Quranic reading.
> - **Quran Audio Reciter**: Sheikh Abdul Basit Abdul Samad (Murattal style) streaming high-clarity verse recitations with full playback, repeat, and seek controls.
> - **Word Analysis UX**: Interactive bottom sheet displaying deep morphological breakdown, roots (جذر), grammatical category (اسم / فعل / حرف), sentence role (فاعل, مفعول, مبتدا, etc.), and Urdu grammatical notes upon tapping any Arabic word.

---

## 1. Overview & Core Concept

- **What It Does**: Noor empowers Urdu-speaking students, scholars, and daily Quran readers to master Quranic Arabic word by word. Users can read verses 1 to 92 of Surah Aal-e-Imran, listen to verse-by-verse recitation by Qari Abdul Basit, tap any word to reveal its grammatical morphology, explore structured Arabic grammar modules, search across Arabic and Urdu roots, and track retention through adaptive quizzes.
- **Target Audience**: Urdu-speaking Quran learners, students of Arabic grammar (Nahw & Sarf), and Muslims seeking a deeper grammatical and contextual understanding of Surah Aal-e-Imran.
- **Key Value**: Bridges the gap between passive Quran recitation and active Arabic comprehension by breaking down every word into its grammatical constituents, root letters, and contextual Urdu significance.

---

## 2. User Experience & Visual Design

### Visual Identity & Theme
- **Emerald & Gold Islamic Aesthetic**:
  - Primary Background: Deep Emerald Green (`#0A2417` dark, `#0E3B27` surface container) and Soft Parchment Ivory (`#FBF8F1` light mode).
  - Accents: Royal Islamic Gold (`#D4AF37`, `#F3CA52`, `#8C6D1F`) for verse markers, active state chips, buttons, and calligraphic borders.
  - Surface Depth: Tonal emerald cards with subtle geometric arabesque borders and golden highlights.
  - Mode Support: Full dynamic Material 3 Dark & Light theme toggling with persistent user preference.
- **Typography & Layout**:
  - Right-to-Left (RTL) first layout for Arabic verses and Urdu translations.
  - High-clarity Indo-Pak style Arabic typography with spacious line heights and crisp diacritics.
  - Nastaliq-inspired Urdu typography for translation and grammar explanations.
  - Configurable font sizing for both Arabic verses (22sp–40sp) and Urdu text (14sp–24sp).

### Key User Flows & Screen Navigation
1. **Splash Screen**: Elegant gold calligraphy of *نُور* on deep emerald background with smooth fade-in to Dashboard.
2. **Home Screen**:
   - Islamic greeting (*السلام علیکم*) with daily verse inspiration.
   - Surah Aal-e-Imran progress banner (completed verses, reading streak, saved words).
   - Quick Access Grid: Continue Reading, Word-by-Word Reader, Grammar Learning Center, Search, Revision Quizzes, Bookmarks & Saved Words.
3. **Quran Reader Screen**:
   - Clean verse cards displaying verse number, Indo-Pak Arabic script, and Urdu translation.
   - Interactive Arabic text: Every single word is an accessible touch target with gold ripple feedback.
   - Floating Audio Bar: Play/Pause current verse, seek bar, previous/next verse, repeat toggle, and reciter indicator (Abdul Basit).
   - Verse actions: Bookmark verse, copy text, view verse context/Tafseer note.
4. **Interactive Word Analysis Bottom Sheet**:
   - Selected Arabic word in large Indo-Pak script with diacritics.
   - Contextual Urdu meaning vs. root general meaning.
   - Transliteration & pronunciation guide.
   - Root letters (جذر) with quick search link.
   - Word classification tag: اسم (Noun), فعل (Verb), or حرف (Particle).
   - Morphological breakdown (Wazn / وزن, Tense, Gender, Number, Case / اعراب).
   - Sentence role (مبتدا، خبر، فاعل، مفعول به، صفت، مضاف إليه، جار ومجرور).
   - Simple Urdu grammar commentary.
   - Quick "Save to Difficult Words" toggle.
5. **Arabic Grammar Learning Center**:
   - 12 comprehensive modular lessons: Ism/Fi'l/Harf, Mubtada/Khabar, Fa'il/Maf'ul, Marfoo'/Mansub/Majroor/Majzoom, Past/Present/Imperative verbs, Gender & Plurals, Definite/Indefinite (ال), Idafah, Adjectives (موصوف صفت), and Quranic particles.
   - Real examples directly referenced from Surah Aal-e-Imran.
6. **Search & Explorer Screen**:
   - Live query matching Arabic words (with/without diacritics), Urdu meanings, root letters, and grammar types.
   - Direct jump to verse reader or word breakdown bottom sheet.
7. **Revision & Quizzes Screen**:
   - Vocabulary Quizzes (Multiple choice Arabic-to-Urdu and Urdu-to-Arabic).
   - Grammar Quizzes (Identifying Ism vs Fi'l vs Harf, identifying roots and sentence roles).
   - Saved Difficult Words flashcard review.
8. **Settings & Customization**:
   - Theme toggle (Emerald Dark / Classic Light / System default).
   - Font size sliders for Arabic script and Urdu translation.
   - Audio playback speed & auto-advance options.

---

## 3. Key Product Decisions & Trade-Offs

- **Local Storage via Room**:
  - *Chosen Approach*: Android Room Database with reactive Kotlin `Flow` for offline persistence of bookmarks, completed verses, saved words, and quiz progress.
  - *Why*: Ensures the application works 100% offline without mandatory network connectivity; users never lose bookmarks or flashcards.
- **Audio Streaming with Android MediaPlayer**:
  - *Chosen Approach*: Stream high-fidelity recitations by Sheikh Abdul Basit Abdul Samad from EveryAyah CDN with playback caching and state management.
  - *Why*: Balances APK size (preventing a 100MB+ APK download) while providing immediate, pristine audio recitation with playback error recovery.
- **Curated Surah Aal-e-Imran Dataset (Verses 1–92)**:
  - *Chosen Approach*: High-integrity JSON asset dataset containing complete Arabic text with tashkeel, authenticated Urdu translations (e.g. Fateh Muhammad Jalandhari / Tahir-ul-Qadri tradition), and verified word-by-word tokenized grammar records.
  - *Why*: Guarantees zero fabricated Quranic content, consistent verse numbering, and precise word-to-verse associations.

---

## 4. Technical Architecture & Data Strategy

### System Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        NOOR JETPACK COMPOSE UI                         │
│                                                                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌────────────┐  │
│  │ Home Dashboard│  │ Quran Reader │  │ Word Details │  │ Grammar Hub│  │
│  │ (Progress)   │  │ (Verses/Audio│  │ BottomSheet  │  │ & Quizzes  │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └─────┬──────┘  │
│         │                 │                 │                │         │
│         └────────────┬────┴─────────────────┴────────────────┘         │
│                      ▼                                                 │
│       ┌──────────────────────────────────────────────┐                 │
│       │             VIEWMODEL LAYER                  │                 │
│       │  • QuranViewModel      • GrammarViewModel    │                 │
│       │  • AudioPlayerState    • QuizViewModel       │                 │
│       └──────────────────────┬───────────────────────┘                 │
└──────────────────────────────┼─────────────────────────────────────────┘
                               │
                               ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        REPOSITORY & SERVICES                           │
│                                                                        │
│  ┌─────────────────────────┐           ┌────────────────────────────┐  │
│  │     QuranRepository     │           │      AudioPlayerService    │  │
│  │  - Verse & Word Loader  │           │  - Sheikh Abdul Basit CDN  │  │
│  │  - Root & Search Index  │           │  - Play / Pause / Seek     │  │
│  └────────────┬────────────┘           └─────────────┬──────────────┘  │
│               │                                      │                 │
│  ┌────────────┴────────────┐           ┌─────────────┴──────────────┐  │
│  │    Room AppDatabase     │           │    Android MediaPlayer     │  │
│  │  • BookmarkDao          │           │    • State Listeners       │  │
│  │  • SavedWordDao         │           │    • Network Fallbacks     │  │
│  │  • LearningProgressDao  │           └────────────────────────────┘  │
│  └─────────────────────────┘                                           │
└────────────────────────────────────────────────────────────────────────┘
```

### Data Models & Schemas

1. **Verse Entity / Domain Model**:
   - `verseNumber`: Int (1..92)
   - `arabicText`: String (Indo-Pak Naskh with full tashkeel)
   - `urduTranslation`: String
   - `words`: List<QuranWord>
   - `audioUrl`: String (Abdul Basit recitation URL)
   - `tafseerNote`: String (Contextual lesson / explanation)

2. **QuranWord Model (Tokenized)**:
   - `wordId`: String (`3_1_1`, `3_1_2`...)
   - `arabic`: String
   - `urdu`: String
   - `transliteration`: String
   - `root`: String? (e.g. "ك ت ب")
   - `category`: WordCategory (`ISM`, `FI_L`, `HARF`)
   - `wazn`: String? (e.g. "فَاعِل")
   - `grammarRole`: String (e.g. "مبتدا", "فاعل مرفوع", "حرف جر")
   - `detailedGrammarUrdu`: String

3. **Room Database Entities**:
   - `BookmarkEntity`: `verseNumber`, `timestamp`, `note`
   - `SavedWordEntity`: `wordId`, `arabic`, `urdu`, `root`, `verseNumber`, `timestamp`
   - `ProgressEntity`: `verseNumber`, `isRead`, `lastReadTimestamp`
   - `QuizScoreEntity`: `quizType`, `score`, `total`, `date`

---

## 5. Implementation Roadmap

1. **Project Setup & Dependencies**:
   - Verify Room KSP, Material 3, and Icons dependencies in `build.gradle.kts`.
   - Update `metadata.json` and `strings.xml` to match "Noor — Quran Arabic Learning".
2. **Color Palette & Theme**:
   - Construct emerald-green (`#0A2417`, `#0E3B27`) and golden accents (`#D4AF37`, `#F3CA52`) with custom Islamic styled shapes and typography in `Theme.kt`.
3. **Quran Content & Dataset**:
   - Bundle structured, verified data for Surah Aal-e-Imran (verses 1–92) with word-by-word Urdu mappings, roots, grammar roles, and audio endpoints.
4. **Data Layer & Room Persistence**:
   - Implement `AppDatabase`, `BookmarkDao`, `SavedWordDao`, `ProgressDao`, and `QuranRepository`.
5. **Audio Player Engine**:
   - Implement audio playback manager for Sheikh Abdul Basit Abdul Samad recitation with error handling, buffering states, and repeat controls.
6. **Compose UI Screens**:
   - Splash & Onboarding
   - Main Home Dashboard with progress rings & quick navigation
   - Quran Verse Reader with interactive clickable words and audio bar
   - Detailed Word Analysis Bottom Sheet with morphology tags and grammar cards
   - Arabic Grammar Learning Center (12 structured lessons with Quranic examples)
   - Interactive Vocabulary & Grammar Quizzes
   - Search Engine (supporting Arabic with/without diacritics, Urdu meanings, and roots)
   - Bookmarks & Saved Difficult Words
   - Settings & Theme Selector
7. **Verification & Testing**:
   - Run compilation via `compile_applet` and execute unit tests.
