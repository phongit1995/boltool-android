package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class IconAppAdapter(val context: Context, val iconApps: List<String>) : BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return IconAppAdapter(
            LayoutInflater.from(context).inflate(R.layout.item_app, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return iconApps.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = holder as IconAppAdapter
        Glide.with(context)
            .load(iconApps[position])
            .override(400, 400)
            .centerCrop()
            .into(item.imgDetail)
        item.imgDetail.setOnClickListener(View.OnClickListener {
            mCallBack.callBack("oke", iconApps[position])
        })
    }

    class IconAppAdapter(item: View) : BaseViewHolder(item) {
        var imgDetail: ImageView = item.findViewById<ImageView>(R.id.imgAppIcon)
    }
}