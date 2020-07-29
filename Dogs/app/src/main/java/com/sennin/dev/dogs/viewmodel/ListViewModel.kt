package com.sennin.dev.dogs.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.sennin.dev.dogs.model.DogBreed
import com.sennin.dev.dogs.model.DogDataBase
import com.sennin.dev.dogs.model.DogsApiService
import com.sennin.dev.dogs.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : BaseViewModel(application) {
    private val dogsService = DogsApiService()
    private val prefHelper = SharedPreferencesHelper(getApplication())

    //in nano Time format
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L

    //To observing avoiding memory leaks
    private val disposable = CompositeDisposable()

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        val updateTime = prefHelper.getUpdateTime()
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchFromDataBase()
        } else {
            fetchFromRemote()

        }
    }

    fun refreshBypassCache(){
        fetchFromRemote()
    }

    private fun fetchFromDataBase() {
        loading.postValue(true)
        launch {
            val dogs = DogDataBase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(getApplication(), "Dogs from database", Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchFromRemote() {
        loading.postValue(true)
        disposable.add(
            dogsService.getDogs()
                //create a new Thread
                .subscribeOn(Schedulers.newThread())
                //Response just in the main Thread
                .observeOn(AndroidSchedulers.mainThread())
                //Use object to indicate the implementation of the abstract class is a object
                .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>() {
                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogsLocally(dogList)
                        Toast.makeText(getApplication(), "Dogs from server", Toast.LENGTH_LONG)
                            .show()

                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.postValue(true)
                        loading.postValue(false)
                        //TODO: handle the error
                    }
                })
        )
    }

    private fun dogsRetrieved(dogList: List<DogBreed>) {
        dogs.postValue(dogList)
        dogsLoadError.postValue(false)
        loading.postValue(false)
    }

    private fun storeDogsLocally(dogList: List<DogBreed>) {
        launch {
            val dao = DogDataBase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val result = dao.insertAll(* dogList.toTypedArray())
            var i = 0
            while (i < dogList.size) {
                dogList[i].uuid = result[i].toInt()
                ++i
            }
            dogsRetrieved(dogList)
        }

        prefHelper.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}