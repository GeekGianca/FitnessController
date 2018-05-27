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
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ConectBluetooth extends AsyncTask<Void,Void,Void>{

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean ConnectSuccess = true;
    private Context context;
    private BluetoothSocket btSocket;
    private BluetoothAdapter myBluetooth;
    private BluetoothDevice bluetoothDevice;
    private comunicacion comunicacion;

    public ConectBluetooth(Context context, BluetoothAdapter bluetoothAdapter){
        this.context=context;
        btSocket=null;
        myBluetooth=bluetoothAdapter;
        this.bluetoothDevice=null;
    }

    public com.example.rafaelmadrid.fitnesscontroller.comunicacion getComunicacion() {
        return comunicacion;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public BluetoothAdapter getMyBluetooth() {
        return myBluetooth;
    }

    protected void onPreExecute()
    {
        Toast.makeText(context,"Connecting....",Toast.LENGTH_LONG).show();
    }

    public boolean isConnectSuccess() {
        return ConnectSuccess;
    }

    protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
    {
        try
        {
            if (btSocket == null && bluetoothDevice!=null )
            {
                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(bluetoothDevice.getAddress());
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                myBluetooth.cancelDiscovery();
                btSocket.connect();//start connection
                comunicacion=new comunicacion(btSocket,context);
            }
        }
        catch (IOException e)
        {
            ConnectSuccess = false;//if the try failed, you can check the exception here
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
    {
        super.onPostExecute(result);
        if (!ConnectSuccess)
        {
            Toast.makeText(context,"Connection Failed",Toast.LENGTH_SHORT).show();
        }
    }

    public void desconectar() throws IOException {
        if(btSocket.isConnected()){
            btSocket.close();
            btSocket=null;
            bluetoothDevice=null;
        }
    }


}


