package com.example.testapplication.ui.gallery

import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testapplication.R

class GalleryViewModel : ViewModel()
{
    private val _navigateTo = MutableLiveData<Int?>()
    val navigateTo: LiveData<Int?> get() = _navigateTo

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> get() = _text

    fun onButtonClicked(fragmentId: Int) {
        _navigateTo.value = fragmentId
    }

    fun onNavigationDone() {
        _navigateTo.value = null
    }

    fun getItem() : List<GalleryItem> = listOf(
            //GalleryItem(id = null, name = "Custom Shape", icon = R.drawable.ic_gallery_custom_shape_foreground),
            //GalleryItem(id = null, name = "Load Image Change", icon = R.drawable.ic_gallery_load_image_change_foreground),
            GalleryItem(id = R.id.gallery_spin_swipe_fragment, name = "Spin Swipe", icon = R.drawable.ic_gallery_spin_swpie_foreground),
            //GalleryItem(id = null, name = "Star Rating", icon = R.drawable.ic_gallery_star_rating_foreground),
            //GalleryItem(id = null, name = "Wave Navigation", icon = R.drawable.ic_gallery_wave_navigation_foreground)
    )


}

data class GalleryItem(
    val id: Int,
    val name: String,
    val icon: Int?
)

