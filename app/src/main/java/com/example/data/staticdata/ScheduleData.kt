package com.example.data.staticdata

import java.util.Calendar

data class DayScheduleItem(
    val dayOfWeek: Int, // Calendar.SUNDAY, Calendar.MONDAY, etc.
    val dayNameArabic: String,
    val quranSurahName: String,
    val quranSurahId: String,
    val munajatName: String,
    val munajatId: String,
    val dailyWorship: String = "زيارة عاشوراء + مناجاة التائبين",
    val description: String
)

object ScheduleData {
    val weeklySchedule: List<DayScheduleItem> = listOf(
        DayScheduleItem(
            dayOfWeek = Calendar.SATURDAY,
            dayNameArabic = "السبت",
            quranSurahName = "سورة الفتح",
            quranSurahId = "surah_fath",
            munajatName = "مناجاة الشاكين والمحبين",
            munajatId = "munajat_shakin",
            description = "سورة الفتح + مناجاة الشاكين والمحبين"
        ),
        DayScheduleItem(
            dayOfWeek = Calendar.SUNDAY,
            dayNameArabic = "الأحد",
            quranSurahName = "سورة يس",
            quranSurahId = "surah_yasin",
            munajatName = "مناجاة الخائفين والمتوسلين",
            munajatId = "munajat_khaifeen",
            description = "سورة يس + مناجاة الخائفين والمتوسلين"
        ),
        DayScheduleItem(
            dayOfWeek = Calendar.MONDAY,
            dayNameArabic = "الاثنين",
            quranSurahName = "سورة الواقعة",
            quranSurahId = "surah_waqiah",
            munajatName = "مناجاة الراجين والمفتقرين",
            munajatId = "munajat_rajin",
            description = "سورة الواقعة + مناجاة الراجين والمفتقرين"
        ),
        DayScheduleItem(
            dayOfWeek = Calendar.TUESDAY,
            dayNameArabic = "الثلاثاء",
            quranSurahName = "سورة الرحمن",
            quranSurahId = "surah_rahman",
            munajatName = "مناجاة الراغبين والعارفين",
            munajatId = "munajat_raghibeen",
            description = "سورة الرحمن + مناجاة الراغبين والعارفين"
        ),
        DayScheduleItem(
            dayOfWeek = Calendar.WEDNESDAY,
            dayNameArabic = "الأربعاء",
            quranSurahName = "سورة الجن",
            quranSurahId = "surah_jinn",
            munajatName = "مناجاة الشاكرين والذاكرين",
            munajatId = "munajat_shakireen",
            description = "سورة الجن + مناجاة الشاكرين والذاكرين"
        ),
        DayScheduleItem(
            dayOfWeek = Calendar.THURSDAY,
            dayNameArabic = "الخميس",
            quranSurahName = "سورة الملك",
            quranSurahId = "surah_mulk",
            munajatName = "مناجاة المطيعين والمعتصمين",
            munajatId = "munajat_mutieen",
            description = "سورة الملك + مناجاة المطيعين لله والمعتصمين والزاهدين"
        ),
        DayScheduleItem(
            dayOfWeek = Calendar.FRIDAY,
            dayNameArabic = "الجمعة",
            quranSurahName = "سورة الكهف والجمعة",
            quranSurahId = "surah_kahf_full",
            munajatName = "مناجاة المريدين والزاهدين",
            munajatId = "munajat_mureedeen",
            description = "سورة الكهف والجمعة + مناجاة المريدين والزاهدين"
        )
    )

    fun getTodaySchedule(): DayScheduleItem {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        return weeklySchedule.find { it.dayOfWeek == currentDay } ?: weeklySchedule.first()
    }
}
