package com.example.rafaelmadrid.fitnesscontroller;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.itangqi.waveloadingview.WaveLoadingView;

public class comunicacion extends AsyncTask<Void,Void,Void> {

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Context context;

    public comunicacion(BluetoothSocket socket,Context context) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {}
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        this.context=context;
    }

    public int subemarea(int i,int total){
        int j=(i*100)/total;
        return j;
    }

    @Override
    protected Void doInBackground(Void... voids) {
       byte[] buffer=new byte[256];
       StringBuilder constructor = new StringBuilder();
        boolean Break=false;
        while(!Break){
            try{
                int bytes=mmInStream.read();
                String mensaje = new String(buffer, 0, bytes);
                constructor.append(mensaje);
                int finlinea = constructor.indexOf("*");
                if (finlinea > 0){
                    String datos = constructor.substring(0, finlinea);
                    Log.d("DATOS", datos);
                }
                Log.d("Recibido", ""+mensaje);
                if(bytes!=0){
                    Log.e("No recibido", bytes+"");
                    Common.estaRepitiendo += 1;
                    Break=true;
                }
            }catch (IOException E){
                break;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Toast.makeText(context,"repeticion hecha",Toast.LENGTH_SHORT).show();
    }

    public void write(String input) {
        byte[] bytes = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

}
