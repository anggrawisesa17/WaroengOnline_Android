package com.example.waroeng_online.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.example.waroeng_online.R
import com.example.waroeng_online.app.ApiConfigAlamat
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.model.Alamat
import com.example.waroeng_online.model.ModelAlamat
import com.example.waroeng_online.model.ResponModel
import com.example.waroeng_online.room.MyDatabase
import com.example.waroeng_online.util.ApiKey
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahAlamatActivity : AppCompatActivity() {

    var provinsi = ModelAlamat.Provinsi()
    var kota = ModelAlamat.Provinsi()

    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var spnProvinsi: Spinner
    lateinit var divProvinsi: RelativeLayout
    lateinit var pb: ProgressBar
    lateinit var spnKota: Spinner
    lateinit var divKota: RelativeLayout
    private lateinit var btnSimpan: Button
    private lateinit var edtNama: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtAlamat: EditText
    private lateinit var edtType: EditText
    private lateinit var edtKodePos: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_alamat)

        toolbar = findViewById(R.id.toolbar)
        spnProvinsi = findViewById(R.id.spn_provinsi)
        divProvinsi = findViewById(R.id.div_provinsi)
        pb = findViewById(R.id.pb)
        spnKota = findViewById(R.id.spn_kota)
        divKota = findViewById(R.id.div_kota)
        btnSimpan = findViewById(R.id.btn_simpan)
        edtNama = findViewById(R.id.edt_nama)
        edtPhone = findViewById(R.id.edt_phone)
        edtAlamat = findViewById(R.id.edt_alamat)
        edtType = findViewById(R.id.edt_type)
        edtKodePos = findViewById(R.id.edt_kodePos)

        Helper().setToolbar(this, toolbar, "Tambah Alamat")

        mainButton()
        getProvinsi()
    }

    private fun mainButton(){
        btnSimpan.setOnClickListener {
            simpan()
        }
    }

    private fun simpan() {
        if (edtNama.text.isEmpty()) {
            error(edtNama)
            return
        } else if (edtPhone.text.isEmpty()) {
            error(edtPhone)
            return
        } else if (edtAlamat.text.isEmpty()) {
            error(edtAlamat)
            return
        } else if (edtType.text.isEmpty()) {
            error(edtType)
            return
        } else if (edtKodePos.text.isEmpty()) {
            error(edtKodePos)
            return
        } else if (provinsi.province_id == "0"){
            toast("silahkan pilih provinsi Anda")
            return
        } else if (kota.city_id == "0"){
            toast("silahkan pilih kota Anda")
            return
        }

        val alamat = Alamat()
        alamat.name = edtNama.text.toString()
        alamat.phone = edtPhone.text.toString()
        alamat.alamat = edtAlamat.text.toString()
        alamat.type = edtType.text.toString()
        alamat.kodePos = edtKodePos.text.toString()

        alamat.id_Provinsi = Integer.valueOf(provinsi.province_id)
        alamat.provinsi = provinsi.province
        alamat.id_Kota = Integer.valueOf(kota.city_id)
        alamat.kota = kota.city_name

        insert(alamat)

    }

    private fun toast(string: String){
        Toast.makeText(this,string, Toast.LENGTH_SHORT).show()
    }

    private fun error(editText: EditText){
        editText.error = "Kolom tidak boleh kosong"
        editText.requestFocus()
    }

    private fun getProvinsi(){
        ApiConfigAlamat.instanceRetrofit.getProvinsi(ApiKey.key).enqueue(object : Callback<ResponModel> {
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                if (response.isSuccessful){
                    pb.visibility = View.GONE
                    divProvinsi.visibility = View.VISIBLE
                    val res = response.body()!!
                    val arrString = ArrayList<String>()
                    arrString.add("Pilih Provinsi")
                    val listProvinsi = res.rajaongkir.results
                    for (prov in listProvinsi){
                        arrString.add(prov.province)
                    }

                    val adapter = ArrayAdapter<Any>(this@TambahAlamatActivity, R.layout.item_spinner, arrString.toTypedArray())
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spnProvinsi.adapter = adapter

                    spnProvinsi.onItemSelectedListener = object : OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            if(position != 0){
                                provinsi = listProvinsi[position -1]
                                val idProv = provinsi.province_id
                                getKota(idProv)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                    }
                }else{
                    Log.d("Error", "gagal memuat data: "+response.message())
                }


            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }

        })
    }

    private fun getKota(id: String) {
        pb.visibility = View.VISIBLE
        ApiConfigAlamat.instanceRetrofit.getKota(ApiKey.key, id).enqueue(object : Callback<ResponModel> {
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                if (response.isSuccessful){
                    pb.visibility = View.GONE
                    divKota.visibility = View.VISIBLE

                    val res = response.body()!!
                    val listArray = res.rajaongkir.results
                    val arrString = ArrayList<String>()
                    arrString.add("Pilih Kota")
                    for (kota in listArray){
                        arrString.add(kota.type+" "+kota.city_name)
                    }

                    val adapter = ArrayAdapter<Any>(this@TambahAlamatActivity, R.layout.item_spinner, arrString.toTypedArray())
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spnKota.adapter = adapter

                    spnKota.onItemSelectedListener = object : OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            if(position != 0){
                                kota = listArray[position -1]
                                val kodePos = kota.postal_code
                                edtKodePos.setText(kodePos)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                    }
                }else{
                    Log.d("Error", "gagal memuat data: "+response.message())
                }


            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }

        })
    }

    @Suppress("DEPRECATION")
    private fun insert(data: Alamat){
        val myDb = MyDatabase.getInstance(this)!!
        if(myDb.daoAlamat().getByStatus(true) == null){
            data.isSelected = true
        }
        CompositeDisposable().add(Observable.fromCallable { myDb.daoAlamat().insert(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                toast("Insert Data Success")
                onBackPressed()
            })
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}