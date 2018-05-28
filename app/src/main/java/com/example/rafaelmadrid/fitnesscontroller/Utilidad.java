package com.example.rafaelmadrid.fitnesscontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

public class Utilidad {
    public static int estaRepitiendo = 0;
    public static ArrayList<DispositivosEmparejados> dispositivosLista;
    public static DispositivosEmparejados dispositivoConectado;
    public static BluetoothAdapter defaultBluetooth(){
        return BluetoothAdapter.getDefaultAdapter();
    }
    public static void cargarDispositivos(BluetoothAdapter adapter){
        dispositivosLista = new ArrayList<>();
        Set<BluetoothDevice> emparejados = adapter.getBondedDevices();
        if (emparejados.size() > 0){
            for(BluetoothDevice dispositivos : emparejados){
                DispositivosEmparejados dispEmp = new DispositivosEmparejados(dispositivos.getAddress(), dispositivos.getName());
                dispositivosLista.add(dispEmp);
                Log.e("Dispositivos",dispEmp.getNombre());
            }
        }
    }

}
