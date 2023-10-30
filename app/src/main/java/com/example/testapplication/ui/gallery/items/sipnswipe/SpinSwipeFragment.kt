package com.example.testapplication.ui.gallery.items.sipnswipe

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.math.MathUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.R
import com.example.testapplication.databinding.FragmentSpinSwipeBinding
import com.example.testapplication.ui.gallery.items.sipnswipe.helper.SwipeSpinnerHelper
import java.time.Month
import java.time.Year
import kotlin.properties.Delegates

class SpinSwipeFragment : Fragment(R.layout.fragment_spin_swipe)
{
    private lateinit var viewModel: SpinSwipeViewModel
    private lateinit var swipeSpinnerHelper: SwipeSpinnerHelper
    private var _binding: FragmentSpinSwipeBinding? = null
    private val binding get() =  _binding!!

    private var selectedYear by Delegates.notNull<Int>()
    private var selectedMonth by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SpinSwipeViewModel::class.java)

        selectedYear = 2023
        selectedMonth = 1

        val emojiData: List<String> = viewModel.GetAllEmoji()
        val monthDataByLetter: List<String> = viewModel.GetAllMonths().map { it.first }
        val monthDataByNumber: List<Int> = viewModel.GetAllMonths().map { it.second }
        val yearData: List<String> = viewModel.GetYears().map{ it.value.toString() }
        var dateData: List<String> = viewModel.GetAllDaysByYearAndMonth(selectedYear, selectedMonth).map { it.toString() }

        _binding = FragmentSpinSwipeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val coverView: RecyclerView = binding.coverRecyclerviewSpinSwipe

        val dateView: RecyclerView = binding.dateRecyclerviewSpinSwipe
        val monthView: RecyclerView = binding.monthRecyclerviewSpinSwipe
        val yearView: RecyclerView = binding.yearRecyclerviewSpinSwipe
        val emojiView: RecyclerView = binding.emojiRecyclerviewSpinSwipe

        initSnapperAndAesthetics(coverView, dateView, monthView, yearView, emojiView)

        coverView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        dateView.layoutManager = LinearLayoutManager(context)
        monthView.layoutManager = LinearLayoutManager(context)
        yearView.layoutManager = LinearLayoutManager(context)
        emojiView.layoutManager = LinearLayoutManager(context)

        viewModel.Year.observe(viewLifecycleOwner, Observer { year->
            year?.let()
            {
            }
        })

        viewModel.Month.observe(viewLifecycleOwner)
        {
        }

        yearView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = yearView.layoutManager as LinearLayoutManager
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                // Do something with firstVisiblePosition and lastVisiblePosition
                if(firstVisiblePosition != lastVisiblePosition)
                {
                    selectedYear = yearData[firstVisiblePosition].toInt()
                    dateData = viewModel.GetAllDaysByYearAndMonth(selectedYear, selectedMonth).map { it.toString() }
                    (dateView.adapter as DataRecyclerViewAdapter).updateData(dateData)
                }
            }
        })

        monthView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = monthView.layoutManager as LinearLayoutManager
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                // Do something with firstVisiblePosition and lastVisiblePosition
                if(firstVisiblePosition != lastVisiblePosition)
                {
                    selectedMonth = monthDataByNumber[firstVisiblePosition].toInt()
                    dateData = viewModel.GetAllDaysByYearAndMonth(selectedYear, selectedMonth).map { it.toString() }
                    (dateView.adapter as DataRecyclerViewAdapter).updateData(dateData)
                }
            }
        })

        coverView.adapter = CoverRecyclerViewAdapter(viewModel)

        yearView.adapter = DataRecyclerViewAdapter(yearData)
        monthView.adapter = DataRecyclerViewAdapter(monthDataByLetter)
        dateView.adapter = DataRecyclerViewAdapter(dateData)
        emojiView.adapter = DataRecyclerViewAdapter(emojiData)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ItemDrawable(private val context: Context,private val swipeSpinnerHelper: SwipeSpinnerHelper) {
        var gradientDrawable: GradientDrawable
        var indicators: LayerDrawable
        var finalDrawable: LayerDrawable
        fun resetIndicators() {
            indicators.getDrawable(0).alpha = INIT_ALPHA
            indicators.getDrawable(1).alpha = INIT_ALPHA
            indicators.invalidateSelf()
        }

        init {
            val view: View = swipeSpinnerHelper.recyclerView
            val drawables = arrayOfNulls<Drawable>(2)
            gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(-0xcccccd)
            gradientDrawable.cornerRadius = (view.height / 2).toFloat()
            drawables[0] = gradientDrawable
            indicators = AppCompatResources.getDrawable(context,
                                                        if (swipeSpinnerHelper.isVertical)
                                                            R.drawable.arrows
                                                        else
                                                            R.drawable.arrows_horizontal)!!.mutate() as LayerDrawable
            drawables[1] = indicators
            finalDrawable = LayerDrawable(drawables)
            view.background = finalDrawable
            resetIndicators()
        }

        companion object {
            const val INIT_ALPHA = 255 / 4
        }
    }

    private fun initSnapperAndAesthetics(vararg recyclerViews: RecyclerView) {
        for (recyclerView in recyclerViews) {
            recyclerView.post {
                val swipeSpinnerHelper = SwipeSpinnerHelper.bindRecyclerView(recyclerView)
                val itemDrawable: ItemDrawable = ItemDrawable(requireContext(), swipeSpinnerHelper)
                val scrollCallbacks: SwipeSpinnerHelper.ScrollCallbacks = object : SwipeSpinnerHelper.ScrollCallbacks
                {
                    override fun onScrolled(directedInterpolationFraction: Float) {
                        val otherDrawable: Drawable
                        val currentDirection: Drawable
                        otherDrawable =
                            if (directedInterpolationFraction > 0) itemDrawable.indicators.getDrawable(
                                0
                            ) else itemDrawable.indicators.getDrawable(
                                1
                            )
                        currentDirection =
                            if (directedInterpolationFraction > 0) itemDrawable.indicators.getDrawable(
                                1
                            ) else itemDrawable.indicators.getDrawable(
                                0
                            )
                        val currentDirectionAlpha = 255
                        currentDirection.alpha = currentDirectionAlpha
                        otherDrawable.alpha = MathUtils.clamp(255 - currentDirectionAlpha, ItemDrawable.INIT_ALPHA, 255)
                        itemDrawable.indicators.invalidateSelf()
                    }

                    override fun onResetScroll() {
                        itemDrawable.resetIndicators()
                    }
                }
                swipeSpinnerHelper.setScrollCallbacks(scrollCallbacks)
            }
        }
    }
}

