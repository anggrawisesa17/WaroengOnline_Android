package com.example.waroeng_online.fragment
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.waroeng_online.R
import com.example.waroeng_online.R.id.*
import com.example.waroeng_online.activity.MasukActivity
import com.example.waroeng_online.activity.RiwayatActivity
import com.example.waroeng_online.app.ApiConfig
import com.example.waroeng_online.helper.Sharedpref
import com.example.waroeng_online.model.ResponModel
import com.example.waroeng_online.util.Config
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.inyongtisto.myhelper.extension.*
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class AkunFragment : Fragment() {

    private lateinit var s:Sharedpref
    private lateinit var btnLogout:TextView
    private lateinit var tvNama:TextView
    private lateinit var tvEmail:TextView
    private lateinit var tvInisial:TextView
    private lateinit var imageProfil: ImageView

    private lateinit var btnRiwayat:RelativeLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_akun, container, false)

        init(view)

        s = Sharedpref(requireActivity())
        setData()
        mainButton()

        return view
    }

    private fun mainButton(){
        btnLogout.setOnClickListener{
            s.setStatuslogin(false)
            startActivity(Intent(activity, MasukActivity::class.java))
        }

        btnRiwayat.setOnClickListener {
            startActivity(Intent(requireActivity(), RiwayatActivity::class.java))
        }

        imageProfil.setOnClickListener {
            imagePicker()
        }
    }

    private fun setData(){
        if(s.getUser() == null){
            val intent = Intent(activity, MasukActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return
        }
        val user = s.getUser()!!
        tvNama.text = user.name
        tvEmail.text = user.email
        tvInisial.text = user.name.getInitial()

        Picasso.get().load(Config.baseUrl+"/storage/user/" +user.image).into(imageProfil)
    }

    private fun imagePicker(){
        ImagePicker.with(requireActivity())
            .crop()
            .maxResultSize(512, 512, true)
            .provider(ImageProvider.BOTH) //Or bothCameraGallery()
            .createIntentFromDialog { launcher.launch(it) }
    }
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                // Use the uri to load the image
                val fileUri: Uri = uri
                Picasso.get().load(File(fileUri.path!!)).into(imageProfil)
                dialogUpload(File(fileUri.path!!))

            }
        }

    var alertDialog: AlertDialog? = null
    @SuppressLint("InflateParams")
    private fun dialogUpload(file:File){
        val view = layoutInflater
        val layout = view.inflate(R.layout.view_upload, null)


        val imageView :ImageView = layout.findViewById(image)
        val btnUpload: Button = layout.findViewById(btn_upload)
        val btnImage: Button = layout.findViewById(btn_image_lain)

        Picasso.get()
            .load(file)
            .into(imageView)

        btnUpload.setOnClickListener {
            upload(file)
        }

        btnImage.setOnClickListener {
            imagePicker()
        }
        alertDialog = AlertDialog.Builder(requireActivity()).create()
        alertDialog!!.setView(layout)
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()
    }

    @Suppress("DEPRECATION")
    private fun upload(file: File){
        val user = s.getUser()
        val fileImage = file.toMultipartBody("image")
        ApiConfig.instanceRetrofit.uploadUser(user!!.id, fileImage!!).enqueue(object :
            Callback<ResponModel> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                if(response.isSuccessful){
                    if (response.body()!!.success == 1){
                        showSuccessDialog("Upload foto profil berhasil"){
                            alertDialog!!.dismiss()
                            Picasso.get().load(File(file.path)).into(imageProfil)
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

    private fun init(view: View) {
        btnLogout = view.findViewById(btn_logout)
        tvNama = view.findViewById(tv_Nama)
        tvEmail = view.findViewById(tv_email)
        btnRiwayat = view.findViewById(btn_riwayat)
        tvInisial = view.findViewById(tv_inisial)
        imageProfil = view.findViewById(image_profile)
    }

    override fun onResume() {

        super.onResume()
    }
}