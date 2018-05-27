package com.example.rafaelmadrid.fitnesscontroller;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

@SuppressLint("ValidFragment")
public class Settings extends Fragment {

    private ConectBluetooth conectBluetooth;
    private String Lista[];
    private Spinner listspinner;

    @SuppressLint("ValidFragment")
    public Settings(ConectBluetooth conectBluetooth) {
        this.conectBluetooth=conectBluetooth;
    }

    public ConectBluetooth getConectBluetooth() {
        return conectBluetooth;
    }

    public void setConectBluetooth(ConectBluetooth conectBluetooth) {
        this.conectBluetooth = conectBluetooth;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_lay_fragment,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        crearlista();
        conectar();
        Desconectar();
        refrescar();
    }

    private void refrescar() {
        Button refresh=getActivity().findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listspinner.isEnabled()){
                    crearlista();
                }
            }
        });
    }

    public void conectar(){
        final Button conect=getActivity().findViewById(R.id.conectar);
        conect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!Lista[0].equals("No hay dispositivos emparejados")) {
                   String name = Lista[listspinner.getSelectedItemPosition()];
                   StringTokenizer stringTokenizer = new StringTokenizer(name);
                   String namedisp = stringTokenizer.nextToken("\n");
                   String address = stringTokenizer.nextToken();
                   if (namedisp.equals("FITNESSCONTROLLER")) {
                       Set<BluetoothDevice> dispositivosenlazados = conectBluetooth.getMyBluetooth().getBondedDevices();
                       if (dispositivosenlazados.size() > 0) {
                           Lista = new String[dispositivosenlazados.size()];
                           for (BluetoothDevice device : dispositivosenlazados) {
                               if (device.getAddress().equals(address)) {
                                   BluetoothAdapter adapter=conectBluetooth.getMyBluetooth();
                                   conectBluetooth=new ConectBluetooth(getActivity(),adapter);
                                   conectBluetooth.setBluetoothDevice(device);
                                   conectBluetooth.execute();
                                   conectBluetooth.onPostExecute(null);

                               }
                           }
                       }
                       if (conectBluetooth.isConnectSuccess()) {
                           listspinner.setEnabled(false);
                       }
                   } else {
                       new AlertDialog.Builder(getActivity())
                               .setTitle("Error")
                               .setMessage("Dispositivo no Compatible")
                               .setPositiveButton(R.string.acept, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) { }
                               })
                               .create()
                               .show();
                   }
               }else{
                   new AlertDialog.Builder(getActivity())
                           .setMessage("Active la conexion Bluetooth o refresque la lista de dispositivos emparejados primero")
                           .setPositiveButton(R.string.acept, new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) { }
                           })
                           .create()
                           .show();
               }
            }
        });
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.setTitle("Ajustes");
    }

    public void Desconectar(){
        Button desconectar=getActivity().findViewById(R.id.desconectar);
        desconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conectBluetooth.getBtSocket()!=null) {
                    try {
                        conectBluetooth.desconectar();
                        if (!listspinner.isEnabled()) {
                            listspinner.setEnabled(true);
                        }
                        crearlista();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public void crearlista(){
        listspinner= getActivity().findViewById(R.id.list_disp);
        if(conectBluetooth.getBtSocket()==null) {
            Set<BluetoothDevice> dispositivosenlazados = conectBluetooth.getMyBluetooth().getBondedDevices();
            int i = 0;
            if (dispositivosenlazados.size() > 0) {
                Lista = new String[dispositivosenlazados.size()];
                for (BluetoothDevice device : dispositivosenlazados) {
                    Lista[i] = device.getName() + "\n" + device.getAddress();
                    i++;
                }
            } else {
                Lista = new String[1];
                Lista[0] = "No hay dispositivos emparejados";
            }
            ArrayAdapter nuevoadapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, Lista);
            listspinner = getActivity().findViewById(R.id.list_disp);
            listspinner.setAdapter(nuevoadapter);
        }else{
            Lista=new String[1];
            Lista[0]=conectBluetooth.getBluetoothDevice().getName()+"\n"+conectBluetooth.getBluetoothDevice().getAddress();
            ArrayAdapter nuevoadapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, Lista);
            listspinner = getActivity().findViewById(R.id.list_disp);
            listspinner.setAdapter(nuevoadapter);
        }
    }
}
