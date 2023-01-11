package com.example.waroeng_online.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.waroeng_online.R
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.model.Transaksi

class AdapterRiwayat(var data:ArrayList<Transaksi>, private var listener: Listeners):RecyclerView.Adapter<AdapterRiwayat.Holder>() {
    class Holder(view: View):RecyclerView.ViewHolder(view){
        val tvNama: TextView = view.findViewById(R.id.tv_nama)
        val tvHarga: TextView = view.findViewById(R.id.tv_harga)
        val tvTanggal: TextView = view.findViewById(R.id.tv_tgl)
        val tvJumlah: TextView = view.findViewById(R.id.tv_jumlah)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
//        val btnDetail: TextView = view.findViewById(R.id.btn_detail)
        val layout: CardView = view.findViewById(R.id.layout)
    }

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat, parent, false)
        return Holder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val a = data[position]
        val name = a.details[0].produk.name
        holder.tvNama.text = name
        holder.tvHarga.text = Helper().gantiRupiah(a.total_transfer)
        holder.tvJumlah.text = a.total_item + " Items"
        holder.tvStatus.text = a.status

//2022-12-14T07:52:29.000000Z
        val formatBaru = "d MMM yyyy"
        holder.tvTanggal.text = Helper().convertTanggal(a.created_at, formatBaru)

        var color = context.getColor(R.color.search)
        if(a.status == "SELESAI") color = context.getColor(R.color.orange)
        else if(a.status == "BATAL") color = context.getColor(R.color.red)
        holder.tvStatus.setTextColor(color)

        holder.layout.setOnClickListener{
            listener.onClicked(a)
        }
    }



    interface Listeners{
        fun onClicked(data: Transaksi)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}