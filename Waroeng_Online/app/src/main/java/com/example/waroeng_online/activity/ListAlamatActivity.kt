package com.example.waroeng_online.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.waroeng_online.R
import com.example.waroeng_online.adapter.AdapterAlamat
import com.example.waroeng_online.helper.Helper
import com.example.waroeng_online.model.Alamat
import com.example.waroeng_online.room.MyDatabase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ListAlamatActivity : AppCompatActivity() {
    private lateinit var btnBuatAlamat:Button
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    private lateinit var divKosong: LinearLayout
    private lateinit var rvAlamat: androidx.recyclerview.widget.RecyclerView

    lateinit var myDb : MyDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_alamat)
        btnBuatAlamat = findViewById(R.id.btn_buatAlamat)
        toolbar = findViewById(R.id.toolbar)
        divKosong = findViewById(R.id.div_kosong)
        rvAlamat = findViewById(R.id.rv_alamat)

        Helper().setToolbar(this, toolbar, "Pilih Alamat")
        myDb = MyDatabase.getInstance(this)!!

        mainButton()
    }

    private fun displayAlamat(){
        val arrayList = myDb.daoAlamat().getAll() as ArrayList

        if(arrayList.isEmpty()) divKosong.visibility = View.VISIBLE
        else divKosong.visibility = View.GONE

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rvAlamat.adapter = AdapterAlamat(arrayList, object : AdapterAlamat.Listeners{
            @Suppress("DEPRECATION")
            override fun onClicked(data: Alamat) {
                if(myDb.daoAlamat().getByStatus(true) != null){
                    val alamatActive = myDb.daoAlamat().getByStatus(true)!!
                    alamatActive.isSelected = false
                    updateActive(alamatActive,data)
                }

            }

        })
        rvAlamat.layoutManager = layoutManager
    }

    private fun updateActive(dataActive: Alamat, dataNonActive: Alamat){
        CompositeDisposable().add(Observable.fromCallable { myDb.daoAlamat().update(dataActive) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                updateNonActive(dataNonActive)
            })
    }

    @Suppress("DEPRECATION")
    private fun updateNonActive(data: Alamat){
        data.isSelected = true
        CompositeDisposable().add(Observable.fromCallable { myDb.daoAlamat().update(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onBackPressed()
            })
    }

    override fun onResume() {
        displayAlamat()
        super.onResume()
    }

    private fun mainButton(){
        btnBuatAlamat.setOnClickListener {
            startActivity(Intent(this, TambahAlamatActivity::class.java))
        }
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}