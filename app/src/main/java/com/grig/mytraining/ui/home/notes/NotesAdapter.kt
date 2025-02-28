package com.grig.mytraining.ui.home.notes

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.grig.mytraining.R
import android.widget.TextView
import android.widget.ImageButton

class NotesAdapter
    (private val notes: List<Note>, private val onNoteClickListener: OnNoteClickListener) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    interface OnNoteClickListener {
        fun onNoteClick(note: Note)
        fun onDeleteNoteClick(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_notes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        holder.titleView.text = note.title
        holder.contentView.text = note.content
        holder.itemView.setOnClickListener { onNoteClickListener.onNoteClick(note) }
        holder.deleteNoteView.setOnClickListener { onNoteClickListener.onDeleteNoteClick(note) }
    }

    override fun getItemCount(): Int = notes.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView
        val contentView: TextView
        val deleteNoteView: ImageButton

        init {
            titleView = view.findViewById(R.id.titleNote)
            contentView = view.findViewById(R.id.contentNote)
            deleteNoteView = view.findViewById(R.id.deleteNote)
        }
    }
}