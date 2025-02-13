package com.example.votersmanagementsystem.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.votersmanagementsystem.R
import com.example.votersmanagementsystem.model.Voter

class VoterAdapter(private var voters: List<Voter>) : RecyclerView.Adapter<VoterAdapter.VoterViewHolder>() {

    inner class VoterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.voterName)
        val ageTextView: TextView = view.findViewById(R.id.voterAge)
        val registeredTextView: TextView = view.findViewById(R.id.voterRegistered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.voter_card, parent, false)
        return VoterViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoterViewHolder, position: Int) {
        val voter = voters[position]
        holder.nameTextView.text = "Name: ${voter.name}"
        holder.ageTextView.text = "Age: ${voter.age}"
        holder.registeredTextView.text = "Registered: ${if (voter.registered) "Yes" else "No"}"
    }

    override fun getItemCount(): Int = voters.size

    fun updateList(newVoters: List<Voter>) {
        voters = newVoters
        notifyDataSetChanged()
    }
}
