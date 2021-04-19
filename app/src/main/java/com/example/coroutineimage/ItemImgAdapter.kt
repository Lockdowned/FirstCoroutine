package com.example.coroutineimage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coroutineimage.databinding.ItemImgForRvBinding

class ItemImgAdapter(private val imgListBitmap: List<CompressedImg>):
    RecyclerView.Adapter<ItemImgAdapter.ImgViewHolder>() {

    inner class ImgViewHolder(private val imgForRvBinding: ItemImgForRvBinding):
        RecyclerView.ViewHolder(imgForRvBinding.root) {
            fun bind(currentImg: CompressedImg){
                imgForRvBinding.run {
                    ivCompressed.setImageBitmap(currentImg.img)
                    tvGradientCompression.text = "Compressed by"
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgViewHolder {
        val imgForRvBinding = ItemImgForRvBinding.inflate(LayoutInflater.from(parent.context),
        parent, false)
        return ImgViewHolder(imgForRvBinding)
    }

    override fun onBindViewHolder(holder: ImgViewHolder, position: Int) {
        val imgCurrentItem = imgListBitmap[position]
        holder.bind(imgCurrentItem)
    }

    override fun getItemCount(): Int {
        return imgListBitmap.size
    }


}