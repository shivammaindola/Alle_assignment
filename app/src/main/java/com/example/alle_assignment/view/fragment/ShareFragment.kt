package com.example.alle_assignment.view.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alle_assignment.databinding.FragmentShareBinding
import com.example.alle_assignment.view.adapter.ImageAdapter
import com.example.alle_assignment.view.viewmodel.SharedViewModel
class ShareFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: FragmentShareBinding // Replace with your actual binding class

    private var recyclerViewState: Parcelable? = null
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.galleryImages.observe(viewLifecycleOwner, Observer { images ->
            if (images.isNotEmpty()) {
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.imageView.visibility = View.VISIBLE
                // Set the first image by default
                setImage(images[images.size - 1])
                binding.recyclerView.adapter = ImageAdapter(images) { imagePath ->
                    setImage(imagePath)
                }
            }
        })
    }

    private fun setImage(imagePath: String) {
        //load the image
        Glide.with(this)
            .load(imagePath)
            .into(binding.imageView)

        sharedViewModel.fetchData(imagePath)

    }

    override fun onResume() {
        super.onResume()

        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            sharedViewModel.syncGalleryImages()
            binding.recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
            // Restore the selected item position
            (binding.recyclerView.adapter as? ImageAdapter)?.let { adapter ->
                adapter.selectedItemPosition = selectedPosition
                adapter.notifyDataSetChanged()
            }

        } else {
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.imageView.visibility = View.VISIBLE
            Toast.makeText(context,"Please provide the permissions",Toast.LENGTH_LONG).show()

        }

    }

    override fun onPause() {
        super.onPause()
        // Save the RecyclerView state
        recyclerViewState = binding.recyclerView.layoutManager?.onSaveInstanceState()

        // Check if the adapter is not null before casting
        if (binding.recyclerView.adapter is ImageAdapter) {
            selectedPosition = (binding.recyclerView.adapter as ImageAdapter).selectedItemPosition
        }
    }
}