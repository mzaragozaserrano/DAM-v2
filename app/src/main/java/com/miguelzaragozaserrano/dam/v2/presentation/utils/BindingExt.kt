package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.data.models.Camera
import com.miguelzaragozaserrano.dam.v2.data.models.SearchViewState
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentCamerasBinding
import com.miguelzaragozaserrano.dam.v2.databinding.FragmentSplashBinding
import com.miguelzaragozaserrano.dam.v2.databinding.ListViewItemBinding
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.MainViewModel
import com.miguelzaragozaserrano.dam.v2.presentation.ui.main.cameras.CamerasAdapter
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Utils.setItemsVisibility

fun ListViewItemBinding.bindListViewItem(
    name: String,
    selected: Boolean,
    favorite: Boolean,
    border: Drawable?,
    borderSelected: Drawable?,
    favIcon: Drawable?,
    favIconSelected: Drawable?
) {
    cameraName.text = name
    constraint.background = if (selected) {
        borderSelected
    } else {
        border
    }
    favButton.background = if (favorite) {
        favIconSelected
    } else {
        favIcon
    }
}

fun ListViewItemBinding.bindFavButton(
    camera: Camera,
    favIcon: Drawable?,
    favIconSelected: Drawable?
) {
    if (camera.favorite) {
        camera.favorite = false
        favButton.background = favIcon
    } else {
        camera.favorite = true
        favButton.background = favIconSelected
    }
}

fun FragmentCamerasBinding.bindImageView(imgUrl: String?) {
    if (imgUrl != null) {
        this.cameraImage.visibility = View.VISIBLE
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
    } else {
        this.cameraImage.visibility = View.GONE
    }
}

fun ListViewItemBinding.bindBackgroundItem(
    camera: Camera?,
    border: Drawable?,
    borderSelected: Drawable?
): ListViewItemBinding? {
    return if (camera?.selected == false) {
        camera.selected = true
        constraint.background = borderSelected
        this
    } else {
        camera?.selected = false
        constraint.background = border
        null
    }
}

fun FragmentSplashBinding.bindProgressBar(camerasDownloaded: Int?, totalCameras: Int?) {
    totalCameras?.let {
        camerasDownloaded?.let {
            progressHorizontal.progress = (camerasDownloaded * 100) / totalCameras
        }
    }
}

fun FragmentSplashBinding.bindProgressCircle() {
    val animator: ValueAnimator = ValueAnimator.ofInt(0, 100)
    animator.addUpdateListener { animation ->
        this.progressCircle.progress = animation.animatedValue as Int
    }
}

fun FragmentCamerasBinding.bindAdapter(
    viewModel: MainViewModel,
    camerasList: RecyclerView,
    adapter: CamerasAdapter
) {
    with(adapter) {
        bindingItem = viewModel.adapterState.bindingItem
        cameraSelected = viewModel.adapterState.camera
        type = viewModel.adapterState.type
        normalList = viewModel.allCameras
        currentList = viewModel.allCameras
        setListByOrder(viewModel.adapterState.order)
        camerasList.adapter = this
        bindImageView(imgUrl = cameraSelected?.url)
    }
}

fun MenuItem.bindSearch(
    menu: Menu?,
    adapter: CamerasAdapter,
    context: Context,
    searchViewState: SearchViewState
) {
    val searchView = actionView as SearchView
    if (searchViewState.focus) {
        expandActionView()
        searchView.isIconified = false
        searchView.setQuery(searchViewState.query, true)
        adapter.setList(searchViewState.query)
        menu?.let { setItemsVisibility(it, false) }
    }
    with(searchView) {
        queryHint = context.getString(R.string.search_query_hint)
        maxWidth = Integer.MAX_VALUE
        setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(auxQuery: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    Log.d("hola", query.toString())
                    adapter.setList(query)
                    searchViewState.query = query
                    return true
                }
            })
    }
    setOnActionExpandListener(
        object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                searchViewState.focus = true
                menu?.let { setItemsVisibility(it, false) }
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                searchViewState.focus = false
                menu?.let { setItemsVisibility(it, true) }
                return true
            }
        }
    )
}

fun AppCompatImageView.bindImageViewMarker(url: String) {
    val imgUri = url.toUri().buildUpon().scheme("http").build()
    GlideApp.with(this.context)
        .load(imgUri)
        .timeout(60000)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.broken_image)
        )
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}