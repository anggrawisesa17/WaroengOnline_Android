package com.example.waroeng_online.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.waroeng_online.model.User
import com.google.gson.Gson

class Sharedpref(activity: Activity) {
    private val mypref = "MAIN_PRF"
    private val sp: SharedPreferences
    private val login = "login"
    val name = "name"

    private val user = "user"

    init {
        sp = activity.getSharedPreferences(mypref, Context.MODE_PRIVATE)
    }

    fun setStatuslogin(status:Boolean){
        sp.edit().putBoolean(login, status).apply()
    }

    fun getStatusLogin():Boolean{
        return sp.getBoolean(login, false)
    }

    fun setUser(value:User){
        val data:String = Gson().toJson(value, User::class.java)
        sp.edit().putString(user, data).apply()
    }

    fun getUser(): User?{
        val data:String = sp.getString(user,null) ?: return null
        return Gson().fromJson(data, User::class.java)
    }
}