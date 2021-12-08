package com.drindrin.todolist.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.drindrin.todolist.R
import com.drindrin.todolist.databinding.ActivityMainBinding
import com.drindrin.todolist.databinding.FragmentTaskListBinding
import kotlinx.coroutines.flow.collect

import com.drindrin.todolist.form.FormActivity
import com.drindrin.todolist.network.Api
import com.drindrin.todolist.userinfo.UserInfoActivity
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {
    private val taskListAdapter: TaskListAdapter = createAdapter()
    private val viewModel: TasksViewModel by viewModels()
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    val formLauncher_add = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as? Task
        if(task != null){
            viewModel.addTask(task)
        }
    }

    val userPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

    }

    val formLauncher_edit = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as? Task
        if(task != null){
            val existingTask = viewModel.firstOrNullTask(task)
            if(existingTask != null){
                viewModel.editTask(existingTask, task)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = taskListAdapter

        binding.addTaskButton.setOnClickListener{
            val intent = Intent(activity, FormActivity::class.java)
            formLauncher_add.launch(intent)
        }

        binding.userPhoto.setOnClickListener{
            val intent = Intent(activity, UserInfoActivity::class.java)
            userPhotoLauncher.launch(intent)
        }

        lifecycleScope.launch{
            viewModel.taskList.collect { newList ->
                taskListAdapter.submitList(newList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val userInfo = Api.userWebService.getInfo().body()!!
            if (binding.userInfoTextView != null) {
                binding.userInfoTextView.text = "Bienvenue ${userInfo.firstName} ${userInfo.lastName}"
            }
            binding.userPhoto.load("https://goo.gl/gEgYUd"){
                transformations(CircleCropTransformation())
            }
            viewModel.loadTasks()
        }

    }

    private fun createAdapter(): TaskListAdapter {
        val adapterListener = object : TaskListListener {
            override fun onClickDelete(task: Task) {
                viewModel.deleteTask(task)
            }

            override fun onClickEdit(task: Task) {
                val intent = Intent(activity, FormActivity::class.java)
                intent.putExtra("task", task)
                formLauncher_edit.launch(intent)
            }

        }
        return TaskListAdapter(adapterListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
