package com.example.testapplication.ui.gallery.items.sipnswipe

import android.animation.TimeInterpolator
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdUnits
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddAlarm
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.math.MathUtils
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.example.testapplication.ui.gallery.GalleryViewModel
import com.example.testapplication.ui.gallery.items.sipnswipe.models.SpinSwipeItem
import com.example.testapplication.ui.gallery.items.sipnswipe.service.SpinSwipeService
import java.time.Month
import java.time.Year


class SpinSwipeViewModel : ViewModel()
{
    private lateinit var spinSwipeService: SpinSwipeService

    init {
        spinSwipeService = SpinSwipeService()
    }

    private val year = MutableLiveData<Int>().apply {}

    private val month = MutableLiveData<Pair<String, Int>>().apply {}

    val Year: LiveData<Int> get() = year

    val Month: LiveData<Pair<String, Int>> get() = month

    fun GetAllEmoji(): ArrayList<String>
    {
        return spinSwipeService.GetEmojis()
    }

    fun GetAllItem(): List<SpinSwipeItem>
    {
       return spinSwipeService.GetItem()
    }

    fun GetAllCoverColor(): ArrayList<Int>
    {
        return spinSwipeService.GetCoverColor()
    }

    fun GetAllDaysByYearAndMonth(year: Int, month: Int): List<Int>
    {
        return  spinSwipeService.GetDays(year, month)
    }

    fun GetAllMonths(): List<Pair<String, Int>>
    {
        return spinSwipeService.GetMonths()
    }

    fun GetYears(): List<Year>
    {
        return  spinSwipeService.GetYears()
    }
}