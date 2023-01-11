package com.example.waroeng_online.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.waroeng_online.R
import com.example.waroeng_online.helper.Sharedpref

class MasukActivity : AppCompatActivity() {

    private lateinit var s:Sharedpref
    private lateinit var btnl:Button
    private lateinit var btnr:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masuk)

        s = Sharedpref(this)

        mainButton()
    }

    private fun mainButton(){
        btnl = findViewById(R.id.btn_login)

        btnl.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnr = findViewById(R.id.btn_regis)

        btnr.setOnClickListener {
            startActivity(Intent(this, RegisActivity::class.java))
        }
    }
}