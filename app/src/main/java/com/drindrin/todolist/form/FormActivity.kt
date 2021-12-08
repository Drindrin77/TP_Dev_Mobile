package com.drindrin.todolist.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.drindrin.todolist.R
import com.drindrin.todolist.tasklist.Task
import java.util.*

class FormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val task = intent.getSerializableExtra("task") as? Task
        val title = findViewById<EditText>(R.id.title_text)
        val description = findViewById<EditText>(R.id.description_text)

        title.setText(task?.title)
        description.setText(task?.description)

        val validate_button = findViewById<Button>(R.id.validate_button)
        validate_button.setOnClickListener {

            val newTask = Task(id = task?.id?:UUID.randomUUID().toString(), title=title.text.toString(), description = description.text.toString())
            intent.putExtra("task", newTask)
            setResult(RESULT_OK, intent)
            finish()

        }

    }


}