package com.example.waroeng_online.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waroeng_online.R
import com.example.waroeng_online.model.Bank
import kotlin.collections.ArrayList

class AdapterBank(var data:ArrayList<Bank>, private var listener: Listeners):RecyclerView.Adapter<AdapterBank.Holder>() {
    class Holder(view: View):RecyclerView.ViewHolder(view){
        val tvNama: TextView = view.findViewById(R.id.tv_nama)
        val image: ImageView = view.findViewById(R.id.image)
        val layout: LinearLayout = view.findViewById(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_bank, parent, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        val a = data[position]
        holder.tvNama.text = a.nama
        holder.image.setImageResource(a.image)
        holder.layout.setOnClickListener{
            listener.onClicked(a, holder.adapterPosition)
        }
    }



    interface Listeners{
        fun onClicked(data: Bank, index:Int)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}