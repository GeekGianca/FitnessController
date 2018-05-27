package com.example.rafaelmadrid.fitnesscontroller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import Data.Archivo_Rutina;
import Data.rutina;

public class MainActivity extends AppCompatActivity {

    private List<rutina> items;

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

    private ConectBluetooth conectBluetooth;
    private PaginaPrincipal principal;
    private Settings settingspage;
    private PaginaRutinas paginaRutinas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        principal=new PaginaPrincipal();
        settingspage=new Settings(principal.getConectBluetooth());
        paginaRutinas=new PaginaRutinas();
        items=new ArrayList<>();
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_tittle));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        MenuItem item=navigation.getMenu().getItem(0);
        navigation.setSelectedItemId(item.getItemId());
        mOnNavigationItemSelectedListener.onNavigationItemSelected(item);

    }

}
