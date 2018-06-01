package com.example.rafaelmadrid.fitnesscontroller;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
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
import android.widget.AdapterView;
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
    private Button cButton;
    private me.itangqi.waveloadingview.WaveLoadingView waveLoadingView;
    private List<rutina> items;
    private Spinner spinner;

    private Handler recibeDatos;
    private StringBuilder constructor = new StringBuilder();
    private final int datoEnviado = 0;
    private comunicacion comm;

    private Repeticion espera = new Repeticion();
    private IEsperandoRepeticion espRep = new IEsperandoRepeticion();
    private IRepeticionCompleta repComp = new IRepeticionCompleta();
    private int waveViewContador = 0;
    private int series = 0;
    private ProgressDialog mDialog;
    private MediaPlayer mp;

    public static abstract class AnonymousNestedClass{
        public abstract void finalizaSerie();
    }


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
        button=(Button)getActivity().findViewById(R.id.cap_r);
        button.setEnabled(false);
        cButton = (Button)getActivity().findViewById(R.id.cancel);
        cButton.setEnabled(false);

        mp = MediaPlayer.create(getActivity(), R.raw.executeendtask);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cButton.setEnabled(true);
                button.setEnabled(false);
                comm = new comunicacion(conectBluetooth.getConnSocketBt(), getActivity().getBaseContext(), recibeDatos, datoEnviado);
                comm.start();
                series = series + 1;
                waveLoadingView.setBottomTitle("Serie: "+ series +" | "+items.get(spinner.getSelectedItemPosition()).getSeries());
                waveLoadingView.setVisibility(View.VISIBLE);
            }
        });

        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm.destroy();
                comm = null;
                recibeDatos = null;
                waveViewContador = 0;
                cButton.setEnabled(false);
                button.setEnabled(false);
                reset();
            }
        });

        final AnonymousNestedClass execute = new AnonymousNestedClass() {
            @Override
            public void finalizaSerie() {
                mp.start();
                cButton.setEnabled(false);
                button.setEnabled(false);
                waveLoadingView.setBottomTitle("Serie: "+ waveViewContador+" | "+items.get(spinner.getSelectedItemPosition()).getSeries());
                Log.d("FINALIZADO","TERMINO SERIE");
                mDialog = new ProgressDialog(getActivity());
                mDialog.setTitle("Serie Terminada");
                mDialog.setMessage("Esperando iniciar siguiente serie");
                mDialog.show();
                new SerieCompleta().execute();
            }
        };

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
                            if (datos.equals("1")){
                                espera.setEstado(repComp);
                                espera.antesDeEjecutarse();
                                int est = espera.waveView;
                                Log.d("Estado de State",""+est);
                            }else{
                                if(espera.repeticion){
                                    int est = espera.waveView;
                                    waveViewContador += est;
                                    int i = waveViewContador*100/items.get(spinner.getSelectedItemPosition()).getRepeticiones();
                                    waveLoadingView.setProgressValue(i+1);
                                    waveLoadingView.setCenterTitle(String.valueOf(waveViewContador));
                                    Log.d("Estado de State 2", ""+waveViewContador);
                                    if(waveViewContador==items.get(spinner.getSelectedItemPosition()).getRepeticiones()){
                                        execute.finalizaSerie();
                                        waveLoadingView.setBottomTitle("Serie: "+ series +" | "+items.get(spinner.getSelectedItemPosition()).getSeries());
                                    }
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

        ArrayAdapter<String> nuevoadapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, listarutinas());
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
            cButton.setEnabled(true);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reset();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void reset() {
        waveViewContador = 0;
        series = 0;
        waveLoadingView.setVisibility(View.INVISIBLE);
        waveLoadingView.setProgressValue(0);
        waveLoadingView.setBottomTitle("Serie: 0 | 0");
        waveLoadingView.setCenterTitle("0");
        if(items.size()!=0) {
            button.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            //comm = new comunicacion(conectBluetooth.getConnSocketBt(), getActivity().getBaseContext(), recibeDatos, datoEnviado);
            //comm.start();
        }catch (NullPointerException npe){
            Log.e("Null Pointer PP",npe.getMessage());
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

    public class SerieCompleta extends AsyncTask<Integer, String, String>{

        @Override
        protected String doInBackground(Integer... integers) {
            float segundos = 4.5f;
            float contador = 0.5f;
            if (waveViewContador == items.get(spinner.getSelectedItemPosition()).getRepeticiones() && series == items.get(spinner.getSelectedItemPosition()).getSeries()){
                return "Rutina terminada";
            }else{
                try {
                    while (contador <= segundos){
                        Thread.sleep(10000);
                        Log.e("Estado", ""+contador);
                        contador = contador+0.5f;
                    }
                } catch (InterruptedException e) {
                    return "Se produjo un error";
                }
            }

            return "Iniciando siguiente serie";
        }

        @Override
        protected void onPostExecute(String s) {
            mDialog.dismiss();
            if (s.equalsIgnoreCase("Rutina terminada")){
                waveLoadingView.setCenterTitle(String.valueOf("Rutina Completa"));
                new AlertDialog.Builder(getActivity())
                        .setTitle("Rutina Completa")
                        .setMessage("Has finalizado tu rutina")
                        .setPositiveButton(R.string.acept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reset();
                            }
                        })
                        .create()
                        .show();
            }else{
                waveViewContador = 0;
                series = series + 1;
                waveLoadingView.setBottomTitle("Serie: "+ series +" | "+items.get(spinner.getSelectedItemPosition()).getSeries());
            }
            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        }
    }
}
