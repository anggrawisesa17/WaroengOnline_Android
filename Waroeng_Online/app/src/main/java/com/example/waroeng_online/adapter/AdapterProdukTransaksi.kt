package com.example.waroeng_online.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.waroeng_online.R
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.model.DetailTransaksi
import com.example.waroeng_online.util.Config
import com.squareup.picasso.Picasso

class AdapterProdukTransaksi(var data:ArrayList<DetailTransaksi>):RecyclerView.Adapter<AdapterProdukTransaksi.Holder>() {
    class Holder(view: View):RecyclerView.ViewHolder(view){
        val imgProduk: ImageView = view.findViewById(R.id.img_produk)
        val tvNama: TextView = view.findViewById(R.id.tv_nama)
        val tvHarga: TextView = view.findViewById(R.id.tv_harga)
        val tvTotalHarga: TextView = view.findViewById(R.id.tv_totalHarga)
        val tvJumlah: TextView = view.findViewById(R.id.tv_jumlah)
        val layout: CardView = view.findViewById(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_transaksi, parent, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val a = data[position]
        val p = a.produk
        val name = p.name
        holder.tvNama.text = name
        holder.tvHarga.text = Helper().gantiRupiah(p.harga)
        holder.tvTotalHarga.text = Helper().gantiRupiah(a.total_harga)
        holder.tvJumlah.text = a.total_item.toString() + " Items"

        holder.layout.setOnClickListener{
//            listener.onClicked(a)
        }

        val image = Config.produkUrl+p.image
        Picasso.get()
            .load(image)
            .placeholder(R.drawable.box)
            .error(R.drawable.box)
            .into(holder.imgProduk)
    }



    interface Listeners{
        fun onClicked(data: DetailTransaksi)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}