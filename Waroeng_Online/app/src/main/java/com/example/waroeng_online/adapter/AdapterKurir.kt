package com.example.waroeng_online.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waroeng_online.R
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.model.rajaongkir.Costs
import kotlin.collections.ArrayList

class AdapterKurir(var data:ArrayList<Costs>, private var kurir:String, private var listener: Listeners):RecyclerView.Adapter<AdapterKurir.Holder>() {
    class Holder(view: View):RecyclerView.ViewHolder(view){
        val tvNama: TextView = view.findViewById(R.id.tv_nama)
        val tvLamaPengiriman: TextView = view.findViewById(R.id.tv_lamaPengiriman)
        val tvBerat: TextView = view.findViewById(R.id.tv_berat)
        val tvHarga: TextView = view.findViewById(R.id.tv_harga)
        val layout: LinearLayout = view.findViewById(R.id.layout)
        val rd: RadioButton = view.findViewById(R.id.rd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_kurir, parent, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        val a = data[position]

        holder.rd.isChecked = a.isActive
        holder.tvNama.text = kurir + " "+a.service
        val cost = a.cost[0]
        holder.tvLamaPengiriman.text = cost.etd + " hari kerja"
        holder.tvHarga.text = Helper().gantiRupiah(cost.value)
        holder.tvBerat.text = "1 kg x" + Helper().gantiRupiah(cost.value)
        /*holder.tvAlamat.text = a.alamat +", "+a.kota+", "+a.kecamatan+", "+a.kodePos+", ("+a.type+")"*/

        holder.rd.setOnClickListener {
            a.isActive = true
            listener.onClicked(a, holder.adapterPosition)
        }
        /*holder.layout.setOnClickListener{
            a.isSelected = true
            listener.onClicked(a)
        }*/
    }



    interface Listeners{
        fun onClicked(data: Costs, index:Int)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}