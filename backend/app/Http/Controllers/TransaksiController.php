<?php

namespace App\Http\Controllers;

use App\Models\Transaksi;
use Illuminate\Http\Request;

class TransaksiController extends Controller
{
    public function index(){
        $transaksiPending['listPending'] = Transaksi::whereStatus("MENUNGGU")->get();
        $transaksiSelesai['listDone'] = Transaksi::where("Status", "NOT LIKE", "%MENUNGGU%")->get();
        return view('transaksi')->with($transaksiPending)->with($transaksiSelesai);
    }

    public function batal($id){
        $transaksi = Transaksi::with(['details.produk'])->where('id', $id)->first();
        $this->pushNotif('Transaksi Dibatalkan', "Transaksi produk ".$transaksi->details[0]->produk->name." berhasil dibatalkan", $transaksi->user->fcm);
        $transaksi->update([
            'status' => "BATAL"
        ]);
        return redirect('transaksi');
    }

    public function confirm($id){
        $transaksi = Transaksi::with(['details.produk'])->where('id', $id)->first();
        $this->pushNotif('Transaksi Diproses', "Transaksi produk ".$transaksi->details[0]->produk->name." Sedang diproses", $transaksi->user->fcm);
        $transaksi->update([
            'status' => "PROSES"
        ]);
        return redirect('transaksi');
    }

    public function kirim($id){
        $transaksi = Transaksi::with(['details.produk'])->where('id', $id)->first();
        $this->pushNotif('Transaksi Dikirim', "Transaksi produk ".$transaksi->details[0]->produk->name." Sedang dalam pengiriman", $transaksi->user->fcm);
        $transaksi->update([
            'status' => "DIKIRIM"
        ]);
        return redirect('transaksi');
    }

    public function selesai($id){
        $transaksi = Transaksi::where('id', $id)->first();
        $this->pushNotif('Transaksi Selesai', "Transaksi produk ".$transaksi->details[0]->produk->name." Sudah Selesai", $transaksi->user->fcm);
        $transaksi->update([
            'status' => "SELESAI"
        ]);
        
        return redirect('transaksi');
    }

    public function pushNotif($title, $message, $mfcm) {

        $mData = [
            'title' => $title,
            'body' => $message
        ];

         $fcm[] = $mfcm;

        $payload = [
            'registration_ids' => $fcm,
            'notification' => $mData
        ];

        $curl = curl_init();
        curl_setopt_array($curl, array(
            CURLOPT_URL => "https://fcm.googleapis.com/fcm/send",
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_ENCODING => "",
            CURLOPT_MAXREDIRS => 10,
            CURLOPT_TIMEOUT => 0,
            CURLOPT_FOLLOWLOCATION => true,
            CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
            CURLOPT_CUSTOMREQUEST => "POST",
            CURLOPT_HTTPHEADER => array(
                "Content-type: application/json",
                "Authorization: key=AAAAwSZg5Fg:APA91bE-GxksnvFasZQ51AFOhGK8UujxP8erFCXm9EY2E66r5sVrn97ETfRHQjxUxJmqLFc8VXJs0id4q66P5k-1U36BZ0ZhBWEfaG6ajJDMtSURSlYpMxoOFLVttH-6t7rsi6KlAK2K"
            ),
        ));
        curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($payload));

        $response = curl_exec($curl);
        curl_close($curl);

        $data = [
            'success' => 1,
            'message' => "Push notif success",
            'data' => $mData,
            'firebase_response' => json_decode($response)
        ];
        return $data;
    }
}