class DataRecyclerViewAdapter(private var ItemList: List<String>) : RecyclerView.Adapter<DataRecyclerViewAdapter.TextViewHolder>()
{
    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById<TextView>(R.id.text_view_spin_swipe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.text_view_spin_swipe, parent, false))
    }

    override fun getItemCount() = ItemList.size

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.textView.text = ItemList[position]
    }

    fun updateData(newData: List<String>) {
        this.ItemList = newData
        notifyDataSetChanged()
    }
}

private fun getEmojisAdapter(data: List<String>): DataRecyclerViewAdapter?
{
    return DataRecyclerViewAdapter(data)
}

private fun getDateAdapter(data: List<String>): DataRecyclerViewAdapter?
{
    return DataRecyclerViewAdapter(data)
}


private fun getMonthsAdapter(data: List<String>): DataRecyclerViewAdapter?
{
    return DataRecyclerViewAdapter(data)
}

private fun getYearsAdapter(data: List<String>): DataRecyclerViewAdapter?
{
    return DataRecyclerViewAdapter(data)
}

class CoverRecyclerViewAdapter(private val viewModel: SpinSwipeViewModel ) : RecyclerView.Adapter<CoverRecyclerViewAdapter.ImageViewHolder>()
{
    private val items = viewModel.GetAllCoverColor()

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconImageView: ImageFilterView = itemView.findViewById(R.id.image_view_spin_swipe)

        fun bind(item: Int)
        {
            iconImageView.setColorFilter(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_view_spin_swipe, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
}