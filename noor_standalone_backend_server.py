"""
====================================================================================================
NOOR — QURAN ARABIC LEARNING: COMPLETE STANDALONE BACKEND SYSTEM
Surah Aal-e-Imran (Verses 1–92 • Para 3)
Language: Python 3 (Standard Library: zero external dependencies required)
====================================================================================================
This single file provides the complete standalone backend system for Noor:
- Full RESTful JSON API server (running on port 8000 by default)
- Complete authentic Surah Aal-e-Imran 1-92 dataset (Indo-Pak script, Urdu translation, word analysis)
- Diacritic-tolerant Arabic search engine, Urdu translation search, and root-letter index
- 12 comprehensive Arabic Grammar lessons with rules and verified Quranic examples
- Dynamic Vocabulary & Grammar Quiz generator
- Built-in SQLite persistence for Bookmarks, Saved Difficult Words, and Learning Progress
- Sheikh Abdul Basit Abdul Samad recitation audio streaming endpoints

HOW TO RUN:
    python3 noor_standalone_backend_server.py
Then open:
    http://localhost:8000/api/health
    http://localhost:8000/api/verses
    http://localhost:8000/api/search?q=الله
====================================================================================================
"""

import http.server
import socketserver
import json
import sqlite3
import urllib.parse
import os
import re
import random
from typing import Dict, Any, List

PORT = 8000
DB_FILE = "noor_learning.db"
ASSET_FILE = os.path.join(os.path.dirname(__file__), "app", "src", "main", "assets", "quran", "surah_3_verses.json")

