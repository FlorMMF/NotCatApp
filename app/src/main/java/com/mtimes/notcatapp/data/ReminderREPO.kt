package com.mtimes.notcatapp.data

import android.content.Context
import com.mtimes.notcatapp.model.Reminder
import com.mtimes.notcatapp.model.UserVM

class ReminderRepository(
    private val dbHelper: UserDB,
    private val context: Context,) {

    fun getUserById(id: Int): UserVM? {
        return dbHelper.getUserById(id)
    }

    fun insertReminder(reminder: Reminder): Long {
        return dbHelper.addReminder(
            getUserById(reminder.userId).toString(),reminder.
            title, reminder.description,
            reminder.date,
            reminder.time,
            reminder.repeat,
            context)
    }

}