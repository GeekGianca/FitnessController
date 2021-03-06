package com.example.rafaelmadrid.fitnesscontroller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import Data.Archivo_Rutina;
import Data.Rutina;

public class PaginaRutinas extends Fragment {

    private List<Rutina> items;
    private View dialogView;
    private AlertDialog builder;
    private Archivo_Rutina archivo_rutina;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rutina_lay_fragment, null);
    }

    public boolean addr(Menu menu) {
        MenuItem menuItem = menu.getItem(0);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                formulario();
                alertdialog();
                return false;
            }
        });
        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Lista de rutinas");
        Menu menu = toolbar.getMenu();
        if (menu.size() == 0) {
            toolbar.inflateMenu(R.menu.toolbarmenu);
        }
        addr(menu);
        recycler();
        archivo_rutina=new Archivo_Rutina(getActivity().getBaseContext());
    }


    public void recycler() {
        RecyclerView recyclerView = getActivity().findViewById(R.id.reciclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        com.example.rafaelmadrid.fitnesscontroller.cardviewAdapter cardviewAdapter = new cardviewAdapter(items);
        recyclerView.setAdapter(cardviewAdapter);
    }


    public void setItems(List<Rutina> items) {
        this.items = items;
    }

    public void formulario() {
        dialogView=View.inflate(getActivity(),R.layout.agregar_rutina,null);
    }

    public void alertdialog(){
        String titulo="Agregar Rutina";
        final AlertDialog.Builder Alertdialog = new AlertDialog.Builder(getActivity());
            Alertdialog.setView(dialogView)
                    .setTitle(titulo)
                    .setPositiveButton("Aceptar",null)
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder=Alertdialog.create();
            builder.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button b=builder.getButton(DialogInterface.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            check_out();
                        }
                    });
                }
            });
            builder.show();
    }

    public void check_out(){
        int i=0;
        String Nombre="";
        int series=0;
        int rep=0;
        EditText campo=null;
        Rutina rutina;
        boolean pass=true;
        rutina=new Rutina();
        do{
            switch (i){
                case 0:
                    campo=dialogView.findViewById(R.id.agg_Nombre_rut);
                    break;
                case 1:
                    campo=dialogView.findViewById(R.id.agg_Numero_rep);
                    break;
                case 2:
                    campo=dialogView.findViewById(R.id.agg_Series_rut);
                    break;
            }
            if(!campo.getText().toString().isEmpty()){
               switch (i){
                   case 0:
                       Nombre=campo.getText().toString();
                       break;
                   case 1:
                       rep=Integer.parseInt(campo.getText().toString());
                       break;
                   case 2:
                       series=Integer.parseInt(campo.getText().toString());
                       break;
               }
            }else{
               pass=false;
               campo.setError("Campo Obligatorio");
            }
            i++;
        }while(i<3);
        if(pass){
            rutina.setNombre(Nombre);
            rutina.setSeries(series);
            rutina.setRepeticiones(rep);
            items.add(rutina);
            archivo_rutina.Guardar(items);
            builder.dismiss();
        }
    }


}


