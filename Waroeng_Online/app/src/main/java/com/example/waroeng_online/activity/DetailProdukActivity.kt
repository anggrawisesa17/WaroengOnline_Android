package com.example.waroeng_online.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.waroeng_online.R
import com.example.waroeng_online.app.ApiConfig
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.model.Produk
import com.example.waroeng_online.model.ResponModel
import com.example.waroeng_online.room.MyDatabase
import com.example.waroeng_online.util.Config
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class DetailProdukActivity : AppCompatActivity() {
    private lateinit var tvNama:TextView
    private lateinit var tvHarga:TextView
    private lateinit var image:ImageView
    private lateinit var tvdeskripsi:TextView
    private lateinit var toolbar:androidx.appcompat.widget.Toolbar
    private lateinit var btnkeranjang: RelativeLayout
    lateinit var produk: Produk
    private lateinit var myDb: MyDatabase
    private lateinit var divAngka: RelativeLayout
    private lateinit var tvAngka: TextView
    private lateinit var img: ImageView
    private lateinit var btnkeranjangTool: RelativeLayout
    private lateinit var btnBeliSekarang: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk)

        tvNama = findViewById(R.id.tv_nama_produk)
        tvHarga = findViewById(R.id.tv_harga_produk)
        image = findViewById(R.id.image_produk)
        tvdeskripsi = findViewById(R.id.tv_deskripsi)
        toolbar = findViewById(R.id.toolbar_custom)
        btnkeranjang = findViewById(R.id.btn_keranjang_produk)
        divAngka = findViewById(R.id.div_angka)
        tvAngka = findViewById(R.id.tv_angka)
        btnBeliSekarang = findViewById(R.id.btn_beli_sekarang)
        img = findViewById(R.id.img)
        btnkeranjangTool = findViewById(R.id.btn_keranjang_toolbar)
        myDb = MyDatabase.getInstance(this)!! // call database

        getProduk()
        getInfo()
        mainButton()
        checkKeranjang()
    }


    private fun mainButton(){
        btnkeranjang.setOnClickListener {
            val data = myDb.daoKeranjang().getProduk(produk.id)
            if(data == null){
                insert()
            }else{
                data.jumlah = data.jumlah + 1
                update(data)
            }
        }

        btnBeliSekarang.setOnClickListener {
            val data = myDb.daoKeranjang().getProduk(produk.id)
            if(data == null){
                insert()
            }else{
                data.jumlah = data.jumlah + 1
                update(data)
            }
            val intent = Intent("event:keranjang")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            onBackPressed()

        }

        btnkeranjangTool.setOnClickListener{
            val intent = Intent("event:keranjang")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            onBackPressed()
        }
    }

    private fun insert(){
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().insert(produk) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                checkKeranjang()
                Log.d("respons", "data inserted")
                Toast.makeText(this, "Berhasil menambah ke keranjang", Toast.LENGTH_SHORT).show()
            })
    }

    private fun update(data: Produk){
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().update(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                checkKeranjang()
                Log.d("respons", "data inserted")
                Toast.makeText(this, "Berhasil menambah ke keranjang", Toast.LENGTH_SHORT).show()
            })
    }

    private fun checkKeranjang(){
        val dataKeranjang = myDb.daoKeranjang().getAll()

        if(myDb.daoKeranjang().getAll().isNotEmpty()){
            divAngka.visibility = View.VISIBLE
            tvAngka.text = dataKeranjang.size.toString()
        }else{
            divAngka.visibility = View.GONE
        }
    }
    private fun getInfo(){
        val data = intent.getStringExtra("extra")
        produk = Gson().fromJson(data, Produk::class.java)

        //set value
        tvNama.text = produk.name
        tvHarga.text = Helper().gantiRupiah(produk.harga)
        tvdeskripsi.text = produk.deskripsi

        val img = Config.produkUrl+produk.image
        Picasso.get()
            .load(img)
            .placeholder(R.drawable.box)
            .error(R.drawable.box)
//            .resize(400, 400)
            .into(image)

        //set toolbar
        Helper().setToolbar(this, toolbar, produk.name)
    }


    private var listProduk:ArrayList<Produk> = ArrayList()
    private fun getProduk(){
        ApiConfig.instanceRetrofit.getProduk().enqueue(object : Callback<ResponModel> {
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1){
                    val arrayProduk = ArrayList<Produk>()
                    for(p in res.produks){
                        arrayProduk.add(p)
                    }
                    listProduk = arrayProduk
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}