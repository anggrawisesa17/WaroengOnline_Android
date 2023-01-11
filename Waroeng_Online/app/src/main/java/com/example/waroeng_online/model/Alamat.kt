package com.example.waroeng_online.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alamat")
class Alamat {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idTb")
    var idTb = 0

    var id = 0
    var name = ""
    var phone = ""
    var type = ""
    var alamat = ""

    var id_Provinsi = 0
    var id_Kota = 0
    var id_Kecamatan = 0
    var provinsi = ""
    var kota = ""
    var kecamatan = ""
    var kodePos = ""

    var isSelected = false
}