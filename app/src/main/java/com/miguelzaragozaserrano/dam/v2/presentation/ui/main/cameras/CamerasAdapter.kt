package com.miguelzaragozaserrano.dam.v2.presentation.ui.main.cameras

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.miguelzaragozaserrano.dam.v2.R
import com.miguelzaragozaserrano.dam.v2.databinding.ListViewItemBinding
import com.miguelzaragozaserrano.dam.v2.domain.models.Camera
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants
import com.miguelzaragozaserrano.dam.v2.presentation.utils.Constants.ORDER.*
import com.miguelzaragozaserrano.dam.v2.presentation.utils.bindBackgroundItemSelected
import com.miguelzaragozaserrano.dam.v2.presentation.utils.bindBackgroundItemUnselected
import com.miguelzaragozaserrano.dam.v2.presentation.utils.bindListViewItem
import kotlin.properties.Delegates

class CamerasAdapter(
    private val context: Context,
    private val onClickCamera: OnClickCameraListener
) : RecyclerView.Adapter<CamerasViewHolder>() {

    private var order = NORMAL

    private var lastCameraSelected: Camera? = null
    private var lastBindingItem: ListViewItemBinding? = null
    var currentList: List<Camera> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }
    private var normalList: List<Camera> = currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CamerasViewHolder {
        return CamerasViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CamerasViewHolder, position: Int) {
        val camera = currentList[position]
        holder.itemView.apply {
            setOnClickListener {
                if (camera != lastCameraSelected) {
                    lastBindingItem?.bindBackgroundItemUnselected(
                        lastCameraSelected,
                        getDrawable(
                            context,
                            R.drawable.border
                        )
                    )
                    lastBindingItem =
                        holder.bindSelectedCamera(
                            context = context,
                            camera = camera
                        )
                    onClickCamera.onClick(camera, lastBindingItem)
                    notifyDataSetChanged()
                }
            }
        }
        holder.bindItem(camera, context)
    }

    override fun getItemCount(): Int = currentList.size

    fun setLastCameraSelected(camera: Camera?) {
        lastCameraSelected = camera
    }

    fun setLastBindingItem(binding: ListViewItemBinding?) {
        lastBindingItem = binding
    }

    fun setOrder(order: Constants.ORDER) {
        this.order = order
        setList(order)
    }

    private fun setList(order: Constants.ORDER) {
        currentList = when (order) {
            ASCENDING -> {
                currentList.sortedBy { camera ->
                    camera.name
                }
            }
            DESCENDING -> {
                currentList.sortedByDescending { camera ->
                    camera.name
                }
            }
            NORMAL -> currentList
        }
    }

    fun getOrder(): Constants.ORDER = order

}

class CamerasViewHolder private constructor(private val binding: ListViewItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindItem(camera: Camera, context: Context) =
        with(binding) {
            bindListViewItem(
                name = camera.name,
                selected = camera.selected,
                border = getDrawable(context, R.drawable.border),
                borderSelected = getDrawable(context, R.drawable.border_selected),
                favIcon = getDrawable(context, R.drawable.ic_favorite_deselected)
            )
        }

    fun bindSelectedCamera(
        context: Context,
        camera: Camera?
    ): ListViewItemBinding {
        with(binding) {
            return bindBackgroundItemSelected(
                camera = camera,
                drawable = getDrawable(context, R.drawable.border_selected)
            )
        }
    }

    companion object {
        fun from(parent: ViewGroup): CamerasViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                ListViewItemBinding.inflate(layoutInflater, parent, false)
            return CamerasViewHolder(binding)
        }
    }

}

class OnClickCameraListener(val clickListener: (camera: Camera, lastBindingItem: ListViewItemBinding?) -> Unit) {
    fun onClick(camera: Camera, lastBindingItem: ListViewItemBinding?) =
        clickListener(camera, lastBindingItem)
}