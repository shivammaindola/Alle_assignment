package com.example.alle_assignment.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alle_assignment.R

class LabelsAdapter(private val labels: MutableList<String>) : RecyclerView.Adapter<LabelsAdapter.LabelViewHolder>() {

    class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelTextView: TextView = itemView.findViewById(R.id.collection_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.labels, parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.labelTextView.text = labels[position]
    }

    override fun getItemCount() = labels.size
}