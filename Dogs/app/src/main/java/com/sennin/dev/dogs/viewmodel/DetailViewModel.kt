package com.sennin.dev.dogs.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.sennin.dev.dogs.model.DogBreed
import com.sennin.dev.dogs.model.DogDataBase
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : BaseViewModel(application) {
    var dogLiveData = MutableLiveData<DogBreed>()
    private val dogsRepository = DogDataBase(getApplication()).dogDao()

    fun fetchDog(id: Int) {
        launch {
            val dog = dogsRepository.getDog(id)
            dogLiveData.postValue(dog)
        }
    }
}