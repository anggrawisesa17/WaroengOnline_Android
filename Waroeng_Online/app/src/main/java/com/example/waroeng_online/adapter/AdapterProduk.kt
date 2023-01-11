package com.example.waroeng_online.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.waroeng_online.R
import com.example.waroeng_online.activity.DetailProdukActivity
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.model.Produk
import com.example.waroeng_online.util.Config
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class AdapterProduk(var activity: Activity, private var data:ArrayList<Produk>):RecyclerView.Adapter<AdapterProduk.Holder>() {
    class Holder(view: View):RecyclerView.ViewHolder(view){
        val tvNama: TextView = view.findViewById(R.id.tv_nama)
        val tvHarga: TextView = view.findViewById(R.id.tv_harga)
        val imgProduk: ImageView = view.findViewById(R.id.img_produk)
        val layout: CardView = view.findViewById(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val a = data[position]
        val harga = Integer.valueOf(a.harga)

        holder.tvNama.text = data[position].name
        holder.tvHarga.text = Helper().gantiRupiah(harga)

        val image = Config.produkUrl+data[position].image
        Log.d("RESPON", "image: $image")
        Picasso.get()
            .load(image)
            .placeholder(R.drawable.box)
            .error(R.drawable.box)
            .into(holder.imgProduk)

        holder.layout.setOnClickListener{
            val activiti = Intent(activity, DetailProdukActivity::class.java)
            val str = Gson().toJson(data[position], Produk::class.java)
            activiti.putExtra("extra", str)
            activity.startActivity(activiti)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}