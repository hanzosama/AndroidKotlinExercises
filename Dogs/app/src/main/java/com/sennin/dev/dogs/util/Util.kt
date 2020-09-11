package com.sennin.dev.dogs.util

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sennin.dev.dogs.R


val PERMISSION_SEND_SMS = 1234
//It will create the Spinner to use with is loading the images from remote.
fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 25f
        start()
    }
}

//Extension of ImageView Class
fun ImageView.loadImage(uri: String?, progressDrawable: CircularProgressDrawable) {
    val options = RequestOptions().placeholder(progressDrawable)
        .error(R.mipmap.ic_dog_launcher_foreground)

    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(uri)
        .into(this)
}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url: String?) {
    if (url != null) {
        view.loadImage(url, getProgressDrawable(view.context))
    }
}