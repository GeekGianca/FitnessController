package com.example.rafaelmadrid.fitnesscontroller;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rafaelmadrid.fitnesscontroller.State.Estado;
import com.example.rafaelmadrid.fitnesscontroller.State.IEsperandoRepeticion;
import com.example.rafaelmadrid.fitnesscontroller.State.IRepeticionCompleta;
import com.example.rafaelmadrid.fitnesscontroller.State.Repeticion;

import java.util.List;

import Data.rutina;

public class PaginaPrincipal extends Fragment {

    private ConectBluetooth conectBluetooth;
    private Button button;
    private comunicacion comunicacion;
    private me.itangqi.waveloadingview.WaveLoadingView waveLoadingView;
    private List<rutina> items;
    private Spinner spinner;

    private Handler recibeDatos;
    private StringBuilder constructor = new StringBuilder();
    private final int datoEnviado = 0;
    private comunicacion comm;

    private rutina rutina;
    private Repeticion espera = new Repeticion();
    private IEsperandoRepeticion espRep = new IEsperandoRepeticion();
    private IRepeticionCompleta repComp = new IRepeticionCompleta();
    private int waveViewContador = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.principal_lay_fragment,null);

    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.setTitle("Pagina Principal");

        TextView nombredisp=getActivity().findViewById(R.id.Nombredispcon);
        TextView Mac=getActivity().findViewById(R.id.mac);
        button=getActivity().findViewById(R.id.cap_r);
        button.setEnabled(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comunicacion = new comunicacion(conectBluetooth.getConnSocketBt(), getActivity().getBaseContext(), recibeDatos, datoEnviado);
                comunicacion.start();
                waveLoadingView.setVisibility(View.VISIBLE);
            }
        });

        recibeDatos = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                    if (msg.what == datoEnviado){
                        String leer = (String)msg.obj;
                        constructor.append(leer);
                        int finalLinea = constructor.indexOf("+");
                        Log.d("Constructor",constructor.toString());
                        if (finalLinea > 0){
                            String datos = constructor.substring(0, finalLinea);
                            Log.d("DATOS",datos);
                            constructor.delete(0, constructor.length());
                            if (datos.equals("Repeticion completa")){
                                espera.setEstado(repComp);
                                espera.antesDeEjecutarse();
                                int est = espera.waveView;
                                Log.d("Estado de State",""+est);
                            }else{
                                if(espera.repeticion){
                                    int est = espera.waveView;
                                    waveViewContador += est;
                                    int i = waveViewContador*100/items.get(spinner.getSelectedItemPosition()).getRepeticiones();
                                    waveLoadingView.setProgressValue(i);
                                    waveLoadingView.setCenterTitle(String.valueOf(waveViewContador));
                                    Log.d("Estado de State 2", ""+waveViewContador);
                                }
                                espera.setEstado(espRep);
                                espera.antesDeEjecutarse();
                            }
                        }
                    }

            }
        };

        waveLoadingView=getActivity().findViewById(R.id.repeticiones);
        waveLoadingView.setVisibility(View.INVISIBLE);
        spinner=getActivity().findViewById(R.id.spinerlista);

        ArrayAdapter nuevoadapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, listarutinas());
        spinner.setAdapter(nuevoadapter);
        if (Utilidad.defaultBluetooth() == null){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Error")
                    .setMessage("Dispositivo no Compatible")
                    .setPositiveButton(R.string.acept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            conectBluetooth=null;
                            getActivity().finish();
                        }
                    })
                    .create()
                    .show();
        }else{
            if (!Utilidad.defaultBluetooth().isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }else{
                if (Utilidad.dispositivoConectado != null){
                    nombredisp.setText(String.valueOf("Nombre del dispositivo: "+Utilidad.dispositivoConectado.getNombre()));
                    Mac.setText(String.valueOf("Mac del dispositivo: "+Utilidad.dispositivoConectado.getMac()));
                }
            }
        }

        if(items.size()!=0) {
            button.setEnabled(true);

        }


    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            comm = new comunicacion(conectBluetooth.getConnSocketBt(), getActivity().getBaseContext(), recibeDatos, datoEnviado);
            comm.start();
        }catch (NullPointerException npe){
            Log.e("Null Pointer",npe.getMessage());
        }
    }

    public void setItems(List<rutina> items) {
        this.items = items;
    }

    public String[] listarutinas(){
        String lista[];
        if(items.size()==0){
            lista=new String[1];
            lista[0]="No hay perfiles";
        }else{
            lista=new String[items.size()];
            int i=0;
            do{
                lista[i]=items.get(i).getNombre();
                i++;
            }while(i<items.size());
        }
        return lista;
    }


    public void setConectBluetooth(ConectBluetooth conectBluetooth) {
        this.conectBluetooth = conectBluetooth;
    }

    public ConectBluetooth getConectBluetooth() {
        return conectBluetooth;
    }

}
