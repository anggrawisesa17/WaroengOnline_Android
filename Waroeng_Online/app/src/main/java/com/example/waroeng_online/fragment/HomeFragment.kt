package com.example.waroeng_online.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waroeng_online.R
import com.example.waroeng_online.R.id.*
import com.example.waroeng_online.adapter.AdapterProduk
import com.example.waroeng_online.app.ApiConfig
import com.example.waroeng_online.model.Produk
import com.example.waroeng_online.model.ResponModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private lateinit var rvProduk: RecyclerView
    private lateinit var rvProduklaris: RecyclerView
    private lateinit var rvElektronik: RecyclerView
    private lateinit var tvTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        getProduk()


        return view
    }

    fun displayProduk(){

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager2 = LinearLayoutManager(activity)
        layoutManager2.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager3 = LinearLayoutManager(activity)
        layoutManager3.orientation = LinearLayoutManager.HORIZONTAL

        rvProduk.adapter = AdapterProduk(requireActivity(), listProduk)
        rvProduk.layoutManager = layoutManager

        rvProduklaris.adapter = AdapterProduk(requireActivity(), listProduk)
        rvProduklaris.layoutManager = layoutManager2

        rvElektronik.adapter = AdapterProduk(requireActivity(), listProduk)
        rvElektronik.layoutManager = layoutManager3
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
                    displayProduk()
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {

            }

        })
    }

    private fun init(view: View){
        rvProduk = view.findViewById(rv_produk)
        rvProduklaris = view.findViewById(rv_produklaris)
        rvElektronik = view.findViewById(rv_elektronik)

        tvTitle = view.findViewById(tv_title)
        tvTitle.visibility = View.VISIBLE
    }
}