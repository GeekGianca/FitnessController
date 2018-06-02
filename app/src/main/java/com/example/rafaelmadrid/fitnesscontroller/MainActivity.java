package com.example.rafaelmadrid.fitnesscontroller;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Data.Rutina;

public class MainActivity extends AppCompatActivity {

    private List<Rutina> items;

    private BluetoothAdapter adaptador;
    private ConectBluetooth conectBluetooth;
    private PaginaPrincipal principal;
    private Settings settingspage;
    private PaginaRutinas paginaRutinas;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_Principal:
                    principal.setItems(items);
                    conectBluetooth=settingspage.getConectBluetooth();
                    principal.setConectBluetooth(conectBluetooth);
                    fragmentTransaction.replace(R.id.container, principal);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_listarutinas:
                    paginaRutinas.setItems(items);
                    fragmentTransaction.replace(R.id.container, paginaRutinas);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_Settings:
                    conectBluetooth=principal.getConectBluetooth();
                    settingspage.setConectBluetooth(conectBluetooth);
                    fragmentTransaction.replace(R.id.container, settingspage);
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_tittle));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        principal=new PaginaPrincipal();
        settingspage=new Settings();
        paginaRutinas=new PaginaRutinas();

        items=new ArrayList<>();

        BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        MenuItem item=navigation.getMenu().getItem(0);
        navigation.setSelectedItemId(item.getItemId());

        adaptador = BluetoothAdapter.getDefaultAdapter();
        if (adaptador == null){
            Toast.makeText(this, "Este dispositivo no soporta el Bluetooth", Toast.LENGTH_LONG).show();
        }else{
            verificaBluetooth();
        }
        Utilidad.cargarDispositivos(adaptador);
        mOnNavigationItemSelectedListener.onNavigationItemSelected(item);
    }

    private void verificaBluetooth() {
        if (!adaptador.isEnabled()){
            Intent habilitarBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(habilitarBluetooth, 1);
        }else{

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && adaptador.isEnabled()){
            Toast.makeText(this, "Bluetooth Encendido", Toast.LENGTH_LONG).show();
        }
    }
}
