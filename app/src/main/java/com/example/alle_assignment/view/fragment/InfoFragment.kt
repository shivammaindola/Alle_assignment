package com.example.alle_assignment.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alle_assignment.R
import com.example.alle_assignment.databinding.FragmentInfoBinding
import com.example.alle_assignment.view.adapter.LabelsAdapter
import com.example.alle_assignment.view.viewmodel.SharedViewModel

class InfoFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: FragmentInfoBinding // Replace with your actual binding class

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.labelsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.imageData.observe(viewLifecycleOwner, Observer { image ->
            binding.tvDescription.text = image.description
            val adapter = LabelsAdapter(image.labels)
            binding.labelsRecyclerView.adapter = adapter
            Glide.with(this)
                .load(image.imagePath)
                .into(binding.imageViewSelected)
        })

        sharedViewModel.isLoading.observe(viewLifecycleOwner, Observer { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        })

        // Bind the ViewModel to the layout
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = sharedViewModel
    }
}
