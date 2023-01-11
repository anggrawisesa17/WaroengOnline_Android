package com.example.waroeng_online.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.waroeng_online.R
import com.example.waroeng_online.adapter.AdapterProdukTransaksi
import com.example.waroeng_online.app.ApiConfig
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.model.DetailTransaksi
import com.example.waroeng_online.model.ResponModel
import com.example.waroeng_online.model.Transaksi
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.gson.Gson
import com.inyongtisto.myhelper.extension.showErrorDialog
import com.inyongtisto.myhelper.extension.showSuccessDialog
import com.inyongtisto.myhelper.extension.toGone
import com.inyongtisto.myhelper.extension.toMultipartBody
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class DetailTransaksiActivity : AppCompatActivity() {
    private lateinit var tvPenerima: TextView
    private lateinit var tvAlamat: TextView
    private lateinit var tvKodeUnik: TextView
    private lateinit var tvTotalBelanja: TextView
    private lateinit var tvOngkir: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvTGL: TextView
    private lateinit var rvProduk: RecyclerView
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    private lateinit var btnBatal: Button
    private lateinit var divFooter: LinearLayout
    private lateinit var btnBayar: Button
    private lateinit var tvCatatan: TextView

    private var transaksi = Transaksi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaksi)

        tvPenerima = findViewById(R.id.tv_penerima)
        tvAlamat = findViewById(R.id.tv_alamat)
        tvKodeUnik = findViewById(R.id.tv_kodeUnik)
        tvTotalBelanja = findViewById(R.id.tv_totalBelanja)
        tvOngkir = findViewById(R.id.tv_ongkir)
        tvTotal = findViewById(R.id.tv_total)
        tvStatus = findViewById(R.id.tv_status)
        tvTGL = findViewById(R.id.tv_tgl)
        rvProduk = findViewById(R.id.rv_produk)
        toolbar = findViewById(R.id.toolbar)
        btnBatal = findViewById(R.id.btn_batal)
        divFooter = findViewById(R.id.div_footer)
        btnBayar = findViewById(R.id.btn_bayar)

        val json = intent.getStringExtra("transaksi")
        transaksi = Gson().fromJson(json, Transaksi::class.java)
        Helper().setToolbar(this, toolbar, "Detail Transaksi")

        setData(transaksi)
        displayProduk(transaksi.details)
        mainButton()
    }

    private fun mainButton(){
        btnBatal.setOnClickListener {

            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Apakah Anda Yakin?")
                .setContentText("Transaksi Akan Dibatalkan Dan Tidak Bisa Dikembalikan!")
                .setConfirmText("Yes,Batalkan!")
                .setConfirmClickListener {
                    it.dismissWithAnimation()
                    batalTransaksi()
                }
                .setCancelText("Tutup")
                .setCancelClickListener {
                    it.dismissWithAnimation()
                }
                .show()
        }

        btnBayar.setOnClickListener {
            imagePicker()
        }
    }

    private fun imagePicker(){
        ImagePicker.with(this)
            .crop()
            .maxResultSize(512, 512)
            .provider(ImageProvider.BOTH) //Or bothCameraGallery()
            .createIntentFromDialog { launcher.launch(it) }
    }
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                // Use the uri to load the image
                Log.d("TAG", "URI IMAGE: $uri")
                val fileUri: Uri = uri
                dialogUpload(File(fileUri.path!!))
            }
        }


    var alertDialog: AlertDialog? = null

    @SuppressLint("InflateParams")
    private fun dialogUpload(file:File){
        val view = layoutInflater
        val layout = view.inflate(R.layout.view_upload, null)


        val imageView :ImageView = layout.findViewById(R.id.image)
        val btnUpload: Button = layout.findViewById(R.id.btn_upload)
        val btnImage: Button = layout.findViewById(R.id.btn_image_lain)

        Picasso.get()
            .load(file)
            .into(imageView)

        btnUpload.setOnClickListener {
            upload(file)
        }

        btnImage.setOnClickListener {
            imagePicker()
        }
        alertDialog = AlertDialog.Builder(this).create()
        alertDialog!!.setView(layout)
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()
    }

    @Suppress("DEPRECATION")
    private fun upload(file:File){
        val fileImage = file.toMultipartBody()
        ApiConfig.instanceRetrofit.uploadBukti(transaksi.id, fileImage!!).enqueue(object :
            Callback<ResponModel> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                if(response.isSuccessful){
                    if (response.body()!!.success == 1){
                        showSuccessDialog("Upload bukti berhasil"){
                            alertDialog!!.dismiss()
                            btnBayar.toGone()
                            tvStatus.text = "PROSES"
                            onBackPressed()
                        }
                    }else{
                        showErrorDialog(response.body()!!.message)
                    }

                }else{
                    showErrorDialog(response.message())
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                showErrorDialog(t.message!!)
            }

        })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun batalTransaksi(){
        val loading = SweetAlertDialog(this@DetailTransaksiActivity, SweetAlertDialog.PROGRESS_TYPE)
            loading.setTitleText("Loading...")
            .show()

        ApiConfig.instanceRetrofit.batalCheckout(transaksi.id).enqueue(object : Callback<ResponModel> {
            @Suppress("DEPRECATION")
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                loading.dismiss()
                val res = response.body()!!
                if (res.success == 1){
                    SweetAlertDialog(this@DetailTransaksiActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success!")
                        .setContentText("Transaksi Dibatalkan!")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            onBackPressed()
                        }
                        .show()
//                    Toast.makeText(this@DetailTransaksiActivity, "Transaksi berhasil dibatalkan", Toast.LENGTH_SHORT).show()

////                    displayRiwayat(res.Transaksis)
                }else{
                    error(res.message)
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                loading.dismiss()
                error(t.message.toString())
            }

        })
    }

    fun error(pesan:String){
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText(pesan)
            .show()
    }

    @SuppressLint("SetTextI18n")
    fun setData(t: Transaksi) {
        tvStatus.text = t.status
        val formatBaru = "dd MMMM yyyy, kk:mm:ss"
        tvTGL.text = Helper().convertTanggal(t.created_at, formatBaru)
        tvPenerima.text = t.name+" - "+t.phone
        tvAlamat.text = t.detail_lokasi
        tvKodeUnik.text = Helper().gantiRupiah(t.kode_unik)
        tvTotalBelanja.text = Helper().gantiRupiah(t.total_harga)
        tvOngkir.text = Helper().gantiRupiah(t.ongkir)
        tvTotal.text = Helper().gantiRupiah(t.total_transfer)

        if(t.status != "MENUNGGU") divFooter.visibility = View.GONE

        var color = getColor(R.color.search)
        if(t.status == "SELESAI") color = getColor(R.color.orange)
        else if(t.status == "BATAL") color = getColor(R.color.red)

        tvStatus.setTextColor(color)
    }

    private fun displayProduk(transaksis: ArrayList<DetailTransaksi>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rvProduk.adapter = AdapterProdukTransaksi(transaksis)
        rvProduk.layoutManager = layoutManager
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}