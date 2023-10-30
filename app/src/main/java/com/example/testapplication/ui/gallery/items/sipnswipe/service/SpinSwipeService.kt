package com.example.testapplication.ui.gallery.items.sipnswipe.service

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdUnits
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddAlarm
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AddCard
import androidx.core.text.util.LocalePreferences.FirstDayOfWeek.Days
import com.example.testapplication.ui.gallery.items.sipnswipe.models.SpinSwipeItem
import java.time.Month
import java.time.Year

class SpinSwipeService {
    fun GetItem() : List<SpinSwipeItem> = listOf(
        SpinSwipeItem(id = 1, name = "Custom Shape", icon = Icons.Default.AdUnits),
        SpinSwipeItem(id = 2, name = "Load Image Change", icon = Icons.Default.AddAlarm),
        SpinSwipeItem(id = 3, name = "Photo", icon = Icons.Default.AddAPhoto),
        SpinSwipeItem(id = 4, name = "Star Rating", icon = Icons.Default.AddAlert),
        SpinSwipeItem(id = 5, name = "Wave Navigation", icon = Icons.Default.AddCard)
    )

    fun GetCoverColor() : ArrayList<Int> = ArrayList(
        mutableListOf(-0x2aff07, -0xe8bc, -0x9ae001,-0xd68601,
                      -0xbf3b01, -0xe70001, -0xff198a, -0x8900fd,
                      -0x100, -0x6f00, -0xc300
        )
    )

    fun GetEmojis(): ArrayList<String> = ArrayList(
        mutableListOf( "😀", "😁", "😂", "🤣", "😃", "😄", "😅", "😆", "😉", "😊",
                       "😋", "😎", "😍", "😘", "🥰", "😗", "😙", "😚", "☺️", "🙂",
                       "🤗", "🤩", "🤔", "🤨", "😐", "😑", "😶", "🙄", "😏", "😣",
                       "😥", "😮", "🤐", "😯", "😪", "😫", "😴", "😌", "😛", "😜",
                       "😝", "🤤", "😒", "😓", "😔", "😕", "🙃", "🤑", "😲", "☹️",
                       "🙁", "😖", "😞", "😟", "😤", "😢", "😭", "😦", "😧", "😨",
                       "😩", "🤯", "😬", "😰", "😱", "🥵", "🥶", "😳", "🤪", "😵",
                       "😡", "😠", "🤬", "😷", "🤒", "🤕", "🤢", "🤮", "🤧", "😇", "🤠"
        )
    )

    fun GetDays(year: Int, month: Int): List<Int>
    {
        val daysInMonth =
            when (month)
            {
                4, 6, 9, 11 -> 30
                2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29
                     else 28
                else -> 31
            }
        return (1..daysInMonth).toList()
    }

    fun GetMonths(): List<Pair<String,Int>>
    {
        return Month.values().map { Pair(it.name, it.value) }.toList()
    }

    fun GetYears(): List<Year> {
        return (1940..2023).map { Year.of(it) }
    }
}