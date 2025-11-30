package com.mtimes.notcatapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mtimes.notcatapp.data.ListEntity
import com.mtimes.notcatapp.data.ListItemEntity
import com.mtimes.notcatapp.data.ListsRepository


class ListsViewModel(private val repository: ListsRepository) : ViewModel() {

    var lists by mutableStateOf<List<ListEntity>>(emptyList())
        private set

    var items by mutableStateOf<List<ListItemEntity>>(emptyList())
        private set

    var currentListName by mutableStateOf("")
        private set

    fun loadLists(UserId: Int) {
        lists = repository.getListsForUser(UserId)
    }

    fun addList(name: String, UserId: Int) {
        repository.createList(name,UserId)
        loadLists(UserId) // refresh
    }

    fun loadItems(listId: Int) {
        items = repository.getItemsForList(listId)
    }

    fun addItem(listId: Int, content: String) {
        repository.addItem(listId, content)
        loadItems(listId)  // refresh items
    }

    //marca si un item esta completado o no
    fun updateItemStatus(itemId: Int, done: Boolean, listId: Int) {
        repository.updateItemChecked(itemId, done)
        loadItems(listId)  // refresh items
    }

    fun loadListName(listId: Int) {
        currentListName = repository.getListName(listId)
    }
}

class ListsViewModelFactory(
    private val repository: ListsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListsViewModel::class.java)) {
            return ListsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}