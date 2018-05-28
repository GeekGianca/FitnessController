package com.example.rafaelmadrid.fitnesscontroller;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.itangqi.waveloadingview.WaveLoadingView;

public class comunicacion extends Thread {

    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler recibeMesj;
    private Context contexto;
    private int datoRecibido;

    public comunicacion(BluetoothSocket socket,Context context, Handler handler, int dato) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        recibeMesj = handler;
        contexto = context;
        datoRecibido = dato;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(context, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[256];
        int bytes;
        Log.d("Accion","Run en hilo");
        if (recibeMesj != null){
            while (true){
                Log.d("Esperando","........");
                try{
                    bytes = mmInStream.read(buffer);
                    String leerMensj = new String(buffer, 0, bytes);
                    recibeMesj.obtainMessage(datoRecibido, bytes, -1, leerMensj).sendToTarget();
                }catch (IOException e) {
                    Toast.makeText(contexto, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void write(String input){
        try{
            mmOutStream.write(input.getBytes());
        }catch (IOException e){
            Log.e("IOExc",e.getMessage());
        }
    }
}
