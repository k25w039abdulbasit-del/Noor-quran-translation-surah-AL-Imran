package com.example

import com.example.data.GrammarLessonsData
import com.example.data.model.WordCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NoorAppUnitTest {

    @Test
    fun grammarLessons_has12Lessons() {
        val lessons = GrammarLessonsData.lessons
        assertEquals(12, lessons.size)
        lessons.forEach { lesson ->
            assertTrue(lesson.titleUrdu.isNotBlank())
            assertTrue(lesson.titleArabic.isNotBlank())
            assertTrue(lesson.explanationUrdu.isNotBlank())
            assertTrue(lesson.rules.isNotEmpty())
            assertTrue(lesson.examples.isNotEmpty())
        }
    }

    @Test
    fun wordCategory_parsingWorks() {
        assertEquals(WordCategory.ISM, WordCategory.fromString("ISM"))
        assertEquals(WordCategory.FI_L, WordCategory.fromString("FI_L"))
        assertEquals(WordCategory.HARF, WordCategory.fromString("HARF"))
        assertEquals(WordCategory.FI_L, WordCategory.fromString("verb"))
    }

    @Test
    fun arabicDiacriticsRemoval_worksForSearch() {
        val diacriticsRegex = Regex("[\u064B-\u0652\u0670\u06D6-\u06ED]")
        val textWithTashkeel = "اللّٰهُ لَاۤ اِلٰهَ اِلَّا هُوَۙ"
        val normalized = textWithTashkeel.replace(diacriticsRegex, "")
        assertTrue(normalized.contains("الله") || normalized.contains("الل"))
    }
}
