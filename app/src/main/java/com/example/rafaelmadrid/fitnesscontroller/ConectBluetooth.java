package com.example.rafaelmadrid.fitnesscontroller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ConectBluetooth{

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Handler recEnvDatos;
    private BluetoothSocket connSocketBt;
    private int datoRecibido;
    private String macDispositivo;
    private BluetoothDevice dispositivo;
    private Context contexto;
    private boolean conectado;

    public ConectBluetooth(Context contexto) {
        this.contexto = contexto;
    }

    public void setOnCallConnection(String mac){
        dispositivo = Utilidad.defaultBluetooth().getRemoteDevice(mac);
    }

    public void desconectar(){
        try{
            if (connSocketBt.isConnected()){
                connSocketBt.close();
                connSocketBt = null;
                dispositivo = null;
                onPostExecute("Bluetooth Desconectado");
            }
        } catch (IOException e) {
            Log.e("IOException",e.getMessage());
        }
    }

    public void execute() {
        try{
            Utilidad.defaultBluetooth().cancelDiscovery();
            connSocketBt = dispositivo.createRfcommSocketToServiceRecord(myUUID);
            connSocketBt.connect();
            conectado = true;
            onPostExecute("Bluetooth conectado");
        } catch (IOException e) {
            Log.e("IOException",e.getMessage());
            onPostExecute("No se pudo conectar el Bluetooth");
            conectado = false;
        }
    }


    public void onPostExecute(String s) {
        Toast.makeText(contexto, s, Toast.LENGTH_SHORT).show();
    }

    public Handler getRecEnvDatos() {
        return recEnvDatos;
    }

    public BluetoothSocket getConnSocketBt() {
        return connSocketBt;
    }

    public int getDatoRecibido() {
        return datoRecibido;
    }

    public String getMacDispositivo() {
        return macDispositivo;
    }

    public boolean isConectado() {
        return conectado;
    }
}


