<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Transaksi;
use App\Models\TransaksiDetail;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class TransaksiController extends Controller
{
    public function store(Request $request)
    {
        $validasi = Validator::make($request->all(), [
            'user_id' => 'required',
            'total_item' => 'required',
            'total_harga' => 'required',
            'name' => 'required',
            'jasa_pengiriman' => 'required',
            'ongkir' => 'required',
            'total_transfer' => 'required',
            'bank' => 'required',
            'phone' => 'required'
        ]);

        if ($validasi->fails()) {
            $val = $validasi->errors()->all();
            return $this->error($val[0]);
        }

        $kode_payment = "INV/PYM/" . now()->format('Y-m-d') . "/" . rand(100, 999);
        $kode_trx = "INV/PYM/" . now()->format('Y-m-d') . "/" . rand(100, 999);
        $kode_unik = rand(100, 999);
        $status = "MENUNGGU";
        $expired_at = now()->addDay();

        $dataTransaksi = array_merge($request->all(), [
            'kode_payment' => $kode_payment,
            'kode_trx' => $kode_trx,
            'kode_unik' => $kode_unik,
            'status' => $status,
            'expired_at' => $expired_at
        ]);

        \DB::beginTransaction();
        $transaksi = Transaksi::create($dataTransaksi);
        foreach($request->produks as $produk){
            $detail = [
                'transaksi_id' => $transaksi->id,
                'produk_id' => $produk['id'],
                'total_item' => $produk['total_item'],
                'catatan' => $produk['catatan'],
                'total_harga' => $produk['total_harga']
            ];
            $transaksiDetail = TransaksiDetail::create($detail);
        }
        
        if(!empty($transaksi)&& !empty($transaksiDetail)){
            \DB::commit();
            return response()->json([
                'success' => 1,
                'message' => 'Transaksi Berhasil',
                'Transaksi' => collect($transaksi)
            ]);
        }else{
            \DB::rollBack();
            $this->error('Transaksi Gagal');
        }
    }

    public function history($id){
        $transaksis = Transaksi::with(['user'])->whereHas('user', function ($query) use ($id) {
            $query->whereId($id);
        })->orderBy("id", "desc")->get();

        foreach($transaksis as $transaksi){
            $details = $transaksi->details;
            foreach($details as $detail){
                $detail->produk;
            }
        }

        if(!empty($transaksis)){
            return response()->json([
                'success' => 1,
                'message' => 'Transaksi Berhasil',
                'Transaksis' => collect($transaksis)
            ]);
        }else{
            $this->error('Transaksi Gagal');
        }
    }

    public function batal($id){
        $transaksi = Transaksi::with(['details.produk','user'])->where('id', $id)->first();
        if($transaksi){
            //update data

            $transaksi->update([
                'status' => "BATAL"
            ]);
            $this->pushNotif('Transaksi Dibatalkan', "Transaksi produk ".$transaksi->details[0]->produk->name." berhasil dibatalkan", $transaksi->user->fcm);

            return response()->json([
                'success' => 1,
                'message' => 'Berhasil',
                'Transaksi' => $transaksi
            ]);
        }else{
            return $this->error('Gagal Memuat Transaksi');
        }
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

    public function upload(Request $request, $id){

        $transaksi = Transaksi::with(['details.produk','user'])->where('id', $id)->first();
        if($transaksi){
            //update data
            $fileName = '';
            if($request->image->getClientOriginalName()){
                $file = str_replace(' ', '-',$request->image->getClientOriginalName());
                $fileName = date('mYdHs').rand(1,999).'_'.$file;
                $request->image->storeAs('public/transfer', $fileName);
            }else{
            return $this->error('Gagal Memuat data');
            }

            $transaksi->update([
                'status' => "DIPROSES"
            ]);
            $this->pushNotif('Transaksi Dibayar', "Transaksi produk ".$transaksi->details[0]->produk->name." berhasil dibayar", $transaksi->user->fcm);

            return response()->json([
                'success' => 1,
                'message' => 'Berhasil',
                'Transaksi' => $transaksi
            ]);
        }else{
            return $this->error('Gagal Memuat Transaksi');
        }
    }
    public function error($pesan){
        return response()->json([
            'success' => 0,
            'message' => $pesan
        ]);
    }
}