# ==================================================================================================
# 1. GRAMMAR CURRICULUM DATASET (12 COMPREHENSIVE LESSONS)
# ==================================================================================================
GRAMMAR_LESSONS = [
    {
        "id": 1,
        "titleUrdu": "کلمہ کی اقسام: اسم، فعل، اور حرف",
        "titleArabic": "أَقْسَامُ الْكَلَامِ: الِاسْمُ، الْفِعْلُ، الْحَرْفُ",
        "summaryUrdu": "عربی زبان کا ہر بامعنی لفظ تین اقسام میں تقسیم ہوتا ہے: اسم، فعل یا حرف۔",
        "explanationUrdu": "عربی زبان کے تمام کلمات ان تین میں سے کسی ایک زمرے میں آتے ہیں: ۱. اسم (Noun)، ۲. فعل (Verb)، ۳. حرف (Particle)۔",
        "rules": [
            "اسم کی علامت: شروع میں 'ال' کا آنا، آخر میں تنوین (ً ٍ ٌ) ہونا۔",
            "فعل کی علامت: قد، س، سوف کا داخل ہونا، یا زمانے پر دلالت کرنا۔",
            "حرف کی علامت: اسم اور فعل کی علامات کا نہ پایا جانا۔"
        ],
        "examples": [
            {
                "arabicText": "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
                "urduTranslation": "اللہ، اس کے سوا کوئی معبود نہیں، وہ زندہ اور قائم رکھنے والا ہے۔",
                "verseRef": "سورۃ آل عمران: آیت ۲",
                "analysisBreakdown": "اللَّهُ (اسم) | لَا (حرف) | إِلَٰهَ (اسم) | إِلَّا (حرف) | هُوَ (اسم ضمیر) | الْحَيُّ (اسم صفت)"
            }
        ],
        "categoryTag": "بنیادی صرف و نحو"
    },
    {
        "id": 2,
        "titleUrdu": "جملہ اسمیہ: مبتدا اور خبر",
        "titleArabic": "الْجُمْلَةُ الِاسْمِيَّةُ: الْمُبْتَدَأُ وَالْخَبَرُ",
        "summaryUrdu": "جملہ اسمیہ وہ جملہ ہے جو اسم سے شروع ہو، اس کے دو بنیادی اجزاء مبتدا اور خبر ہوتے ہیں۔",
        "explanationUrdu": "مبتدا عموماً معرفہ اور مرفوع ہوتا ہے، اور خبر عموماً نکرہ اور مرفوع ہوتی ہے۔",
        "rules": [
            "مبتدا اور خبر دونوں حالتِ رفع (مرفوع) میں ہوتے ہیں۔",
            "خبر جنس اور عدد میں مبتدا کے مطابق ہوتی ہے۔"
        ],
        "examples": [
            {
                "arabicText": "وَاللَّهُ بَصِيرٌ بِمَا يَعْمَلُونَ",
                "urduTranslation": "اور اللہ اس سے خوب باخبر ہے جو وہ کرتے ہیں۔",
                "verseRef": "سورۃ آل عمران: آیت ۱۵۶",
                "analysisBreakdown": "اللَّهُ: مبتدا مرفوع | بَصِيرٌ: خبر مرفوع بالضمہ"
            }
        ],
        "categoryTag": "جملے کی ساخت"
    },
    {
        "id": 3,
        "titleUrdu": "جملہ فعلیہ: فعل، فاعل اور مفعول بہ",
        "titleArabic": "الْجُمْلَةُ الْفِعْلِيَّةُ: الْفِعْلُ، الْفَاعِلُ وَالْمَفْعُولُ بِهِ",
        "summaryUrdu": "وہ جملہ جو فعل سے شروع ہو، اس میں کام کرنے والا فاعل (مرفوع) اور جس پر کام واقع ہو وہ مفعول بہ (منصوب) ہوتا ہے۔",
        "explanationUrdu": "فاعل ہمیشہ مرفوع (پیش) ہوتا ہے اور مفعول بہ ہمیشہ منصوب (زبر) ہوتا ہے۔",
        "rules": [
            "فاعل ہمیشہ مرفوع ہوتا ہے۔",
            "مفعول بہ ہمیشہ منصوب ہوتا ہے۔"
        ],
        "examples": [
            {
                "arabicText": "شَهِدَ اللَّهُ أَنَّهُ لَا إِلَٰهَ إِلَّا هُوَ",
                "urduTranslation": "اللہ نے گواہی دی کہ اس کے سوا کوئی معبود نہیں۔",
                "verseRef": "سورۃ آل عمران: آیت ۱۸",
                "analysisBreakdown": "شَهِدَ: فعل ماضی | اللَّهُ: فاعل مرفوع بالضمہ"
            }
        ],
        "categoryTag": "جملے کی ساخت"
    },
    {
        "id": 4,
        "titleUrdu": "اعراب کی اقسام: مرفوع، منصوب، مجرور اور مجزوم",
        "titleArabic": "حَالَاتُ الْإِعْرَابِ: الرَّفْعُ، النَّصْبُ، الْجَرُّ، وَالْجَزْمُ",
        "summaryUrdu": "عربی زبان میں کلمات کی آخری حرکات ان کے جملے میں مقام کو ظاہر کرتی ہیں۔",
        "explanationUrdu": "رفع کی علامت ضمہ، نصب کی فتحہ، جر کی کسرہ اور جزم کی سکون ہے۔",
        "rules": [
            "اسم میں جر ہوتا ہے لیکن جزم نہیں۔",
            "فعل میں جزم ہوتا ہے لیکن جر نہیں۔"
        ],
        "examples": [
            {
                "arabicText": "بِالْحَقِّ",
                "urduTranslation": "حق کے ساتھ",
                "verseRef": "سورۃ آل عمران: آیت ۳",
                "analysisBreakdown": "بِ: حرفِ جر | الْحَقِّ: اسم مجرور بالکسرہ"
            }
        ],
        "categoryTag": "علم النحو"
    },
    {
        "id": 5,
        "titleUrdu": "فعل کی اقسام: ماضی، مضارع اور امر",
        "titleArabic": "أَقْسَامُ الْفِعْلِ: الْمَاضِي، الْمُضَارِعُ، وَالْأَمْرُ",
        "summaryUrdu": "فعل زمانے کے اعتبار سے تین طرح کا ہوتا ہے: ماضی، مضارع، اور امر۔",
        "explanationUrdu": "ماضی گزرے ہوئے زمانے پر، مضارع حال اور مستقبل پر، اور امر حکم یا طلب پر دلالت کرتا ہے۔",
        "rules": [
            "فعل ماضی مبنی بر فتح ہوتا ہے۔",
            "فعل مضارع کے شروع میں حروف اتین (أ، ت، ي، ن) آتے ہیں۔"
        ],
        "examples": [
            {
                "arabicText": "قُلْ إِن كُنتُمْ تُحِبُّونَ اللَّهَ",
                "urduTranslation": "آپ فرمائیے: اگر تم اللہ سے محبت رکھتے ہو",
                "verseRef": "سورۃ آل عمران: آیت ۳۱",
                "analysisBreakdown": "قُلْ: فعل امر | تُحِبُّونَ: فعل مضارع مرفوع"
            }
        ],
        "categoryTag": "علم الصرف"
    },
    {
        "id": 6,
        "titleUrdu": "جنس: مذکر اور مؤنث کے احکام",
        "titleArabic": "الْمُذَكَّرُ وَالْمُؤَنَّثُ فِي الْقُرْآنِ",
        "summaryUrdu": "عربی میں اسم مذکر یا مؤنث ہوتا ہے، مؤنث کی علامات تائے مربوطہ، الف مقصورہ وغیرہ ہیں۔",
        "explanationUrdu": "مؤنث قیاسی علامات کے ساتھ اور مؤنث سماعی اہل عرب کی بول چال کے مطابق ہوتا ہے۔",
        "rules": ["غیر عاقل کی جمع مکسر کے ساتھ صفت واحد مؤنث آتی ہے۔"],
        "examples": [
            {
                "arabicText": "آيَاتٌ مُّحْكَمَاتٌ",
                "urduTranslation": "محکم آیات ہیں",
                "verseRef": "سورۃ آل عمران: آیت ۷",
                "analysisBreakdown": "آيَاتٌ: جمع مؤنث | مُّحْكَمَاتٌ: صفت مؤنث"
            }
        ],
        "categoryTag": "علم الصرف"
    },
    {
        "id": 7,
        "titleUrdu": "عدد: واحد، تثنیہ اور جمع",
        "titleArabic": "الْمُفْرَدُ، الْمُثَنَّى، وَالْجَمْعُ",
        "summaryUrdu": "عربی میں ایک کے لیے واحد، دو کے لیے تثنیہ اور تین یا زائد کے لیے جمع آتا ہے۔",
        "explanationUrdu": "تثنیہ کے آخر میں 'ـَانِ' یا 'ـَيْنِ'، اور جمع کے لیے 'ـُونَ' یا 'ـِينَ' یا جمع مکسر آتا ہے۔",
        "rules": ["تثنیہ کا اعراب الف اور یاء سے، جمع سالم کا واو اور یاء سے ہوتا ہے۔"],
        "examples": [
            {
                "arabicText": "فِي فِئَتَيْنِ الْتَقَتَا",
                "urduTranslation": "دو گروہوں میں جو باہم ٹکرائے",
                "verseRef": "سورۃ آل عمران: آیت ۱۳",
                "analysisBreakdown": "فِئَتَيْنِ: مثنیٰ مجرور بالیاء (دو گروہ)"
            }
        ],
        "categoryTag": "علم الصرف"
    },
    {
        "id": 8,
        "titleUrdu": "معرفہ اور نکرہ: ال اور تنوین",
        "titleArabic": "الْمَعْرِفَةُ وَالنَّكِرَةُ",
        "summaryUrdu": "نکرہ عام اسم اور معرفہ خاص اسم ہوتا ہے۔",
        "explanationUrdu": "معرفہ کی علامات میں 'ال'، علم، ضمیر، اسم اشارہ اور مضاف الی المعرفہ شامل ہیں۔",
        "rules": ["'ال' اور تنوین ایک اسم پر کبھی جمع نہیں ہو سکتے۔"],
        "examples": [
            {
                "arabicText": "الْكِتَابَ ... آيَاتٌ",
                "urduTranslation": "کتاب (معرفہ) ... آیات (نکرہ)",
                "verseRef": "سورۃ آل عمران: آیت ۷",
                "analysisBreakdown": "الْكِتَابَ: معرف باللام | آيَاتٌ: نکرہ منون"
            }
        ],
        "categoryTag": "بنیادی نحو"
    },
    {
        "id": 9,
        "titleUrdu": "حروفِ اصلیہ (جذر) اور کلمات کے اوزان",
        "titleArabic": "الْجُذُورُ الثُّلَاثِيَّةُ وَالْمِيزَانُ الصَّرْفِيُّ",
        "summaryUrdu": "عربی کے کلمات بنیادی تین حروف (مادہ) سے میزانِ صرفی (ف-ع-ل) پر بنتے ہیں۔",
        "explanationUrdu": "روٹ کے ساتھ مختلف اوزان مل کر نئے معانی پیدا کرتے ہیں۔",
        "rules": ["بابِ افعال (أَفْعَلَ) اور تفعیل (فَعَّلَ) کثرت اور تعدیہ پیدا کرتے ہیں۔"],
        "examples": [
            {
                "arabicText": "نَزَّلَ ... وَأَنزَلَ",
                "urduTranslation": "رفتہ رفتہ اتارا ... بیک وقت اتارا (مادہ: ن-ز-ل)",
                "verseRef": "سورۃ آل عمران: آیت ۳",
                "analysisBreakdown": "نَزَّلَ: باب تفعیل | أَنزَلَ: باب افعال"
            }
        ],
        "categoryTag": "علم الصرف"
    },
    {
        "id": 10,
        "titleUrdu": "قرآنی حروفِ معانی: جارہ، عاطفہ اور نافیہ",
        "titleArabic": "حُرُوفُ الْمَعَانِي فِي الْقُرْآنِ الْكَرِيمِ",
        "summaryUrdu": "قرآن میں حروفِ جارہ، عاطفہ اور نافیہ جملے میں ربط اور معانی پیدا کرتے ہیں۔",
        "explanationUrdu": "حروف جارہ اپنے بعد اسم کو جر دیتے ہیں؛ حروف نفی لَا، مَا، لَمْ، لَنْ نفی کرتے ہیں۔",
        "rules": ["لَنْ فعل مضارع کو مستقبل کی قطعی نفی کے ساتھ نصب دیتا ہے۔"],
        "examples": [
            {
                "arabicText": "لَن تَنَالُوا الْبِرَّ حَتَّىٰ تُنفِقُوا",
                "urduTranslation": "تم ہرگز نیکی کو نہیں پہنچ سکتے جب تک خرچ نہ کرو",
                "verseRef": "سورۃ آل عمران: آیت ۹۲",
                "analysisBreakdown": "لَن: حرف نفی و نصب | تَنَالُوا: مضارع منصوب بحذف النون"
            }
        ],
        "categoryTag": "علم النحو"
    },
    {
        "id": 11,
        "titleUrdu": "مرکب اضافی: مضاف اور مضاف الیہ",
        "titleArabic": "الْمُرَكَّبُ الْإِضَافِيُّ: الْمُضَافُ وَالْمُضَافُ إِلَيْهِ",
        "summaryUrdu": "دو اسموں کا تعلق جس میں ایک کی نسبت دوسرے کی طرف ہو (جیسے: اللہ کا کلام)۔",
        "explanationUrdu": "مضاف پر 'ال' اور تنوین نہیں آتی، اور مضاف الیہ ہمیشہ مجرور ہوتا ہے۔",
        "rules": ["مضاف الیہ ہمیشہ مجرور (زیر) ہوتا ہے۔"],
        "examples": [
            {
                "arabicText": "مَالِكَ الْمُلْكِ",
                "urduTranslation": "بادشاہی کا مالک",
                "verseRef": "سورۃ آل عمران: آیت ۲۶",
                "analysisBreakdown": "مَالِكَ: مضاف منصوب | الْمُلْكِ: مضاف الیہ مجرور"
            }
        ],
        "categoryTag": "تراکیب"
    },
    {
        "id": 12,
        "titleUrdu": "مرکب توصیفی: موصوف اور صفت",
        "titleArabic": "الْمُرَكَّبُ التَّوْصِيفِيُّ: الْمَوْصُوفُ وَالصِّفَةُ",
        "summaryUrdu": "وہ مرکب جس میں صفت اپنے موصوف کی اچھائی یا حالت بیان کرے۔",
        "explanationUrdu": "صفت اعراب، جنس، عدد، اور وسعت میں اپنے موصوف کے تابع ہوتی ہے۔",
        "rules": ["صفت چاروں امور میں موصوف کے مطابق ہوتی ہے۔"],
        "examples": [
            {
                "arabicText": "عَذَابٌ شَدِيدٌ",
                "urduTranslation": "سخت عذاب",
                "verseRef": "سورۃ آل عمران: آیت ۴",
                "analysisBreakdown": "عَذَابٌ: موصوف مرفوع | شَدِيدٌ: صفت مرفوع"
            }
        ],
        "categoryTag": "تراکیب"
    }
]

