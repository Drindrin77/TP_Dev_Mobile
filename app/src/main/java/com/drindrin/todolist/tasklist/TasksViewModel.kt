package com.drindrin.todolist.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class TasksViewModel : ViewModel() {
    private val repository = TasksRepository()
    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    public val taskList: StateFlow<List<Task>> = _taskList

    fun loadTasks() {
        viewModelScope.launch {
            val tasks = repository.loadTasks()
            if (tasks != null) _taskList.value = tasks
        }

    }
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            if(repository.removeTask(task) == true) {
                val oldTask = firstOrNullTask(task)
                if (oldTask != null) _taskList.value = taskList.value - oldTask
            }
        }
    }
    fun addTask(task: Task) {
        viewModelScope.launch {
            val createdTask = repository.createTask(task)
            if (createdTask != null) _taskList.value = taskList.value + createdTask
        }
    }
    fun editTask(oldTask: Task, task: Task) {
        viewModelScope.launch {
            val updatedTask = repository.updateTask(task)
            if(updatedTask != null){
                if (oldTask != null) _taskList.value = taskList.value - oldTask + updatedTask
            }
        }
    }

    fun firstOrNullTask(task: Task): Task?{
        return taskList.value.firstOrNull { it.id == task.id }
    }
}