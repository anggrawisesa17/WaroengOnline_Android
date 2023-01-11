package com.example.waroeng_online.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.waroeng_online.R
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.model.Produk
import com.example.waroeng_online.room.MyDatabase
import com.example.waroeng_online.util.Config
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList

class AdapterKeranjang(var activity: Activity, private var data:ArrayList<Produk>, private var listener: Listeners):RecyclerView.Adapter<AdapterKeranjang.Holder>() {
    class Holder(view: View):RecyclerView.ViewHolder(view){
        val tvNama: TextView = view.findViewById(R.id.tv_nama_keranjang)
        val tvHarga: TextView = view.findViewById(R.id.tv_harga_keranjang)
        val imgProduk: ImageView = view.findViewById(R.id.img_produk_Keranjang)
        val layout: CardView = view.findViewById(R.id.layout_keranjang)

        val btnTambah: ImageView = view.findViewById(R.id.btn_tambah)
        val btnKurang: ImageView = view.findViewById(R.id.btn_kurang)
        val btnDelete: ImageView = view.findViewById(R.id.btn_delete_keranjang_item)
        val checkbox: CheckBox = view.findViewById(R.id.checkBox)
        val tvjumlah: TextView = view.findViewById(R.id.tv_jumlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val produk = data[position]
        val harga = Integer.valueOf(produk.harga)

        holder.tvNama.text = produk.name
        holder.tvHarga.text = Helper().gantiRupiah(harga * produk.jumlah)

        var jumlah = data[position].jumlah
        holder.tvjumlah.text = jumlah.toString()

        holder.checkbox.isChecked = produk.selected
        holder.checkbox.setOnCheckedChangeListener { _, isChecked -> //button view
            produk.selected = isChecked
            update(produk)
        }

        val image = Config.produkUrl+data[position].image
        Picasso.get()
            .load(image)
            .placeholder(R.drawable.box)
            .error(R.drawable.box)
            .into(holder.imgProduk)

        holder.btnTambah.setOnClickListener {
            if(jumlah >= 10) return@setOnClickListener //stok
            jumlah++
            produk.jumlah = jumlah
            update(produk)
            holder.tvjumlah.text = jumlah.toString()
            holder.tvHarga.text = Helper().gantiRupiah(harga * jumlah)
        }

        holder.btnKurang.setOnClickListener {
            if(jumlah <= 1) return@setOnClickListener
            jumlah--
            produk.jumlah = jumlah
            update(produk)
            holder.tvjumlah.text = jumlah.toString()
            holder.tvHarga.text = Helper().gantiRupiah(harga * jumlah)
        }

        holder.btnDelete.setOnClickListener {
            delete(produk)
            listener.onDelete(position)
        }
    }

    interface Listeners{
        fun onUpdate()
        fun onDelete(position: Int)
    }

    private fun update(data: Produk){
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable { myDb!!.daoKeranjang().update(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listener.onUpdate()
            })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun delete(data: Produk) {
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable { myDb!!.daoKeranjang().delete(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                notifyDataSetChanged()
            })
    }
    override fun getItemCount(): Int {
        return data.size
    }
}