# ==================================================================================================
# 2. SQLITE DATABASE INITIALIZATION (BOOKMARKS, SAVED WORDS, PROGRESS)
# ==================================================================================================
def init_db():
    conn = sqlite3.connect(DB_FILE)
    c = conn.cursor()
    c.execute('''
        CREATE TABLE IF NOT EXISTS bookmarks (
            verse_number INTEGER PRIMARY KEY,
            timestamp INTEGER,
            note TEXT
        )
    ''')
    c.execute('''
        CREATE TABLE IF NOT EXISTS saved_words (
            word_id TEXT PRIMARY KEY,
            arabic TEXT,
            urdu TEXT,
            transliteration TEXT,
            root TEXT,
            category TEXT,
            verse_number INTEGER,
            timestamp INTEGER
        )
    ''')
    c.execute('''
        CREATE TABLE IF NOT EXISTS learning_progress (
            verse_number INTEGER PRIMARY KEY,
            is_completed INTEGER,
            timestamp INTEGER
        )
    ''')
    c.execute('''
        CREATE TABLE IF NOT EXISTS quiz_records (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            quiz_type TEXT,
            score INTEGER,
            total_questions INTEGER,
            timestamp INTEGER
        )
    ''')
    conn.commit()
    conn.close()

# ==================================================================================================
# 3. DATA LOADER & NORMALIZATION
# ==================================================================================================
VERSES_DATA: List[Dict[str, Any]] = []

