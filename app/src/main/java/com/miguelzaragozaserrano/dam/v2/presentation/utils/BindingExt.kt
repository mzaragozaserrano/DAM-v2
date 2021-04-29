package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentCamerasBinding
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentSplashBinding
import com.miguelzaragozaserrano.dam.v2.databinding.ListViewItemBinding
import com.miguelzaragozaserrano.dam.v2.domain.models.Camera

fun ListViewItemBinding.bindListViewItem(
    name: String,
    selected: Boolean,
    border: Drawable?,
    borderSelected: Drawable?,
    favIcon: Drawable?
) {
    cameraName.text = name
    favButton.background = favIcon
    if (selected) {
        constraint.background = borderSelected
    } else {
        constraint.background = border
    }
}

fun FragmentCamerasBinding.bindImageView(imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("http").build()
        GlideApp.with(this.cameraImage.context)
            .load(imgUri)
            .timeout(60000)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.broken_image)
            )
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(this.cameraImage as ImageView)
    }
}

fun ListViewItemBinding.bindBackgroundItemSelected(camera: Camera?, drawable: Drawable?): ListViewItemBinding {
    camera?.selected = true
    constraint.background = drawable
    return this
}

fun ListViewItemBinding.bindBackgroundItemUnselected(camera: Camera?, drawable: Drawable?) {
    camera?.selected = false
    constraint.background = drawable
}

fun FragmentSplashBinding.bindProgressBar(camerasDownloaded: Int, totalCameras: Int?) {
    totalCameras?.let {
        progressHorizontal.progress = (camerasDownloaded * 100) / totalCameras
    }
}

fun FragmentSplashBinding.bindProgressCircle() {
    val animator: ValueAnimator = ValueAnimator.ofInt(0, 100)
    animator.addUpdateListener { animation ->
        this.progressCircle.progress = animation.animatedValue as Int
    }
}