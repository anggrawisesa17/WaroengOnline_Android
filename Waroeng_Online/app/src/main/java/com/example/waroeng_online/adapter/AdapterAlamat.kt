package com.example.waroeng_online.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.waroeng_online.R
import com.example.waroeng_online.model.Alamat
import kotlin.collections.ArrayList

class AdapterAlamat(var data:ArrayList<Alamat>, private var listener: Listeners):RecyclerView.Adapter<AdapterAlamat.Holder>() {
    class Holder(view: View):RecyclerView.ViewHolder(view){
        val tvNama: TextView = view.findViewById(R.id.tv_nama)
        val tvPhone: TextView = view.findViewById(R.id.tv_phone)
        val tvAlamat: TextView = view.findViewById(R.id.tv_alamat)
        val layout: CardView = view.findViewById(R.id.layout)
        val rd: RadioButton = view.findViewById(R.id.rd_alamat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_alamat, parent, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        val a = data[position]

        holder.rd.isChecked = a.isSelected
        holder.tvNama.text = a.name
        holder.tvPhone.text = a.phone
        holder.tvAlamat.text = a.alamat +", "+a.kota+", "+a.provinsi+", "+a.kodePos+", ("+a.type+")"

        holder.rd.setOnClickListener {
            a.isSelected = true
            listener.onClicked(a)
        }
        /*holder.layout.setOnClickListener{
            a.isSelected = true
            listener.onClicked(a)
        }*/
    }



    interface Listeners{
        fun onClicked(data: Alamat)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}