package com.dvach_2ch.a2ch.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import com.dvach_2ch.a2ch.data.Repository
import com.dvach_2ch.a2ch.models.threads.ThreadPost
import kotlinx.coroutines.runBlocking

class HistoryDataSource(private val repository: Repository,private val empty:MutableLiveData<Boolean>) : PositionalDataSource<ThreadPost>(){
    private var historyList : List<ThreadPost>? = null
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<ThreadPost>) {
        runBlocking {
            val threads =  historyList ?: repository.loadHistory()
            historyList = threads
            empty.postValue(threads.isEmpty())
            val endPosition = if(params.loadSize > threads.size) threads.size  else params.startPosition + params.loadSize

            if (endPosition <= threads.size) {

                callback.onResult(
                    threads.subList(
                        params.startPosition,
                        endPosition
                    )
                )
            }
        }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<ThreadPost>) {
        runBlocking {
            val threads =  historyList ?: repository.loadHistory()
            historyList = threads
            empty.postValue(threads.isEmpty())
            val endPosition = if(params.requestedLoadSize > threads.size) threads.size  else params.requestedStartPosition + params.requestedLoadSize
            if (endPosition <= threads.size) {
                callback.onResult(
                    threads.subList(
                        params.requestedStartPosition,
                        endPosition
                    ), 0, threads.size
                )
            }

        }
    }

}

class HistoryDataSourceFactory(private val repository: Repository, private val empty: MutableLiveData<Boolean>) : DataSource.Factory<Int, ThreadPost>(){
    override fun create(): DataSource<Int, ThreadPost> {
        return HistoryDataSource(repository,empty)
    }

}