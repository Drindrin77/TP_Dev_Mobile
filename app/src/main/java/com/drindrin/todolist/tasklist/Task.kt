package com.drindrin.todolist.tasklist
import kotlinx.serialization.SerialName
import java.io.Serializable

@kotlinx.serialization.Serializable
data class Task (
    @SerialName("id")
    var id: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String = ""
): Serializable
{

}