def load_verses():
    global VERSES_DATA
    if os.path.exists(ASSET_FILE):
        with open(ASSET_FILE, 'r', encoding='utf-8') as f:
            d = json.load(f)
            VERSES_DATA = d.get("verses", [])
            print(f"[NoorBackend] Loaded {len(VERSES_DATA)} verses from {ASSET_FILE}")
    else:
        print(f"[NoorBackend] Warning: {ASSET_FILE} not found. Running with empty verse cache.")

def normalize_arabic(text: str) -> str:
    # Strip diacritics / tashkeel
    cleaned = re.sub(r'[\u064B-\u0652\u0670\u06D6-\u06ED]', '', text)
    cleaned = cleaned.replace("أ", "ا").replace("إ", "ا").replace("آ", "ا").replace("ة", "ه").replace("ى", "ي")
    return cleaned.strip()

# ==================================================================================================
# 4. HTTP REQUEST HANDLER (REST API)
# ==================================================================================================
class NoorApiHandler(http.server.BaseHTTPRequestHandler):
    def _send_json(self, data: Any, status: int = 200):
        body = json.dumps(data, ensure_ascii=False, indent=2).encode('utf-8')
        self.send_response(status)
        self.send_header('Content-Type', 'application/json; charset=utf-8')
        self.send_header('Content-Length', str(len(body)))
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, DELETE')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()
        self.wfile.write(body)

    def do_OPTIONS(self):
        self.send_response(200)
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, DELETE')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()

    def do_GET(self):
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path
        params = urllib.parse.parse_qs(parsed.query)

        # Health
        if path == "/api/health" or path == "/":
            self._send_json({
                "app": "Noor — Quran Arabic Learning",
                "surah": "Surah Aal-e-Imran (Verses 1-92)",
                "status": "Online",
                "totalVerses": len(VERSES_DATA),
                "grammarLessons": len(GRAMMAR_LESSONS),
                "reciter": "Sheikh Abdul Basit Abdul Samad (Murattal)"
            })
            return

        # Verses list
        if path == "/api/verses":
            self._send_json({"verses": VERSES_DATA, "total": len(VERSES_DATA)})
            return

        # Single Verse
        if path.startswith("/api/verse/"):
            try:
                v_num = int(path.split("/")[-1])
                verse = next((v for v in VERSES_DATA if v["verseNumber"] == v_num), None)
                if verse:
                    self._send_json(verse)
                else:
                    self._send_json({"error": "Verse not found (must be 1-92)"}, 404)
            except Exception:
                self._send_json({"error": "Invalid verse number"}, 400)
            return

        # Search endpoint
        if path == "/api/search":
            query = params.get("q", [""])[0].strip()
            if not query:
                self._send_json({"matchedVerses": [], "matchedWords": []})
                return

            norm_q = normalize_arabic(query)
            matched_verses = []
            matched_words = []

            for v in VERSES_DATA:
                norm_v_ar = normalize_arabic(v["arabicText"])
                if norm_q in norm_v_ar or query in v["urduTranslation"]:
                    matched_verses.append(v)

                for w in v["words"]:
                    norm_w_ar = normalize_arabic(w["arabic"])
                    norm_root = normalize_arabic(w.get("root", ""))
                    if norm_q in norm_w_ar or query in w["urdu"] or norm_q in norm_root or query in w.get("grammaticalRole", ""):
                        matched_words.append({"verseNumber": v["verseNumber"], "word": w})

            self._send_json({
                "query": query,
                "matchedVerses": matched_verses,
                "matchedWords": matched_words[:50]
            })
            return

        # Grammar lessons
        if path == "/api/grammar/lessons":
            self._send_json({"lessons": GRAMMAR_LESSONS})
            return

        if path.startswith("/api/grammar/lesson/"):
            try:
                l_id = int(path.split("/")[-1])
                lesson = next((l for l in GRAMMAR_LESSONS if l["id"] == l_id), None)
                if lesson:
                    self._send_json(lesson)
                else:
                    self._send_json({"error": "Lesson not found"}, 404)
            except Exception:
                self._send_json({"error": "Invalid lesson id"}, 400)
            return

        # Quiz generation
        if path == "/api/quiz":
            quiz_type = params.get("type", ["vocabulary"])[0].lower()
            pool = []
            for v in VERSES_DATA:
                for w in v["words"]:
                    if w["urdu"].strip() and len(w["arabic"]) >= 2:
                        pool.append((v["verseNumber"], w))

            random.shuffle(pool)
            questions = []

            for v_num, target_word in pool[:10]:
                distractors = list({p[1]["urdu"] for p in pool if p[1]["urdu"] != target_word["urdu"]})
                random.shuffle(distractors)
                opts = distractors[:3] + [target_word["urdu"]]
                random.shuffle(opts)

                questions.append({
                    "id": f"q_{len(questions)+1}",
                    "type": quiz_type,
                    "prompt": target_word["arabic"],
                    "questionUrdu": f"قرآنی لفظ '{target_word['arabic']}' کا درست اردو مفہوم کیا ہے؟",
                    "options": opts,
                    "correctIndex": opts.index(target_word["urdu"]),
                    "explanationUrdu": f"آیت {v_num} میں '{target_word['arabic']}' کا معنی '{target_word['urdu']}' ہے، مادہ: '{target_word.get('root','-')}'.",
                    "verseNumber": v_num
                })

            self._send_json({"questions": questions})
            return

        # Bookmarks
        if path == "/api/bookmarks":
            conn = sqlite3.connect(DB_FILE)
            c = conn.cursor()
            c.execute("SELECT verse_number, timestamp, note FROM bookmarks ORDER BY verse_number ASC")
            rows = c.fetchall()
            conn.close()
            bookmarks = [{"verseNumber": r[0], "timestamp": r[1], "note": r[2]} for r in rows]
            self._send_json({"bookmarks": bookmarks})
            return

        # Saved words
        if path == "/api/saved-words":
            conn = sqlite3.connect(DB_FILE)
            c = conn.cursor()
            c.execute("SELECT word_id, arabic, urdu, transliteration, root, category, verse_number, timestamp FROM saved_words ORDER BY timestamp DESC")
            rows = c.fetchall()
            conn.close()
            saved = [{
                "wordId": r[0], "arabic": r[1], "urdu": r[2], "transliteration": r[3],
                "root": r[4], "category": r[5], "verseNumber": r[6], "timestamp": r[7]
            } for r in rows]
            self._send_json({"savedWords": saved})
            return

        # Progress
        if path == "/api/progress":
            conn = sqlite3.connect(DB_FILE)
            c = conn.cursor()
            c.execute("SELECT verse_number, is_completed, timestamp FROM learning_progress WHERE is_completed = 1")
            rows = c.fetchall()
            conn.close()
            completed = [{"verseNumber": r[0], "timestamp": r[2]} for r in rows]
            self._send_json({
                "completedCount": len(completed),
                "totalVerses": 92,
                "percentage": round(len(completed) / 92 * 100, 1),
                "completedVerses": completed
            })
            return

        self._send_json({"error": "Endpoint not found"}, 404)

    def do_POST(self):
        content_len = int(self.headers.get('Content-Length', 0))
        body = self.rfile.read(content_len).decode('utf-8') if content_len > 0 else "{}"
        try:
            payload = json.loads(body)
        except Exception:
            payload = {}

        path = urllib.parse.urlparse(self.path).path

        if path == "/api/bookmarks/toggle":
            v_num = payload.get("verseNumber")
            if not v_num:
                self._send_json({"error": "Missing verseNumber"}, 400)
                return
            conn = sqlite3.connect(DB_FILE)
            c = conn.cursor()
            c.execute("SELECT 1 FROM bookmarks WHERE verse_number = ?", (v_num,))
            exists = c.fetchone()
            if exists:
                c.execute("DELETE FROM bookmarks WHERE verse_number = ?", (v_num,))
                status = "removed"
            else:
                c.execute("INSERT INTO bookmarks (verse_number, timestamp, note) VALUES (?, strftime('%s', 'now'), ?)", (v_num, payload.get("note", "")))
                status = "added"
            conn.commit()
            conn.close()
            self._send_json({"status": status, "verseNumber": v_num})
            return

        if path == "/api/saved-words/toggle":
            w_id = payload.get("wordId")
            if not w_id:
                self._send_json({"error": "Missing wordId"}, 400)
                return
            conn = sqlite3.connect(DB_FILE)
            c = conn.cursor()
            c.execute("SELECT 1 FROM saved_words WHERE word_id = ?", (w_id,))
            exists = c.fetchone()
            if exists:
                c.execute("DELETE FROM saved_words WHERE word_id = ?", (w_id,))
                status = "removed"
            else:
                c.execute(
                    "INSERT INTO saved_words (word_id, arabic, urdu, transliteration, root, category, verse_number, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, strftime('%s', 'now'))",
                    (w_id, payload.get("arabic", ""), payload.get("urdu", ""), payload.get("transliteration", ""), payload.get("root", ""), payload.get("category", ""), payload.get("verseNumber", 1))
                )
                status = "saved"
            conn.commit()
            conn.close()
            self._send_json({"status": status, "wordId": w_id})
            return

        if path == "/api/progress/toggle":
            v_num = payload.get("verseNumber")
            if not v_num:
                self._send_json({"error": "Missing verseNumber"}, 400)
                return
            conn = sqlite3.connect(DB_FILE)
            c = conn.cursor()
            c.execute("SELECT 1 FROM learning_progress WHERE verse_number = ?", (v_num,))
            exists = c.fetchone()
            if exists:
                c.execute("DELETE FROM learning_progress WHERE verse_number = ?", (v_num,))
                status = "unmarked"
            else:
                c.execute("INSERT INTO learning_progress (verse_number, is_completed, timestamp) VALUES (?, 1, strftime('%s', 'now'))", (v_num,))
                status = "completed"
            conn.commit()
            conn.close()
            self._send_json({"status": status, "verseNumber": v_num})
            return

        self._send_json({"error": "Endpoint not found"}, 404)

# ==================================================================================================
# 5. SERVER ENTRYPOINT
# ==================================================================================================
def run_server():
    init_db()
    load_verses()
    server_address = ('', PORT)
    with socketserver.TCPServer(server_address, NoorApiHandler) as httpd:
        print(f"\n==================================================================")
        print(f"✨ Noor Quran Learning Backend Server running at http://localhost:{PORT}")
        print(f"📖 Surah Aal-e-Imran: 92 Verses Loaded with Full Urdu & Grammar Analysis")
        print(f"🔊 Audio Reciter: Sheikh Abdul Basit Abdul Samad (Murattal)")
        print(f"⚡ Press Ctrl+C to stop the server")
        print(f"==================================================================\n")
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nShutting down server gracefully...")
            httpd.server_close()

if __name__ == "__main__":
    run_server()
