package com.grig.mytraining.ui.home.notes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.grig.mytraining.DateBase.DBHelper
import com.grig.mytraining.MyHelper
import com.grig.mytraining.R
import com.grig.mytraining.ui.home.notes.NotesAdapter.OnNoteClickListener
import kotlinx.android.synthetic.main.activity_notes.*

class NotesActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        showNotes()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        findViewById<View>(R.id.goBackButtonFromNotes).setOnClickListener { onBackPressed() }
        findViewById<View>(R.id.plusNote).setOnClickListener {
            val intent = Intent(this@NotesActivity, EditNoteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showNotes() {
        val notes = MyHelper.MyDBHelper.getNotes()
        if (notes.isEmpty())
            findViewById<View>(R.id.tvEmptyNotes).visibility = View.VISIBLE
        else findViewById<View>(R.id.tvEmptyNotes).visibility = View.INVISIBLE

        recyclerViewNotes!!.adapter = NotesAdapter(notes, object : OnNoteClickListener {
            override fun onNoteClick(note: Note) {
                val intent = Intent(this@NotesActivity, EditNoteActivity::class.java)
                intent.putExtra("title", note.title)
                intent.putExtra("content", note.content)
                startActivity(intent)
            }

            override fun onDeleteNoteClick(note: Note) {
                MyHelper.MyDBHelper.getDatabase().delete(
                    DBHelper.TABLE_NOTES,
                    "title = ?", arrayOf(note.title)
                )
                showNotes()
            }
        })
    }
}