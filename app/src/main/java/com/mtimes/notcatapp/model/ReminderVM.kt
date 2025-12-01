package com.mtimes.notcatapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mtimes.notcatapp.data.ReminderRepository


data class Reminder(
    val user: String,
    val reminderId: Int,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val repeat: String
)

class ReminderViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    var user by mutableStateOf<UserVM?>(null)
        private set

    var reminders by mutableStateOf<List<Reminder>>(emptyList())
        private set


    fun loadUser(userId: Int) {
        user = repository.getUserById(userId)
    }


    fun saveReminder(reminder: Reminder) {
        repository.insertReminder(reminder)
    }
}


class ReminderViewModelFactory(
    private val repo: ReminderRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            return ReminderViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}