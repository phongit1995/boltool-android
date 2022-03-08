package com.example.myapplication

import androidx.recyclerview.widget.RecyclerView


abstract class BaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var mCallBack: OnActionCallBack
}

