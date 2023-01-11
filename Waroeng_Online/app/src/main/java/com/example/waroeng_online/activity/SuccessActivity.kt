package com.example.waroeng_online.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.waroeng_online.MainActivity
import com.example.waroeng_online.R
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.model.Bank
import com.example.waroeng_online.model.Checkout
import com.example.waroeng_online.model.Transaksi
import com.example.waroeng_online.room.MyDatabase
import com.google.gson.Gson

class SuccessActivity : AppCompatActivity() {
    private lateinit var tvNomorRekening: TextView
    private lateinit var tvNamaPenerima:TextView
    private lateinit var imageBank: ImageView
    private lateinit var tvNominal: TextView
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    private lateinit var btnCopyNorek: ImageView
    private lateinit var btnCopyNominal: ImageView
    private lateinit var btnCekStatus: Button

    private var nominal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        tvNomorRekening = findViewById(R.id.tv_nomorRekening)
        tvNamaPenerima = findViewById(R.id.tv_namaPenerima)
        imageBank = findViewById(R.id.image_bank)
        tvNominal = findViewById(R.id.tv_nominal)
        toolbar = findViewById(R.id.toolbar)
        btnCopyNorek = findViewById(R.id.btn_copyNoRek)
        btnCopyNominal = findViewById(R.id.btn_copyNominal)
        btnCekStatus = findViewById(R.id.btn_cekStatus)

        Helper().setToolbar(this, toolbar, "Bank Transfer")

        setValue()
        mainButton()
    }

    private fun mainButton(){
        btnCopyNorek.setOnClickListener {
            copyText(tvNomorRekening.text.toString())
        }

        btnCopyNominal.setOnClickListener {
            copyText(nominal.toString())
        }

        btnCekStatus.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }
    }

    private fun copyText(text:String){
        val copyManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copyText = ClipData.newPlainText("text", text)
        copyManager.setPrimaryClip(copyText)

        Toast.makeText( this,"text berhasil di copy", Toast.LENGTH_LONG).show()
    }

    fun setValue(){
        val jsbank = intent.getStringExtra("bank")
        val jsTransaksi = intent.getStringExtra("Transaksi")
        val jsCheckout = intent.getStringExtra("checkout")

        val bank = Gson().fromJson(jsbank, Bank::class.java)
        val transaksi = Gson().fromJson(jsTransaksi, Transaksi::class.java)
        val checkout = Gson().fromJson(jsCheckout, Checkout::class.java)

        //hapus keranjang
        val myDb = MyDatabase.getInstance(this)!!

        for(produk in checkout.produks){
            myDb.daoKeranjang().deleteById(produk.id)
        }

        tvNomorRekening.text = bank.rekening
        tvNamaPenerima.text = bank.penerima
        imageBank.setImageResource(bank.image)

        nominal = Integer.valueOf(transaksi.total_transfer) + transaksi.kode_unik
        tvNominal.text = Helper().gantiRupiah(nominal)
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }
}