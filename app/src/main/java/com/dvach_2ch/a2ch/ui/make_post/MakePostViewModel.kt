package com.dvach_2ch.a2ch.ui.make_post

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.*
import com.dvach_2ch.a2ch.data.Repository
import com.dvach_2ch.a2ch.models.util.Error
import com.dvach_2ch.a2ch.models.util.WARNING
import com.dvach_2ch.a2ch.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendPostViewModel(private val repository: Repository) : ViewModel() {
    var comment = ""
    var board = ""
    var thread = ""
    var username = repository.loadUsername()
    private var captchaKey = ""
    val captchaResponse = MutableLiveData("")

    val dataLoading = MutableLiveData<Boolean>()

    val namesEnabled = MutableLiveData<Boolean>()

    private val _openCaptchaDialog = MutableLiveData<Event<String>>()
    val openCaptchaDialog: LiveData<Event<String>> get() = _openCaptchaDialog

    private val _error = MutableLiveData<Event<Error>>()
    val error: LiveData<Event<Error>> = _error

    private val _success = MutableLiveData<Any>()
    val success: LiveData<Any> = _success

     val images = MutableLiveData<String>("")

    fun removeFile(position:Int){
        val list = images.value!!.toArrayList()
        list.removeAt(position)
        images.value = list.toStr()
    }

    fun fileAttached(data:Intent, context: Context){
        val paths = repository.loadFilePaths(data,context)
        if(images.value!!.toArrayList().size>= 4){
            context.toast("Максимум 4 файла")
        }else{
            images.value = images.value+ "," + paths.toStr()
        }

    }

    fun checkNamesEnabled() = viewModelScope.launch {
        val boardInfo = repository.loadBoardInfo(board)
        boardInfo?.let {
            namesEnabled.value = boardInfo.namesEnabled == 1
        }

    }

    fun makePost(view:View  ) {
        if (checkFields()) {
            CoroutineScope(Dispatchers.IO).launch {
                dataLoading.postValue(true)
                try {
                    val result = repository.makePost(
                        board,
                        thread, comment,
                        captchaKey, captchaResponse.value!!,
                        username,
                        images.value!!.toArrayList()
                    )
                    checkResult(result)
                    PathUtil.deleteTempFiles(view.context)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    _error.postValue(Event(Error(WARNING, "Не получилось отправить запрос")))
                }
                dataLoading.postValue(false)
            }
        }
    }

    fun captchaPassed(response: String) {
        captchaResponse.postValue(response)
    }

    private fun checkResult(result: String) {
        if (result != "OK") {
            _error.postValue(Event(Error(WARNING, result)))
        } else {
            _success.postValue(Any())
        }
    }

    fun loadCaptcha(view: View) {
        viewModelScope.launch {
            captchaKey = repository.getCaptchaKey(board, thread)
            _openCaptchaDialog.value = Event(captchaKey)
        }
    }

    private fun checkFields(): Boolean {
        if (comment.length > 14999 || comment.trim().isEmpty()) {
            _error.postValue(Event(Error(WARNING, "Введите текст")))
            return false
        } else if (captchaResponse.value.isNullOrEmpty()) {
            _error.postValue(Event(Error(WARNING, "Пройдите капчу")))
            return false
        } else if(thread == "0" && images.value!!.toArrayList().isEmpty()){
            _error.postValue(Event(Error(WARNING, "Необходимо добавить хотябы одно фото")))
            return false
        }
        return true
    }
}

@Suppress("UNCHECKED_CAST")
class SendPostViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SendPostViewModel(
            repository
        ) as T
    }

}