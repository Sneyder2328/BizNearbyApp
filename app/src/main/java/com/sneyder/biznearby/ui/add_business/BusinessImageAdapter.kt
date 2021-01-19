package com.sneyder.biznearby.ui.add_business

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sneyder.biznearby.R
import com.sneyder.biznearby.utils.debug
import kotlinx.android.synthetic.main.business_add_image_item.view.*
import kotlinx.android.synthetic.main.business_image_item.view.*

class BusinessImageAdapter(
    private val listener: BusinessImageListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val IMAGE_TYPE = 1
        const val ICON_TYPE = 2
    }

    val images: MutableList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == IMAGE_TYPE) BusinessImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.business_image_item, parent, false)
        ) else {
            IconViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.business_add_image_item, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BusinessImageViewHolder) holder.bind(images[position])
        else if (holder is IconViewHolder) holder.bind()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) ICON_TYPE else IMAGE_TYPE
    }

    override fun getItemCount(): Int = images.count() + 1

    inner class BusinessImageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val removeButton: ImageButton by lazy { view.removeButton }
        private val imgImageView: ImageView by lazy { view.imgImageView }

        fun bind(imageUrl: String) {
            debug("BusinessImageViewHolder bind $imageUrl")
            removeButton.setOnClickListener {
                listener.onImageRemoved(imageUrl)
            }
            Glide.with(view)
                .load(imageUrl)
                .centerCrop()
                .into(imgImageView)
        }

    }

    inner class IconViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            view.icImageView.setOnClickListener {
                listener.onImageSelected()
            }
        }

    }

    interface BusinessImageListener {
        fun onImageRemoved(imageUrl: String)
        fun onImageSelected()
    }

}