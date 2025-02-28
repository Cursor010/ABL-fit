package com.grig.mytraining.ui.home.notes

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.os.Bundle
import com.grig.mytraining.R
import android.widget.Toast
import android.view.View.OnFocusChangeListener
import com.grig.mytraining.MyHelper
import com.grig.mytraining.ui.home.notes.EditNoteActivity
import android.content.ContentValues
import android.view.View
import com.grig.mytraining.DateBase.DBHelper
import kotlinx.android.synthetic.main.activity_edit_note.*

class EditNoteActivity : AppCompatActivity() {
    var title: String? = null
    var content: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        goBackButtonFromEditNotes.setOnClickListener { onBackPressed() }

        applyChangeNote.setOnClickListener { if (editTextTitleNote.text.isBlank()
            && editTextTitleNote.hint == "Заголовок") {
            Toast.makeText(this@EditNoteActivity, "Введите название", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
            if (title == null) {
                // есть null то значит никаких аргументов не передавалось следовалтельно это не измение а добавление
                if (!saveNoteToDB(
                        Note(
                            editTextTitleNote.text.toString(),  // проверяем сохранилась ли заметка
                            editTextContentNote.text.toString()
                        )
                    )
                ) return@setOnClickListener
            } else
                updateNoteInDB()
            Toast.makeText(this, "Сохранено!", Toast.LENGTH_SHORT).show()
        }

        title = intent.getStringExtra("title")
        if (title != null) {
            content = intent.getStringExtra("content")
            editTextTitleNote.hint = title
            editTextContentNote.setText(content)
        }
        editTextTitleNote.setOnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus && editTextTitleNote.hint.toString() != "Заголовок"
            ) editTextTitleNote.setText(editTextTitleNote.hint.toString())
        }
    }

    override fun onDestroy() {
        updateNoteInDB()
        super.onDestroy()
    }

    private fun saveNoteToDB(note: Note): Boolean {
        if (isTitleInDB(note.title)) return false
        MyHelper.MyDBHelper.insertNote(note)
        return true
    }

    private fun updateNoteInDB() {
        if (title != null) if (editTextTitleNote!!.text.toString().isEmpty()) updateNoteInDB(
            Note(
                editTextTitleNote!!.hint.toString(),
                editTextContentNote!!.text.toString()
            ), title!!
        ) else updateNoteInDB(
            Note(
                editTextTitleNote!!.text.toString(),
                editTextContentNote!!.text.toString()
            ), title!!
        )
    }

    private fun isTitleInDB(title: String): Boolean {
        for (noteDB in MyHelper.MyDBHelper.getNotes()) {
            if (noteDB.title == title) {
                Toast.makeText(this, "Такой заголовок уже есть!", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return false
    }

    companion object {
        private fun updateNoteInDB(note: Note, oldTitle: String) {
            val contentValues = ContentValues()
            contentValues.put(DBHelper.NOTES_KEY_TITLE, note.title)
            contentValues.put(DBHelper.NOTES_KEY_CONTENT, note.content)
            MyHelper.MyDBHelper.getDatabase().update(
                DBHelper.TABLE_NOTES, contentValues,
                "title = ?", arrayOf(oldTitle)
            )
        }
    }
}