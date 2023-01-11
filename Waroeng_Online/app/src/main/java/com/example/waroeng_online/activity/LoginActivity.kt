package com.example.waroeng_online.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.waroeng_online.MainActivity
import com.example.waroeng_online.R
import com.example.waroeng_online.app.ApiConfig
import com.example.waroeng_online.helper.Sharedpref
import com.example.waroeng_online.model.ResponModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var s:Sharedpref
    private lateinit var btnl:Button
    private lateinit var edtemail:EditText
    private lateinit var edtpassword:EditText
    lateinit var progress:ProgressBar

    private lateinit var fcm: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        s = Sharedpref(this)
        btnl = findViewById(R.id.btn_login)
        edtemail = findViewById(R.id.edt_emaillog)
        edtpassword = findViewById(R.id.edt_passwordlog)
        progress = findViewById(R.id.pb)

        getFcm()

        btnl.setOnClickListener {
            login()
        }

    }

    private fun getFcm(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Respon", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            fcm = token.toString()

            // Log and toast
            Log.d("Respon fcm: ", token.toString())
        })
    }

    private fun login(){
        if(edtemail.text.isEmpty()) {
            edtemail.error = "Kolom email tidak boleh kosong"
            edtemail.requestFocus()
            return
        }else if(edtpassword.text.isEmpty()) {
            edtpassword.error = "Kolom password tidak boleh kosong"
            edtpassword.requestFocus()
            return
        }

        progress.visibility = View.VISIBLE

        ApiConfig.instanceRetrofit.login(edtemail.text.toString(), edtpassword.text.toString(), fcm).enqueue(object : Callback<ResponModel> {
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                progress.visibility = View.GONE
                val respon = response.body()!!

                if(respon.success == 1){
                    s.setStatuslogin(true)
                    s.setUser(respon.user)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this@LoginActivity, "Selamat Datang "+respon.user.name, Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@LoginActivity, "Error:"+respon.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                progress.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Error:"+t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}