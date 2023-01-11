package com.example.waroeng_online.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waroeng_online.R
import com.example.waroeng_online.activity.MasukActivity
import com.example.waroeng_online.activity.PengirimanActivity
import com.example.waroeng_online.adapter.AdapterKeranjang
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.helper.Sharedpref
import com.example.waroeng_online.model.Produk
import com.example.waroeng_online.room.MyDatabase
import com.inyongtisto.myhelper.extension.isNotNull
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * A simple [Fragment] subclass.
 */
class KeranjangFragment : Fragment() {
    private lateinit var myDb : MyDatabase
    private lateinit var s: Sharedpref

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_keranjang, container, false)
        init(view)
        myDb = MyDatabase.getInstance(requireActivity())!!
        s = Sharedpref(requireActivity())

        mainButton()


        return view
    }

    lateinit var adapter : AdapterKeranjang
    var listProduk = ArrayList<Produk>()
    private fun displayProduk(){
        listProduk = myDb.daoKeranjang().getAll() as ArrayList
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter = AdapterKeranjang(requireActivity(), listProduk, object : AdapterKeranjang.Listeners{
            override fun onUpdate() {
                hitungTotal()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onDelete(position: Int) {
                listProduk.removeAt(position)
                adapter.notifyDataSetChanged()
                hitungTotal()
            }

        })

        rvProduk.adapter = adapter
        rvProduk.layoutManager = layoutManager
    }

    private var totalHarga = 0
    fun hitungTotal(){
        val listProduk = myDb.daoKeranjang().getAll() as ArrayList
        totalHarga = 0
        var isSelectedAll = true

        for(produk in listProduk){
            if(produk.selected){
                val harga = Integer.valueOf(produk.harga)
                totalHarga += (harga*produk.jumlah)
            }else{
                isSelectedAll = false
            }

        }
        cbAll.isChecked = isSelectedAll

        tvTotal.text = Helper().gantiRupiah(totalHarga)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun mainButton(){
        btnDelete.setOnClickListener {
            val lisDelete = ArrayList<Produk>()
            for(p in listProduk){
                if(p.selected)lisDelete.add(p)
            }
            delete(lisDelete)
        }

        btnBayar.setOnClickListener {
            if(s.getStatusLogin()){
                var isThereProduk = false
                for(p in listProduk){
                    if(p.selected) isThereProduk = true
                }
                if(isThereProduk){
                    val intent = Intent(requireActivity(), PengirimanActivity::class.java)
                    intent.putExtra("extra", ""+totalHarga)
                    startActivity(intent)
                }else{
                    Toast.makeText(requireContext(), "Tidak ada produk yang dipilih", Toast.LENGTH_SHORT).show()
                }
            }else{
                requireActivity().startActivity(Intent(requireActivity(), MasukActivity::class.java))
            }
        }
        
        cbAll.setOnClickListener {
            for(i in listProduk.indices){
                val produk = listProduk[i]
                produk.selected = cbAll.isChecked
                listProduk[i] = produk
            }
            adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun delete(data: ArrayList<Produk>) {
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().delete(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listProduk.clear()
                listProduk.addAll(myDb.daoKeranjang().getAll() as ArrayList)
                adapter.notifyDataSetChanged()
            })
    }


    private lateinit var btnDelete: ImageView
    private lateinit var rvProduk: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnBayar: TextView
    private lateinit var cbAll: CheckBox
    private fun init(view: View) {
        btnDelete = view.findViewById(R.id.btn_delete_keranjang)
        rvProduk = view.findViewById(R.id.rv_produk_keranjang)
        tvTotal = view.findViewById(R.id.tv_total)
        btnBayar = view.findViewById(R.id.btn_bayar)
        cbAll = view.findViewById(R.id.cb_all)

    }

    override fun onResume() {
        hitungTotal()
        displayProduk()
        super.onResume()
    }
}