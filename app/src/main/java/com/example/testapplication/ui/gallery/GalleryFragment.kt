package com.example.testapplication.ui.gallery

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.ArraySet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.R
import com.example.testapplication.databinding.FragmentGalleryBinding
import com.google.android.material.button.MaterialButton

class GalleryFragment : Fragment(R.layout.fragment_gallery)
{
    private lateinit var viewModel: GalleryViewModel
    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val navController = androidx.navigation.findNavController(R.id.nav_host_fragment_content_gallery)

        val textView: TextView = binding.textGallery
        //val gridView: GridView = binding.gridGallery
        val recyclerView: RecyclerView = binding.recyclerviewGallery

        val navController = findNavController()

        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        viewModel.navigateTo.observe(viewLifecycleOwner, Observer { fragmentId ->
            fragmentId?.let {
                if (navController.currentDestination?.id == R.id.nav_gallery) {
                    navController.navigate(it)
                }
                viewModel.onNavigationDone()
            }
        })

        //gridView.adapter = GridViewAdapter(galleryViewModel.getItem())
        recyclerView.adapter = RecycleViewAdapter(viewModel)
        recyclerView.layoutManager = GridLayoutManager(context, 2) // 2 columns
        val spacingVerticalInPixels = resources.getDimensionPixelSize(androidx.appcompat.R.dimen.abc_button_inset_vertical_material)
        val spacingHorizontalInPixels = resources.getDimensionPixelSize(androidx.appcompat.R.dimen.abc_button_inset_horizontal_material)
        recyclerView.addItemDecoration(RecycleViewItemDecoration(spacingVerticalInPixels, spacingHorizontalInPixels))

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class GridViewAdapter(private val items: List<GalleryItem>) : BaseAdapter()
{

    override fun getCount(): Int {
        return items.size // replace with your actual item count
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val view = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.gallery_imagebutton, parent, false)
        var button: Button = view.findViewById<Button>(R.id.gallery_button)

        if (view == null)
        {
            val button =  Button(parent.context).apply ()
            {
                layoutParams = ViewGroup.LayoutParams(
                    //600,
                    //450
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setOnClickListener {
                    // Handle button click
                }
            }
        }
        //else
        //{
        //    convertView as Button
        //}

        val item = getItem(position) as GalleryItem
        button.text = item.name
        item.icon?.let {
            button.setCompoundDrawablesWithIntrinsicBounds(0, it, 0, 0)
        }

        return button
    }
}

class RecycleViewAdapter(private val viewModel: GalleryViewModel) : RecyclerView.Adapter<RecycleViewAdapter.ButtonViewHolder>()
{
    private val items = viewModel.getItem()

    class ButtonViewHolder(private val viewModel: GalleryViewModel, itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val button: MaterialButton = itemView.findViewById(R.id.gallery_button)

        fun bind(item: GalleryItem) {
            button.text = item.name
            item.icon?.let {
                button.setCompoundDrawablesWithIntrinsicBounds(0, it, 0, 0)
            }
            button.setOnClickListener {
                viewModel.onButtonClicked(item.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_imagebutton, parent, false)

        //This is the way that we can style a recycle view inside the adapter
        //val layoutParams = view.layoutParams as RecyclerView.LayoutParams
        //layoutParams.leftMargin = 25
        //layoutParams.rightMargin = 25
        //view.layoutParams = layoutParams

        return ButtonViewHolder(viewModel, view)
    }


    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        //holder.button.text = item.name
        //item.icon?.let()
        //{
        //    holder.button.setCompoundDrawablesWithIntrinsicBounds(0, it, 0, 0)
        //}
    }
}

class RecycleViewItemDecoration(private val verticalSpaceHeight: Int, private val horizontalSpaceWidth: Int) : RecyclerView.ItemDecoration()
{

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)
    {
        outRect.left = horizontalSpaceWidth
        outRect.right = horizontalSpaceWidth
        //outRect.bottom = verticalSpaceHeight
        //outRect.top = verticalSpaceHeight

        // Add top margin only for the first item to avoid double space between items
        //if (parent.getChildAdapterPosition(view) == 0) {
        //    outRect.top = verticalSpaceHeight
        //}

        // Add top margin only for the first item in each column to avoid double space between items
        //val position = parent.getChildAdapterPosition(view)
        //if (position >= 2) {
        //    val column = position % 2
        //    if (column == 0) {
        //        outRect.left = verticalSpaceHeight
        //    } else {
        //        outRect.right = verticalSpaceHeight
        //    }
        //}
    }
}