package com.dvach_2ch.a2ch.ui.boards

import androidx.lifecycle.*
import com.dvach_2ch.a2ch.data.Repository
import com.dvach_2ch.a2ch.models.boards.Board
import com.dvach_2ch.a2ch.models.boards.BoardsBase
import com.dvach_2ch.a2ch.models.util.Error
import com.dvach_2ch.a2ch.models.util.WARNING
import com.dvach_2ch.a2ch.util.Event
import com.dvach_2ch.a2ch.util.NO_INTERNET
import com.dvach_2ch.a2ch.util.isNetworkAvailable
import kotlinx.coroutines.launch

class BoardsViewModel(private val repository: Repository) : ViewModel() {
    private val _startCategory = MutableLiveData<Event<String>>()
    val startCategory: LiveData<Event<String>> = _startCategory


    private val _boards = MutableLiveData<List<Board>>()
    val boards: LiveData<List<Board>> = _boards

    private val _error = MutableLiveData<Event<Error>>()
    val error: LiveData<Event<Error>> = _error

    init {
        loadBoards()
    }

    fun loadBoards() = viewModelScope.launch {
        try {
            val boards = repository.loadBoards()
            _boards.value = (getResultList(boards))
            _error.value = null
        } catch (ex: Exception) {
            _error.value = (Event(Error(WARNING, ex.localizedMessage)))
            ex.printStackTrace()
        }
    }

    fun startThreadActivity(id: String) {
        if (isNetworkAvailable()) {
            _startCategory.postValue(Event(id))
        } else {
            _error.value = Event(Error(WARNING, NO_INTERNET))
        }

    }


    private fun getResultList(boards: BoardsBase): ArrayList<Board> {
        val resultList = ArrayList<Board>()
        resultList.add(Board(name = "Разное", isHeader = true))
        resultList.addAll(boards.sundry!!)
        resultList.add(Board(name = "Тематика", isHeader = true))
        resultList.addAll(boards.thematics!!)
        resultList.add(Board(name = "Творчество", isHeader = true))
        resultList.addAll(boards.art!!)
        resultList.add(Board(name = "Политика", isHeader = true))
        resultList.addAll(boards.politics!!)
        resultList.add(Board(name = "Техника и софт", isHeader = true))
        resultList.addAll(boards.it!!)
        resultList.add(Board(name = "Игры", isHeader = true))
        resultList.addAll(boards.games!!)
        resultList.add(Board(name = "Японская культура", isHeader = true))
        resultList.addAll(boards.japanese!!)
        resultList.add(Board(name = "Пользовательские", isHeader = true))
        resultList.addAll(boards.userBoards!!)
        return resultList
    }
}

@Suppress("UNCHECKED_CAST")
class BoardsViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BoardsViewModel(
            repository
        ) as T
    }

}

