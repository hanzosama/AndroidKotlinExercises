package com.sennin.dev.dogs.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.sennin.dev.dogs.model.DogBreed
import com.sennin.dev.dogs.model.DogDataBase
import com.sennin.dev.dogs.model.DogsApiService
import com.sennin.dev.dogs.util.NotificationsHelper
import com.sennin.dev.dogs.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class ListViewModel(application: Application) : BaseViewModel(application) {
    private val dogsService = DogsApiService()
    private val prefHelper = SharedPreferencesHelper(getApplication())
    private val dogsRepository = DogDataBase(getApplication()).dogDao()

    //in nano Time format
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L

    //To observing avoiding memory leaks
    private val disposable = CompositeDisposable()

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        checkCacheDuration()
        val updateTime = prefHelper.getUpdateTime()
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchFromDataBase()
        } else {
            fetchFromRemote()

        }
    }

    private fun checkCacheDuration() {

        val cachePreference = prefHelper.getCacheDuration()
        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5 * 60
            refreshTime = cachePreferenceInt.times(1000 * 1000 * 1000L)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    fun refreshBypassCache() {
        fetchFromRemote()
    }

    private fun fetchFromDataBase() {
        loading.postValue(true)
        launch {
            val dogs = dogsRepository.getAllDogs()
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

                        //implemented the notification
                        NotificationsHelper(getApplication()).createNotification()

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

            dogsRepository.deleteAllDogs()
            val result = dogsRepository.insertAll(* dogList.toTypedArray())
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