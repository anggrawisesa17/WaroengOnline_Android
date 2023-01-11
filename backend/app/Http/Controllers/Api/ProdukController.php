<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Produk;

class ProdukController extends Controller
{
    public function index(Request $request){

        //dd($request->all());die();
        $produk = Produk::all();

        return response()->json([
            'success' => 1,
            'message' => 'get produk berhasil ',
            'produks' => $produk
        ]);
    }
}
