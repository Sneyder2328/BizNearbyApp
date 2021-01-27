package com.sneyder.biznearby.ui.business_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sneyder.biznearby.R
import com.sneyder.biznearby.utils.debug
import kotlinx.android.synthetic.main.activity_business_details_image_item.view.*

class BusinessDetailsImageAdapter(private val onImgClicked: (imgUrl: String) -> Unit) :
    RecyclerView.Adapter<BusinessDetailsImageAdapter.ImageViewHolder>() {

    var images: List<String> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_business_details_image_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.count()

    inner class ImageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(imgUrl: String) {
            view.post {
                debug("width = ${view.width}")
                Glide.with(view)
                    .load(imgUrl)
                    .apply(RequestOptions().override(view.width, view.width))
                    .centerCrop()
                    .into(view.imgImageView)
            }
            view.setOnClickListener {
                onImgClicked(imgUrl)
            }
        }
    }
}