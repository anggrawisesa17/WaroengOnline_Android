<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class UserController extends Controller
{
    public function login(Request $request){

        //dd($request->all());die();
        $user = User::where('email', $request->email)->first();

        if($user){
            $user->update([
                'fcm' => $request->fcm
            ]);

            if(password_verify($request->password, $user->password)){
                return response()->json([
                    'success' => 1,
                    'message' => 'selamat datang '.$user->name,
                    'user' => $user
                ]);
            }
            return $this->error('password salah');
        }
        return $this->error('Email tidak terdaftar');
    }
    
    public function register(Request $request){
        $validasi = Validator::make($request->all(), [
            'name' => 'required',
            'email' => 'required|email|unique:users',
            'Phone' => 'required|max:13|unique:users',
            'password' => 'required|min:6'
        ]);

        if ($validasi->fails()){
            $val = $validasi->errors()->all();
            return $this->error($val[0]);
        }

        $user = User::create(array_merge($request->all(), [
            'password' => bcrypt($request->password)
        ]));

        if($user){
            return response()->json([
                'success' => 1,
                'message' => 'selamat datang register berhasil',
                'user' => $user
            ]);
        }

        return $this->error('registrasi gagal');
    }

    public function upload(Request $request, $id){
        $user = User::where('id', $id)->first();
        if($user){
            $fileName = '';
            if($request->image->getClientOriginalName()){
                $file = str_replace(' ', '-',$request->image->getClientOriginalName());
                $fileName = date('mYdHs').rand(1,999).'_'.$file;
                $request->image->storeAs('public/user', $fileName);
            }else{
                return $this->error("image wajib ada");
            }

            $user->update([
                'image'=> $fileName
            ]);
            // return $this->success($user);
            return response()->json([
                'success' => 1,
                'message' => 'Berhasil',
                'image' => $fileName
            ]);
        }
        return $this->error("user tidak di temukan");
    }

    public function success($pesan){
        return response()->json([
            'success' => 1,
            'message' => $pesan
        ]);
    }

    public function error($pesan){
        return response()->json([
            'success' => 0,
            'message' => $pesan
        ]);
    }
}
