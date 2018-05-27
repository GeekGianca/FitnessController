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
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import Data.rutina;

public class PaginaPrincipal extends Fragment {

    private ConectBluetooth conectBluetooth=new ConectBluetooth(getActivity(),BluetoothAdapter.getDefaultAdapter());;
    private Button button;
    private comunicacion comunicacion;
    private me.itangqi.waveloadingview.WaveLoadingView waveLoadingView;
    private List<rutina> items;
    private Spinner spinner;
    int rep=0;
    int muestraRepeticiones = 0;
    int series=0;
    private rutina rutina;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.principal_lay_fragment,null);

    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView nombredisp=getActivity().findViewById(R.id.Nombredispcon);
        TextView Mac=getActivity().findViewById(R.id.mac);
        button=getActivity().findViewById(R.id.cap_r);
        button.setEnabled(false);

        BluetoothAdapter myBluetoothadapter=BluetoothAdapter.getDefaultAdapter();
        waveLoadingView=getActivity().findViewById(R.id.repeticiones);
        waveLoadingView.setVisibility(View.INVISIBLE);
        spinner=getActivity().findViewById(R.id.spinerlista);
        ArrayAdapter nuevoadapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, listarutinas());
        spinner.setAdapter(nuevoadapter);
        if(myBluetoothadapter==null){
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
        }else {
            if (!myBluetoothadapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
            if (conectBluetooth.getBtSocket()==null) {
                nombredisp.setText(nombredisp.getText() + ": Sin Conexion");
                Mac.setText(Mac.getText() + " Sin Conexion");
                if(items.size()==0){
                    spinner.setEnabled(false);
                }
            } else {
                nombredisp.setText(nombredisp.getText() + ": " + conectBluetooth.getBluetoothDevice().getName());
                Mac.setText(Mac.getText() + " " + conectBluetooth.getBluetoothDevice().getAddress());
                comunicacion=conectBluetooth.getComunicacion();
                if(items.size()!=0 && conectBluetooth.getBtSocket().isConnected()) {
                    button.setEnabled(true);
                }
            }
        }

        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.setTitle("Pagina Principal");
        iniciarrutina();
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

    public void iniciarrutina(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comunicacion = conectBluetooth.getComunicacion();
                waveLoadingView.setVisibility(View.VISIBLE);
                new CargarRepeticiones().execute();
            }
        });

    }

    public void conteo(){
        comunicacion.doInBackground(null);
        comunicacion.onPostExecute(null);
    }

    public ConectBluetooth getConectBluetooth() {
        return conectBluetooth;
    }

    public void setConectBluetooth(ConectBluetooth conectBluetooth) {
        this.conectBluetooth = conectBluetooth;
    }

    private class CargarRepeticiones extends AsyncTask<Integer, String, String>{

        @Override
        protected String doInBackground(Integer... integers) {

            Log.e("ENTRO AL BCKGND", "UNA VEZ");
            String retorno = "";
            rutina = items.get(spinner.getSelectedItemPosition());
            while (rep <= rutina.getRepeticiones()) {
                //comunicacion = conectBluetooth.getComunicacion();
                conteo();
                /*rep += 1;
                if (rep == muestraRepeticiones+3){
                    rep =+ 1;

                    Log.d("Contador Rep", ""+muestraRepeticiones);
                }*/
                Log.d("Repeticion", "" + Common.estaRepitiendo);
                Log.d("Cantidad", "" + rutina.getRepeticiones());
                retorno = "Repeticion realizada";
                publishProgress(String.valueOf(Common.estaRepitiendo), retorno);
            }
            return "Completo";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            int repet = Integer.parseInt(values[0]);
            waveLoadingView.setProgressValue(comunicacion.subemarea(repet, rutina.getRepeticiones()));
            waveLoadingView.setCenterTitle(String.valueOf(repet));
            Toast.makeText(getActivity().getBaseContext(),values[1], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Aqui muestran el final de la repeticion
        }
    }
}
