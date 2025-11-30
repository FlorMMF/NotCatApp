package com.mtimes.notcatapp.data


data class ListEntity(
    val id: Int,
    val userId: Int,
    val name: String
)

data class ListItemEntity(
    val id: Int,
    val listId: Int,
    val text: String,
    val checked: Boolean
)

class ListsRepository(private val db: UserDB) {


    fun createList( name: String, userId: Int): Long {
        return db.createList(name, userId)
    }

    fun getListsForUser(userId: Int): List<ListEntity> {
        return db.getAllLists(userId)
    }

    fun addItem(listId: Int, text: String) {
        db.addItemToList(listId, text)
    }

    fun getItemsForList(listId: Int): List<ListItemEntity> {
        return db.getItemsForList(listId)
    }

    fun updateItemChecked(id: Int, checked: Boolean){
        db.updateItemDone(id, checked)
    }

    fun getListName(listId: Int): String {
        return db.getListName(listId)
    }


}