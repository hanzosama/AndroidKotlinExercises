package com.sennin.dev.dogs.view

import android.view.View

interface DogClickListener {
    fun onDogClicked(view:View,id:Int)
}