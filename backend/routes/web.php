<?php

use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

Auth::routes();

Route::get('/home', [App\Http\Controllers\HomeController::class, 'index'])->name('home');
Route::resource('/user', 'App\Http\Controllers\UserController');
Route::resource('/produk', 'App\Http\Controllers\ProdukController');
Route::resource('/transaksi', 'App\Http\Controllers\TransaksiController');
Route::get('/transaksi/batal/{id}', [App\Http\Controllers\TransaksiController::class, 'batal'])->name('transaksiBatal');
Route::get('/transaksi/confirm/{id}', [App\Http\Controllers\TransaksiController::class, 'confirm'])->name('transaksiConfirm');
Route::get('/transaksi/kirim/{id}', [App\Http\Controllers\TransaksiController::class, 'kirim'])->name('transaksiKirim');
Route::get('/transaksi/selesai/{id}', [App\Http\Controllers\TransaksiController::class, 'selesai'])->name('transaksiSelesai');