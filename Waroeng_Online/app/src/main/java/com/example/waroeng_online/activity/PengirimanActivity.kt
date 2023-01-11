package com.example.waroeng_online.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waroeng_online.R
import com.example.waroeng_online.adapter.AdapterKurir
import com.example.waroeng_online.app.ApiConfigAlamat
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.helper.Sharedpref
import com.example.waroeng_online.model.Checkout
import com.example.waroeng_online.model.rajaongkir.Costs
import com.example.waroeng_online.model.rajaongkir.ResponOngkir
import com.example.waroeng_online.room.MyDatabase
import com.example.waroeng_online.util.ApiKey
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class PengirimanActivity : AppCompatActivity() {

    private lateinit var myDb : MyDatabase

    private lateinit var btnTambahAlamat:Button
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    private lateinit var divAlamat: CardView
    private lateinit var divKosong: TextView
    private lateinit var tvKosong: TextView
    private lateinit var tvPhone:TextView
    private lateinit var tvAlamat:TextView
    private lateinit var tvNama:TextView
    private lateinit var rvMetode: RecyclerView
    private lateinit var divMetodePengiriman: LinearLayout
    lateinit var spnKurir: Spinner
    private lateinit var divPengiriman: CardView
    private lateinit var tvOngkir: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvTotalBelanja: TextView
    private lateinit var btnBayar: Button
    private lateinit var edtCatatan: EditText

    private var totalHarga = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengiriman)

        btnTambahAlamat = findViewById(R.id.btn_tambahAlamat)
        toolbar = findViewById(R.id.toolbar)
        divAlamat = findViewById(R.id.div_alamat)
        divKosong = findViewById(R.id.div_kosong)
        tvKosong = findViewById(R.id.tv_kosong)
        tvPhone = findViewById(R.id.tv_phone)
        tvAlamat = findViewById(R.id.tv_alamat)
        tvNama = findViewById(R.id.tv_nama)
        rvMetode = findViewById(R.id.rv_metode)
        divMetodePengiriman = findViewById(R.id.div_metodePengiriman)
        spnKurir = findViewById(R.id.spn_kurir)
        divPengiriman = findViewById(R.id.div_pengiriman)
        tvOngkir = findViewById(R.id.tv_ongkir)
        tvTotal = findViewById(R.id.tv_total)
        tvTotalBelanja = findViewById(R.id.tv_totalBelanja)
        btnBayar = findViewById(R.id.btn_bayar)
        edtCatatan = findViewById(R.id.edt_catatan)


        Helper().setToolbar(this, toolbar, "Pengiriman")
        myDb = MyDatabase.getInstance(this)!!

        totalHarga = Integer.valueOf(intent.getStringExtra("extra")!!)
        tvTotalBelanja.text = Helper().gantiRupiah(totalHarga)
        mainButton()
        setSepinner()
    }

    private fun setSepinner(){
        val arrString = ArrayList<String>()
        arrString.add("JNE")
        arrString.add("POS")
        arrString.add("TIKI")

        val adapter = ArrayAdapter<Any>(this, R.layout.item_spinner, arrString.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnKurir.adapter = adapter

        spnKurir.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                getOngkir(spnKurir.selectedItem.toString())
//                if(position != 0){
//                    getOngkir(spnKurir.selectedItem.toString())
//                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    @SuppressLint("SetTextI18n")
    fun chekAlamat(){
        if (myDb.daoAlamat().getByStatus(true) != null){
            divAlamat.visibility = View.VISIBLE
            divKosong.visibility = View.GONE
            divMetodePengiriman.visibility = View.VISIBLE

            val a = myDb.daoAlamat().getByStatus(true)!!
            tvNama.text = a.name
            tvPhone.text = a.phone
            tvAlamat.text = a.alamat +", "+a.kota+", "+a.kodePos+", ("+a.type+")"
            btnTambahAlamat.text = "Ubah Alamat"

            getOngkir("JNE")
        } else{
            divAlamat.visibility = View.GONE
            divKosong.visibility = View.VISIBLE
            btnTambahAlamat.text = "Tambah Alamat"
        }
    }

    private fun mainButton(){
        btnTambahAlamat.setOnClickListener {
            startActivity(Intent(this, ListAlamatActivity::class.java))
        }

        btnBayar.setOnClickListener {
            bayar()
        }
    }

    private fun bayar(){
        val user = Sharedpref(this).getUser()!!
        val a = myDb.daoAlamat().getByStatus(true)!!
        val listProduk = myDb.daoKeranjang().getAll() as ArrayList
        var totalItem = 0
        var totalHarga = 0
        val catatan = edtCatatan.text.toString()
        val produks = ArrayList<Checkout.Item>()
        for(p in listProduk){
            if(p.selected){
                totalItem += p.jumlah
                totalHarga += (p.jumlah * Integer.valueOf(p.harga))

                val produk = Checkout.Item()
                produk.id = ""+ p.id
                produk.total_item = ""+ p.jumlah
                produk.total_harga = ""+ (p.jumlah * Integer.valueOf(p.harga))
                produk.catatan = catatan
                produks.add(produk)
            }
        }
        val checkout = Checkout()
        checkout.user_id = ""+ user.id
        checkout.total_item = ""+ totalItem
        checkout.total_harga = ""+ totalHarga
        checkout.name = a.name
        checkout.phone = a.phone
        checkout.jasa_pengiriman = jasaKirim
        checkout.ongkir = ongkir
        checkout.kurir = kurir
        checkout.detail_lokasi = tvAlamat.text.toString()
        checkout.total_transfer = "" + (totalHarga + Integer.valueOf(ongkir))
        checkout.produks = produks


        val json = Gson().toJson(checkout, Checkout::class.java)
        Log.d("Respon: ", "json: $json")
        val intent = Intent(this, PembayaranActivity::class.java)
        intent.putExtra("extra", json)
        startActivity(intent)

    }

    private fun getOngkir(kurir: String){
        val alamat = myDb.daoAlamat().getByStatus(true)
        val origin = "23"
        val destination =""+ alamat?.id_Kota.toString()
        val berat = 1000

        ApiConfigAlamat.instanceRetrofit.ongkir(ApiKey.key, origin, destination, berat, kurir.lowercase(Locale.getDefault())).enqueue(object : Callback<ResponOngkir> {
            override fun onResponse(call: Call<ResponOngkir>, response: Response<ResponOngkir>) {
                if (response.isSuccessful){
                    Log.d("Success", "berhasil memuat data")
                    val result = response.body()!!.rajaongkir.results
                    if (result.isNotEmpty()){
                        displayOngkir(result[0].code.uppercase(Locale.getDefault()), result[0].costs)
                    }

                }else{
                    Log.d("Error", "gagal memuat data: "+response.message())
                }


            }

            override fun onFailure(call: Call<ResponOngkir>, t: Throwable) {
                Log.d("Error", "gagal memuat data: "+t.message)
            }

        })
    }

    var ongkir = ""
    var kurir = ""
    var jasaKirim = ""
    private fun displayOngkir(_kurir:String, arrayList: ArrayList<Costs>){
        var arrayOngkir = ArrayList<Costs>()
        for (i in arrayList.indices){
            val ongkir = arrayList[i]
            if(i == 0){
                ongkir.isActive = true
            }
            arrayOngkir.add(ongkir)
        }
        setTotal(arrayOngkir[0].cost[0].value)
        ongkir = arrayOngkir[0].cost[0].value
        kurir = _kurir
        jasaKirim = arrayOngkir[0].service

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        var adapter:AdapterKurir? = null
        adapter = AdapterKurir(arrayOngkir, _kurir, object : AdapterKurir.Listeners{
            @SuppressLint("NotifyDataSetChanged")
            override fun onClicked(data: Costs, index: Int) {
                val newArrayOngkir = ArrayList<Costs>()
                for (ongkir in arrayOngkir){
                    ongkir.isActive = data.description == ongkir.description
                    newArrayOngkir.add(ongkir)
                }
                arrayOngkir = newArrayOngkir
                adapter!!.notifyDataSetChanged()
                setTotal(data.cost[0].value)

                ongkir = data.cost[0].value
                kurir = _kurir
                jasaKirim = data.service
            }

        })

        rvMetode.adapter = adapter
        rvMetode.layoutManager = layoutManager
    }

    fun setTotal(ongkir:String){
        tvOngkir.text = Helper().gantiRupiah(ongkir)
        tvTotal.text = Helper().gantiRupiah(Integer.valueOf(ongkir) + totalHarga)
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        chekAlamat()
        super.onResume()
    }
}