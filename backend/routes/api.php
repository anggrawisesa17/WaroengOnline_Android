<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

//Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    //return $request->user();
//});

Route::post('login', 'App\Http\Controllers\Api\UserController@login');
Route::post('register', 'App\Http\Controllers\Api\UserController@register');
Route::post('upload/{id}', 'App\Http\Controllers\Api\UserController@upload');
Route::get('produk', 'App\Http\Controllers\Api\ProdukController@index');
Route::post('checkout', 'App\Http\Controllers\Api\TransaksiController@store');
Route::get('checkout/user/{id}', 'App\Http\Controllers\Api\TransaksiController@history');
Route::post('checkout/batal/{id}', 'App\Http\Controllers\Api\TransaksiController@batal');
Route::post('checkout/upload/{id}', 'App\Http\Controllers\Api\TransaksiController@upload');

Route::post('push', 'App\Http\Controllers\Api\TransaksiController@pushNotif');