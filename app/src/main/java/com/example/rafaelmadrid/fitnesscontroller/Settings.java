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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

@SuppressLint("ValidFragment")
public class Settings extends Fragment implements View.OnClickListener {

    private String lista[];
    private Spinner listspinner;
    private Button conectar;
    private Button desconectar;
    private ConectBluetooth conectBluetooth;
    private DispositivosEmparejados dispEmp;

    public Settings() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_lay_fragment,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        conectar = (Button)view.findViewById(R.id.conectar);
        desconectar = (Button)view.findViewById(R.id.desconectar);
        conectar.setOnClickListener(this);
        desconectar.setOnClickListener(this);
        listspinner = (Spinner)view.findViewById(R.id.list_disp);
        cargaralalista();

        ArrayAdapter<String> adaptadorLista = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, lista);
        listspinner.setAdapter(adaptadorLista);

        listspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dispEmp = new DispositivosEmparejados(Utilidad.dispositivosLista.get(position).getMac(),Utilidad.dispositivosLista.get(position).getNombre());
                Log.d("SELECCIONADO", String.valueOf(Utilidad.dispositivosLista.get(position).getNombre()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("SIN SELECCION","0");
            }
        });

    }

    private void cargaralalista() {
        int i = 0;
        if (Utilidad.dispositivosLista.size() > 0){
            lista = new String[Utilidad.dispositivosLista.size()];
            for (DispositivosEmparejados disp : Utilidad.dispositivosLista){
                lista[i] = disp.getNombre() + "-"+disp.getMac();
                i ++;
                Log.d("Dispositivos Settings", disp.getNombre() + "-" + disp.getMac());
            }
        }else{
            lista[i] = "Sin dispositivos";
            Toast.makeText(getActivity().getBaseContext(), "No hay dispositivos emparejados", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Vista","Ejecutando....");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.conectar:
                if (Utilidad.defaultBluetooth() != null){
                    conectBluetooth = new ConectBluetooth(getContext());
                    setConectBluetooth(conectBluetooth);
                    conectBluetooth.setOnCallConnection(dispEmp.getMac());
                    conectBluetooth.execute();
                    if (conectBluetooth.isConectado()){
                        Utilidad.dispositivoConectado = dispEmp;
                        listspinner.setEnabled(false);
                    }
                }else{
                    Log.d("Adaptador Bluetooth","Nulo");
                }
                break;
            case R.id.desconectar:
                if (conectBluetooth.getConnSocketBt() != null){
                    conectBluetooth.desconectar();
                    if (!listspinner.isEnabled()){
                        listspinner.setEnabled(true);
                    }
                }
                break;
        }

    }

    public ConectBluetooth getConectBluetooth() {
        return conectBluetooth;
    }

    public void setConectBluetooth(ConectBluetooth conectBluetooth) {
        this.conectBluetooth = conectBluetooth;
    }
}
