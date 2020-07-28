package com.sennin.dev.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sennin.dev.dogs.model.DogBreed

class DetailViewModel : ViewModel() {
    var dogLiveData = MutableLiveData<DogBreed>()

    fun fetch() {
        val dog1 = DogBreed("1", "Gorgi", "15 years", "breedGroup", "breedFor", "temperament", "te")
        dogLiveData.value = dog1
    }
}