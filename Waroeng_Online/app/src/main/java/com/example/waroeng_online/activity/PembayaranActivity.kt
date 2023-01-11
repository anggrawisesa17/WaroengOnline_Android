package com.example.waroeng_online.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.waroeng_online.R
import com.example.waroeng_online.adapter.AdapterBank
import com.example.waroeng_online.app.ApiConfig
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.model.Bank
import com.example.waroeng_online.model.Checkout
import com.example.waroeng_online.model.ResponModel
import com.example.waroeng_online.model.Transaksi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PembayaranActivity : AppCompatActivity() {
    private lateinit var rvData: RecyclerView
    lateinit var toolbar:androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)

        rvData = findViewById(R.id.rv_data)
        toolbar = findViewById(R.id.toolbar)

        Helper().setToolbar(this, toolbar, "Pembayaran")

        displayBank()
    }

    private fun displayBank(){
        val arrBank = ArrayList<Bank>()
        arrBank.add(Bank("BCA", "9874109324780", "Sesa", R.drawable.bca))
        arrBank.add(Bank("Mandiri", "9875289120", "Sesa", R.drawable.mandiri))
        arrBank.add(Bank("BRI", "48972968510", "Sesa", R.drawable.bri))

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvData.layoutManager = layoutManager
        rvData.adapter = AdapterBank(arrBank, object : AdapterBank.Listeners{
            override fun onClicked(data: Bank, index: Int) {
                bayar(data)
            }

        })
    }

    private fun bayar(bank: Bank){
        val json = intent.getStringExtra("extra")!!.toString()
        val checkout = Gson().fromJson(json, Checkout::class.java)
        checkout.bank = bank.nama

        val loading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        loading.setTitleText("Loading...")
            .show()

        ApiConfig.instanceRetrofit.checkout(checkout).enqueue(object : Callback<ResponModel> {
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                loading.dismiss()
                if(!response.isSuccessful){
                    error(response.message())
                }
                val respon = response.body()!!

                if(respon.success == 1){
                    val jsBank = Gson().toJson(bank, Bank::class.java)
                    val jsTransaksi = Gson().toJson(respon.Transaksi, Transaksi::class.java)
                    val jsCheckout = Gson().toJson(checkout, Checkout::class.java)

                    val intent = Intent(this@PembayaranActivity, SuccessActivity::class.java)
                    intent.putExtra("bank", jsBank)
                    intent.putExtra("Transaksi", jsTransaksi)
                    intent.putExtra("checkout", jsCheckout)
                    startActivity(intent)
                }else{
                    error(respon.message)
//                    Toast.makeText(this@PembayaranActivity, "Error:"+respon.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                loading.dismiss()
                error(t.message.toString())
//                Toast.makeText(this@PembayaranActivity, "Error:"+t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun error(pesan:String){
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText(pesan)
            .show()
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}