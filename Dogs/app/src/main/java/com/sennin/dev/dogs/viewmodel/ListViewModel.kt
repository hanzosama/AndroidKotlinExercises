package com.sennin.dev.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sennin.dev.dogs.model.DogBreed
import com.sennin.dev.dogs.model.DogsApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ListViewModel : ViewModel() {
    private val dogsService = DogsApiService()

    //To observing avoiding memory leaks
    private val disposable = CompositeDisposable()

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        fetchFromRemote()
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
                        dogs.postValue(dogList)
                        dogsLoadError.postValue(false)
                        loading.postValue(false)
                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.postValue(true)
                        loading.postValue(false)
                        //TODO: handle the error
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}